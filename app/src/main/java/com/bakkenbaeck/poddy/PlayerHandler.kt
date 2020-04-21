package com.bakkenbaeck.poddy

import com.bakkenbaeck.poddy.notification.PlayerNotificationHandler
import com.bakkenbaeck.poddy.presentation.mappers.mapToViewEpisodeFromDB
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction
import com.bakkenbaeck.poddy.repository.ProgressRepository
import com.bakkenbaeck.poddy.repository.QueueRepository
import com.bakkenbaeck.poddy.service.PlayerActionBuilder
import com.bakkenbaeck.poddy.util.EpisodePathHelper
import com.bakkenbaeck.poddy.util.PlayerQueue
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*
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
    private val progressRepository: ProgressRepository,
    private val playerChannel: ConflatedBroadcastChannel<ViewPlayerAction>,
    private val playerNotificationHandler: PlayerNotificationHandler,
    private val podcastPlayer: PodcastPlayer,
    private val mainDispatcher: CoroutineContext,
    private val episodeHelper: EpisodePathHelper,
    private val playerQueue: PlayerQueue
) {
    private var isQueueListenerInitialised = false

    private val scope by lazy { CoroutineScope(Dispatchers.Main) }

    private val tickerChannel by lazy { ticker(delayMillis = 1000, context = mainDispatcher) }
    private val playerActionBuilder by lazy { PlayerActionBuilder(playerQueue) }

    fun init() {
        listenForProgressUpdates()
        listenForPlayerAction()
    }

    private fun listenForProgressUpdates() {
        scope.launch {
            tickerChannel.consumeAsFlow().collect {
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
        progressRepository.updateProgress(episode.id, progress.toLong())
    }

    private fun listenForPlayerAction() {
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

    private fun loadPlayerAndNotification(episode: ViewEpisode) {
        val podcastPath = episodeHelper.getPath(episode)
        podcastPlayer.load(episode, podcastPath, { onStart(episode) }, { onFinished() })
        playerNotificationHandler.initNotification(episode.title)
    }

    private fun onStart(episode: ViewEpisode) {
        playerNotificationHandler.showPauseNotification(episode.title)
    }

    private fun onFinished() {
        val currentEpisode = playerQueue.current() ?: return
        playerQueue.clearCurrentEpisode()

        scope.launch {
            queueRepository.deleteEpisodeFromQueue(currentEpisode.id)
        }
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

    fun handleIntent(action: String?, episode: ViewEpisode?) {
        if (action == null) return
        handleAction(action, episode)
        buildAndBroadcastAction(action, episode)
    }

    private fun handleAction(action: String, episode: ViewEpisode?) {
        if (action == ACTION_SEEK_TO && episode != null) {
            podcastPlayer.seekTo(episode.progress.toInt())
        } else if (action == ACTION_REWIND) {
            podcastPlayer.goBack(10000)
        } else if (action == ACTION_FAST_FORWARD) {
            podcastPlayer.goForward(30000)
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
            else -> {} // Log
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

    private fun getQueue() {
        scope.launch {
            queueRepository.getQueue()
        }
    }

    private fun handleQueue(episodes: List<ViewEpisode>) {
        playerQueue.setQueue(episodes)

        if (!playerQueue.hasCurrent()) {
            val nextEpisode = playerQueue.first() ?: return
            buildAndBroadcastAction(ACTION_START, nextEpisode)
        }
    }

    fun destroy() {
        podcastPlayer.destroy()
        tickerChannel.cancel()
        playerChannel.cancel()
        scope.cancel()
    }
}
