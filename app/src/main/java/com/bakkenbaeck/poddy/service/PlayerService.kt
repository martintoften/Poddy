package com.bakkenbaeck.poddy.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import com.bakkenbaeck.poddy.extensions.getEpisodePath
import com.bakkenbaeck.poddy.notification.PlayerNotificationHandler
import com.bakkenbaeck.poddy.presentation.mappers.mapToViewEpisodeFromDB
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction
import com.bakkenbaeck.poddy.repository.PodcastRepository
import com.bakkenbaeck.poddy.repository.QueueRepository
import com.bakkenbaeck.poddy.util.PlayerQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

const val EPISODE = "EPISODE"

const val ACTION_START = "action_start"
const val ACTION_PLAY = "action_play"
const val ACTION_PAUSE = "action_pause"
const val ACTION_REWIND = "action_rewind"
const val ACTION_FAST_FORWARD = "action_fast_forward"
const val ACTION_SEEK_TO = "action_seek_to"

class PlayerService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val playerNotificationHandler by lazy { PlayerNotificationHandler(this) }
    private val queueRepository by inject<QueueRepository>()
    private val podcastRepository by inject<PodcastRepository>()

    private val scope by lazy { CoroutineScope(Dispatchers.Main) }

    private val playerChannel by inject<ConflatedBroadcastChannel<ViewPlayerAction>>(named("playerChannel"))
    private val tickerChannel by lazy { ticker(delayMillis = 1000, context = Dispatchers.Main) }

    private val playerQueue = PlayerQueue()
    private val playerActionBuilder = PlayerActionBuilder(playerQueue)
    private var isListenerInitialised = false

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        listenForProgressUpdates()
        listenForPlayerAction()
    }

    private fun listenForProgressUpdates() {
        scope.launch {
            for (event in tickerChannel) {
                if (mediaPlayer?.isPlaying == true) {
                    broadcastProgress()
                }
            }
        }
    }

    private suspend fun broadcastProgress() {
        val episode = playerQueue.current() ?: return
        val progressInMillis = mediaPlayer?.currentPosition ?: 0
        val durationInMillis = mediaPlayer?.duration ?: 0
        val action = ViewPlayerAction.Progress(episode, progressInMillis, durationInMillis)
        playerChannel.send(action)
        podcastRepository.updateProgress(episode.id, progressInMillis.toLong())
    }

    private fun listenForPlayerAction() {
        scope.launch {
            playerChannel.asFlow()
                .collect { handlePlayerAction(it) }
        }
    }

    private fun handlePlayerAction(playerAction: ViewPlayerAction) {
        when (playerAction) {
            is ViewPlayerAction.Start -> initPlayerAndNotification(playerAction.episode)
            is ViewPlayerAction.Play -> onPlay()
            is ViewPlayerAction.Pause -> onPause()
        }
    }

    private fun initQueueListener() {
        if (isListenerInitialised) return
        isListenerInitialised = true

        listenForQueueUpdates()
        getQueue()
    }

    private fun listenForQueueUpdates() {
        scope.launch {
            queueRepository.listenForQueueUpdates()
                .flowOn(Dispatchers.IO)
                .map { mapToViewEpisodeFromDB(it) }
                .collect { handleQueue(it) }
        }
    }

    private fun handleQueue(episodes: List<ViewEpisode>) {
        playerQueue.updateQueue(episodes)

        if (!playerQueue.hasCurrent()) {
            val nextEpisode = playerQueue.first() ?: return
            buildAndBroadcastAction(ACTION_START, nextEpisode)
        }
    }

    private fun getQueue() {
        scope.launch {
            queueRepository.getQueue()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { handleIntent(it) }
        return START_NOT_STICKY
    }

    private fun handleIntent(intent: Intent) {
        val action = intent.action ?: return
        val episode = intent.getParcelableExtra<ViewEpisode?>(EPISODE)

        handleAction(action, episode)
        buildAndBroadcastAction(action, episode)
    }

    private fun handleAction(action: String, episode: ViewEpisode?) {
        if (action == ACTION_SEEK_TO && episode != null) {
            seekTo(episode.progress.toInt())
        }
    }

    private fun buildAndBroadcastAction(action: String, episode: ViewEpisode?) {
        val playerAction = buildAction(action, episode) ?: return

        scope.launch {
            broadcastAction(playerAction)
            initQueueListener()
        }
    }

    private fun buildAction(action: String, episode: ViewEpisode?): ViewPlayerAction? {
        return when (action) {
            ACTION_START -> playerActionBuilder.getStartAction(episode, mediaPlayer?.isPlaying == true)
            ACTION_PLAY -> playerActionBuilder.getPlayAction()
            ACTION_PAUSE -> playerActionBuilder.getPauseAction()
            else -> null
        }
    }

    private suspend fun broadcastAction(action: ViewPlayerAction) {
        when (action) {
            is ViewPlayerAction.Start -> {
                queueRepository.addToQueue(action.episode)
                playerQueue.setCurrent(action.episode)
                playerChannel.send(action)
            }
            is ViewPlayerAction.Pause, is ViewPlayerAction.Play -> playerChannel.send(action)
            else -> Log.d("PlayerService", "Invalid action at this stage")
        }
    }

    private fun initPlayerAndNotification(episode: ViewEpisode) {
        val podcastPath = episode.getEpisodePath(this)
        initMediaPlayer(episode, podcastPath, { onStart(episode) }, { onFinished() })
        playerNotificationHandler.createChannel()
    }

    private fun onFinished() {
        val currentEpisode = playerQueue.current() ?: return
        playerQueue.clearCurrentEpisode()

        scope.launch {
            queueRepository.deleteEpisodeFromQueue(currentEpisode.id)
        }
    }

    private fun onStart(episode: ViewEpisode) {
        val action = playerNotificationHandler.generatePauseAction()
        playerNotificationHandler.buildNotification(episode.title, action)
    }

    private fun onPlay() {
        val episode = playerQueue.current() ?: return
        val action = playerNotificationHandler.generatePauseAction()
        playerNotificationHandler.buildNotification(episode.title, action)
        mediaPlayer?.start()
    }

    private fun onPause() {
        val episode = playerQueue.current() ?: return
        val action = playerNotificationHandler.generatePlayAction()
        playerNotificationHandler.buildNotification(episode.title, action)
        mediaPlayer?.pause()
    }

    private fun initMediaPlayer(episode: ViewEpisode, path: String, onStartListener: () -> Unit, onCompletedListener: () -> Unit) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }

        mediaPlayer?.reset()
        mediaPlayer?.setDataSource(path)
        mediaPlayer?.setOnPreparedListener {
            onStartListener()
            mediaPlayer?.seekTo(episode.progress.toInt())
            mediaPlayer?.start()
        }
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnCompletionListener { onCompletedListener() }
    }

    private fun seekTo(progressInPercent: Int) {
        val progress = (mediaPlayer?.duration ?: 0) * (progressInPercent.toDouble() / 100)
        mediaPlayer?.seekTo(progress.toInt())
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        tickerChannel.cancel()
        playerChannel.cancel()
        super.onDestroy()
    }
}
