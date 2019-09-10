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

class PlayerService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val playerNotificationHandler by lazy { PlayerNotificationHandler(this) }
    private val queueRepository by inject<QueueRepository>()

    private val scope by lazy { CoroutineScope(Dispatchers.Main) }

    private val playerChannel by inject<ConflatedBroadcastChannel<ViewPlayerAction>>(named("playerChannel"))
    private val tickerChannel by lazy { ticker(delayMillis = 1000, context = Dispatchers.Main) }

    private val playerQueue = PlayerQueue()

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        initProgress()
        listenForPlayerAction()
        listenForQueueUpdates()
        getQueue()
    }

    private fun listenForQueueUpdates() {
        scope.launch {
            queueRepository.listenForQueueUpdates()
                .flowOn(Dispatchers.IO)
                .map { mapToViewEpisodeFromDB(it) }
                .collect { playerQueue.updateQueue(it) }
        }
    }

    private fun getQueue() {
        scope.launch {
            queueRepository.getQueue()
        }
    }

    private fun initProgress() {
        scope.launch {
            for (event in tickerChannel) {
                broadcastProgress()
            }
        }
    }

    private suspend fun broadcastProgress() {
        val episode = playerQueue.current() ?: return
        val progressInMillis = mediaPlayer?.currentPosition ?: 0
        val durationInMillis = mediaPlayer?.duration ?: 0
        playerChannel.send(ViewPlayerAction.Progress(episode, progressInMillis, durationInMillis))
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { handleIntent(it) }
        return START_NOT_STICKY
    }

    private fun handleIntent(intent: Intent) {
        val action = intent.action ?: return
        val episode = intent.getParcelableExtra<ViewEpisode?>(EPISODE)

        broadcastAction(action, episode)
    }

    private fun broadcastAction(action: String, episode: ViewEpisode?) {
        scope.launch {
            when (action) {
                ACTION_START -> broadcastStartAction(episode)
                ACTION_PLAY -> broadcastPlayAction()
                ACTION_PAUSE -> broadcastPauseAction()
                else -> Log.d("PlayerService", "Invalid intent action")
            }
        }
    }

    private suspend fun broadcastStartAction(episode: ViewEpisode?) {
        if (episode == null) throw IllegalStateException("Must pass an episode with the start action")

        if (episode.id == playerQueue.current()?.id) {
            val action = if (mediaPlayer?.isPlaying == true) ViewPlayerAction.Pause(episode)
            else ViewPlayerAction.Play(episode)
            playerChannel.send(action)
        } else {
            playerQueue.setCurrent(episode)
            playerChannel.send(ViewPlayerAction.Start(episode))
        }
    }

    private suspend fun broadcastPauseAction() {
        val episode = playerQueue.current() ?: return
        playerChannel.send(ViewPlayerAction.Pause(episode))
    }

    private suspend fun broadcastPlayAction() {
        val episode = playerQueue.current() ?: return
        playerChannel.send(ViewPlayerAction.Play(episode))
    }

    private fun initPlayerAndNotification(episode: ViewEpisode) {
        val podcastPath = episode.getEpisodePath(this)
        initMediaPlayer(podcastPath) {
            val action = playerNotificationHandler.generatePauseAction()
            playerNotificationHandler.buildNotification(episode.title, action)
        }
        playerNotificationHandler.createChannel()
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

    private fun initMediaPlayer(path: String, onStartListener: () -> Unit) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }

        mediaPlayer?.reset()
        mediaPlayer?.setDataSource(path)
        mediaPlayer?.setOnPreparedListener {
            onStartListener()
            mediaPlayer?.start()
        }
        mediaPlayer?.prepareAsync()
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }
}
