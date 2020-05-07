package com.bakkenbaeck.poddy.presentation.player

import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction
import com.bakkenbaeck.poddy.presentation.notification.PlayerNotificationHandler
import com.bakkenbaeck.poddy.repository.ProgressRepository
import com.bakkenbaeck.poddy.useCase.AddToQueueUseCase
import com.bakkenbaeck.poddy.useCase.DeleteQueueUseCase
import com.bakkenbaeck.poddy.useCase.QueueFlowUseCase
import com.bakkenbaeck.poddy.util.EpisodePathHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

const val EPISODE = "EPISODE"

const val ACTION_START = "action_start"
const val ACTION_PLAY = "action_play"
const val ACTION_PAUSE = "action_pause"
const val ACTION_REWIND = "action_rewind"
const val ACTION_FAST_FORWARD = "action_fast_forward"
const val ACTION_SEEK_TO = "action_seek_to"
const val ACTION_NOTIFICATION_DISMISSED = "action_notification_dismissed"

class PlayerHandler(
    private val progressRepository: ProgressRepository,
    private val playerChannel: ConflatedBroadcastChannel<ViewPlayerAction?>,
    private val playerNotificationHandler: PlayerNotificationHandler,
    private val podcastPlayer: PodcastPlayer,
    private val episodeHelper: EpisodePathHelper,
    private val playerQueue: PlayerQueue,
    private val queueFlowUseCase: QueueFlowUseCase,
    private val addToQueueUseCase: AddToQueueUseCase,
    private val deleteQueueUseCase: DeleteQueueUseCase,
    private val playerActionBuilder: PlayerActionBuilder = PlayerActionBuilder(playerQueue),
    private val backgroundContext: CoroutineContext = Dispatchers.IO,
    tickerContext: CoroutineContext = Dispatchers.Main,
    mainContext: CoroutineContext = Dispatchers.Main
) {
    private var isQueueListenerInitialised = false
    private val scope = CoroutineScope(mainContext)
    private val tickerChannel = ticker(delayMillis = 1000, context = tickerContext)

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
            playerChannel.send(null) // Make sure the channel is cleared when starting a new session.
            playerChannel.asFlow()
                .filterNotNull()
                .collect { handlePlayerAction(it) }
        }
    }

    private fun handlePlayerAction(playerAction: ViewPlayerAction) {
        if (playerAction is ViewPlayerAction.Progress) return

        when (playerAction) {
            is ViewPlayerAction.Start -> loadPlayerAndNotification(playerAction.episode)
            is ViewPlayerAction.Play -> onPlay()
            is ViewPlayerAction.Pause -> onPause()
        }
    }

    private fun loadPlayerAndNotification(episode: ViewEpisode) {
        val podcastPath = episodeHelper.getPath(episode)
        podcastPlayer.load(episode, podcastPath, { onStart(episode) }, { onFinished() })
        playerNotificationHandler.initNotification(episode.podcastTitle.orEmpty(), episode.title)
    }

    private fun onStart(episode: ViewEpisode) {
        playerNotificationHandler.showPauseNotification(episode.podcastTitle.orEmpty(), episode.title)
    }

    private fun onFinished() {
        val currentEpisode = playerQueue.current() ?: return
        playerQueue.clearCurrentEpisode()

        scope.launch {
            deleteQueueUseCase.execute(currentEpisode.id)
        }
    }

    private fun onPlay() {
        val episode = playerQueue.current() ?: return
        playerNotificationHandler.showPauseNotification(episode.podcastTitle.orEmpty(), episode.title)
        podcastPlayer.start()
    }

    private fun onPause() {
        val episode = playerQueue.current() ?: return
        playerNotificationHandler.showPlayNotification(episode.podcastTitle.orEmpty(), episode.title)
        podcastPlayer.pause()
    }

    fun handlePlayerAction(action: String?, episode: ViewEpisode?) {
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
        val playerAction = buildAction(action, episode) ?: return // Invalid action

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
                addToQueueUseCase.execute(action.episode)
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
    }

    private fun listenForQueueUpdates() {
        scope.launch {
            queueFlowUseCase.execute()
                .flowOn(backgroundContext)
                .collect { handleQueueUpdate(it) }
        }
    }

    private fun handleQueueUpdate(episodes: List<ViewEpisode>) {
        playerQueue.updateQueue(episodes)

        if (!playerQueue.hasCurrent()) {
            val nextEpisode = playerQueue.first() ?: return
            buildAndBroadcastAction(ACTION_START, nextEpisode)
        }
    }

    fun destroy() {
        playerQueue.clearCurrentEpisode()
        podcastPlayer.destroy()
        tickerChannel.cancel()
        scope.cancel()
    }
}
