package com.bakkenbaeck.poddy.presentation.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.bakkenbaeck.poddy.di.PLAYER_CHANNEL
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction
import com.bakkenbaeck.poddy.presentation.notification.PlayerNotificationHandlerImpl
import com.bakkenbaeck.poddy.presentation.player.*
import com.bakkenbaeck.poddy.repository.ProgressRepository
import com.bakkenbaeck.poddy.useCase.AddToQueueUseCase
import com.bakkenbaeck.poddy.useCase.DeleteQueueUseCase
import com.bakkenbaeck.poddy.useCase.QueueFlowUseCase
import com.bakkenbaeck.poddy.util.EpisodePathHelper
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

class PlayerService : Service() {

    private val playerHandler by lazy {
        val progressRepository by inject<ProgressRepository>()
        val playerChannel by inject<ConflatedBroadcastChannel<ViewPlayerAction?>>(named(PLAYER_CHANNEL))
        val playerQueue by inject<PlayerQueue>()
        val episodePathHelper by inject<EpisodePathHelper>()
        val podcastPlayer by inject<PodcastPlayer>()
        val queueFlowUseCase  by inject<QueueFlowUseCase>()
        val addToQueueFlow  by inject<AddToQueueUseCase>()
        val deleteQueueFlow  by inject<DeleteQueueUseCase>()
        val playerNotificationHandler = PlayerNotificationHandlerImpl(this)

        PlayerHandler(
            progressRepository = progressRepository,
            playerChannel = playerChannel,
            playerNotificationHandler = playerNotificationHandler,
            podcastPlayer = podcastPlayer,
            episodeHelper = episodePathHelper,
            playerQueue = playerQueue,
            queueFlowUseCase = queueFlowUseCase,
            addToQueueUseCase = addToQueueFlow,
            deleteQueueUseCase = deleteQueueFlow
        )
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        playerHandler.init()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            if (intent.action == ACTION_NOTIFICATION_DISMISSED) {
                stopForeground(true)
                stopSelf()
                return@let
            }

            val action = intent.action
            val episode = intent.getParcelableExtra<ViewEpisode?>(EPISODE)
            playerHandler.handlePlayerAction(action = action, episode = episode)
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        playerHandler.destroy()
    }
}
