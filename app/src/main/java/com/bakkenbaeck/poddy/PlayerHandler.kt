package com.bakkenbaeck.poddy

import android.content.Intent
import android.util.Log
import com.bakkenbaeck.poddy.extensions.getEpisodePath
import com.bakkenbaeck.poddy.notification.PlayerNotificationHandler
import com.bakkenbaeck.poddy.presentation.mappers.mapToViewEpisodeFromDB
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction
import com.bakkenbaeck.poddy.repository.PodcastRepository
import com.bakkenbaeck.poddy.repository.QueueRepository
import com.bakkenbaeck.poddy.service.*
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

const val EPISODE = "EPISODE"

const val ACTION_START = "action_start"
const val ACTION_PLAY = "action_play"
const val ACTION_PAUSE = "action_pause"
const val ACTION_REWIND = "action_rewind"
const val ACTION_FAST_FORWARD = "action_fast_forward"
const val ACTION_SEEK_TO = "action_seek_to"

class PlayerHandler(
    private val queueRepository: QueueRepository,
    private val podcastRepository: PodcastRepository,
    private val playerChannel: ConflatedBroadcastChannel<ViewPlayerAction>,
    private val playerNotificationHandler: PlayerNotificationHandler
) {

    private var isQueueListenerInitialised = false

    private val scope by lazy { CoroutineScope(Dispatchers.Main) }
    private val podcastPlayer by lazy { PodcastPlayer() }

    private val tickerChannel by lazy { ticker(delayMillis = 1000, context = Dispatchers.Main) }
    private val playerQueue by lazy { PlayerQueue() }
    private val playerActionBuilder by lazy { PlayerActionBuilder(playerQueue) }

    fun listenForProgressUpdates() {
        scope.launch {
            for (event in tickerChannel) {
                if (podcastPlayer.isPlaying()) {
                    broadcastProgress()
                }
            }
        }
    }

    private suspend fun broadcastProgress() {
        val episode = playerQueue.current() ?: return
        val (progress, duration) = podcastPlayer.getProgressAndDuration()
        val action = ViewPlayerAction.Progress(episode, progress, duration)
        playerChannel.send(action)
        podcastRepository.updateProgress(episode.id, progress.toLong())
    }

    fun listenForPlayerAction() {
        scope.launch {
            playerChannel.asFlow()
                .collect { handlePlayerAction(it) }
        }
    }

    private fun handlePlayerAction(playerAction: ViewPlayerAction) {
        when (playerAction) {
            is ViewPlayerAction.Start -> loadPlayerAndNotification(playerAction.episode)
            is ViewPlayerAction.Play -> onPlay()
            is ViewPlayerAction.Pause -> onPause()
        }
    }

    private fun initQueueListener() {
        if (isQueueListenerInitialised) return
        isQueueListenerInitialised = true

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

    fun handleIntent(intent: Intent) {
        val action = intent.action ?: return
        val episode = intent.getParcelableExtra<ViewEpisode?>(EPISODE)

        handleAction(action, episode)
        buildAndBroadcastAction(action, episode)
    }

    private fun handleAction(action: String, episode: ViewEpisode?) {
        if (action == ACTION_SEEK_TO && episode != null) {
            podcastPlayer.seekTo(episode.progress.toInt())
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
            ACTION_START -> playerActionBuilder.getStartAction(episode, podcastPlayer.isPlaying())
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

    private fun loadPlayerAndNotification(episode: ViewEpisode) {
        val podcastPath = episode.getEpisodePath()
        podcastPlayer.load(episode, podcastPath, { onStart(episode) }, { onFinished() })
        playerNotificationHandler.initNotification(episode.title)
    }

    private fun onFinished() {
        val currentEpisode = playerQueue.current() ?: return
        playerQueue.clearCurrentEpisode()

        scope.launch {
            queueRepository.deleteEpisodeFromQueue(currentEpisode.id)
        }
    }

    private fun onStart(episode: ViewEpisode) {
        playerNotificationHandler.showPauseNotification(episode.title)
    }

    private fun onPlay() {
        val episode = playerQueue.current() ?: return
        playerNotificationHandler.showPauseNotification(episode.title)
        podcastPlayer.start()
    }

    private fun onPause() {
        val episode = playerQueue.current() ?: return
        playerNotificationHandler.showPlayNotification(episode.title)
        podcastPlayer.pause()
    }

    fun onDestroy() {
        podcastPlayer.release()
        tickerChannel.cancel()
        playerChannel.cancel()
    }
}
