package com.bakkenbaeck.poddy.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.bakkenbaeck.poddy.PlayerHandler
import com.bakkenbaeck.poddy.notification.PlayerNotificationHandler
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction
import com.bakkenbaeck.poddy.repository.PodcastRepository
import com.bakkenbaeck.poddy.repository.QueueRepository
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

class PlayerService : Service() {

    private val playerHandler by lazy {
        val queueRepository by inject<QueueRepository>()
        val podcastRepository by inject<PodcastRepository>()
        val playerChannel by inject<ConflatedBroadcastChannel<ViewPlayerAction>>(named("playerChannel"))
        val playerNotificationHandler = PlayerNotificationHandler(this)

        PlayerHandler(
            queueRepository = queueRepository,
            podcastRepository = podcastRepository,
            playerChannel = playerChannel,
            playerNotificationHandler = playerNotificationHandler
        )
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        playerHandler.listenForProgressUpdates()
        playerHandler.listenForPlayerAction()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { playerHandler.handleIntent(it) }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        playerHandler.onDestroy()
    }
}
