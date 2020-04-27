package com.bakkenbaeck.poddy.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.bakkenbaeck.poddy.ACTION_NOTIFICATION_DISMISSED
import com.bakkenbaeck.poddy.EPISODE
import com.bakkenbaeck.poddy.PlayerHandler
import com.bakkenbaeck.poddy.PodcastPlayer
import com.bakkenbaeck.poddy.notification.PlayerNotificationHandlerImpl
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction
import com.bakkenbaeck.poddy.repository.ProgressRepository
import com.bakkenbaeck.poddy.usecase.AddToQueueUseCase
import com.bakkenbaeck.poddy.usecase.DeleteQueueUseCase
import com.bakkenbaeck.poddy.usecase.QueueFlowUseCase
import com.bakkenbaeck.poddy.util.EpisodePathHelper
import com.bakkenbaeck.poddy.util.PlayerQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

class PlayerService : Service() {

    private val playerHandler by lazy {
        val progressRepository by inject<ProgressRepository>()
        val playerChannel by inject<ConflatedBroadcastChannel<ViewPlayerAction?>>(named("playerChannel"))
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
            mainDispatcher = Dispatchers.Main,
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
            if (intent.action == ACTION_NOTIFICATION_DISMISSED ) {
                stopForeground(true)
                stopSelf()
                return@let
            }

            val action = intent.action
            val episode = intent.getParcelableExtra<ViewEpisode?>(EPISODE)
            playerHandler.handleIntent(action = action, episode = episode)
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        playerHandler.destroy()
    }
}
