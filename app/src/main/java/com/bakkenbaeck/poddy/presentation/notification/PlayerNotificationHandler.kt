package com.bakkenbaeck.poddy.presentation.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bakkenbaeck.poddy.*
import com.bakkenbaeck.poddy.extensions.createNotificationChannel
import com.bakkenbaeck.poddy.extensions.notifyNotification
import com.bakkenbaeck.poddy.presentation.MainActivity
import com.bakkenbaeck.poddy.presentation.player.*
import com.bakkenbaeck.poddy.presentation.service.PlayerService

const val PLAYER_NOTIFICATION_ID = 2
const val PLAYER_CHANNEL_ID = "102"
const val PLAYER_CHANNEL_NAME = "Player channel"

interface PlayerNotificationHandler {
    fun showPauseNotification(podcastName: String, episodeName: String)
    fun showPlayNotification(podcastName: String, episodeName: String)
    fun initNotification(podcastName: String, episodeName: String)
}

class PlayerNotificationHandlerImpl(
    private val context: Service
) : PlayerNotificationHandler {

    private var channel: NotificationChannel? = null

    private fun buildNotification(podcastName: String, episodeName: String, action: NotificationCompat.Action): Notification {
        val style = androidx.media.app.NotificationCompat.MediaStyle()
            .setShowActionsInCompactView(0, 1, 2)

        val builder = NotificationCompat.Builder(context, PLAYER_CHANNEL_ID)
            .setContentTitle(podcastName)
            .setContentText(episodeName)
            .setSmallIcon(R.drawable.ic_queue)
            .setContentIntent(getPendingIntent())
            .setStyle(style)
            .addAction(generateAction(android.R.drawable.ic_media_rew, "Rewind",
                ACTION_REWIND
            ))
            .addAction(action)
            .addAction(
                generateAction(
                    android.R.drawable.ic_media_ff,
                    "Fast Forward",
                    ACTION_FAST_FORWARD
                )
            )
            .setSound(null)
            .setOnlyAlertOnce(true)
            .setDeleteIntent(createOnDismissedIntent(context))

        return builder.build()
    }

    private fun createOnDismissedIntent(context: Context): PendingIntent? {
        val intent: Intent = Intent(context, PlayerService::class.java)
            .setAction(ACTION_NOTIFICATION_DISMISSED)
        return PendingIntent.getService(context, 100, intent, 0)
    }

    override fun showPauseNotification(podcastName: String, episodeName: String) {
        val action = generatePauseAction()
        val notification = buildNotification(podcastName, episodeName, action)
        context.startForeground(PLAYER_NOTIFICATION_ID, notification)
    }

    override fun showPlayNotification(podcastName: String, episodeName: String) {
        val action = generatePlayAction()
        val notification = buildNotification(podcastName, episodeName, action)
        context.notifyNotification(PLAYER_NOTIFICATION_ID, notification)
        context.stopForeground(false)
    }

    private fun generatePauseAction(): NotificationCompat.Action {
        return generateAction(android.R.drawable.ic_media_pause, "Pause",
            ACTION_PAUSE
        )
    }

    private fun generatePlayAction(): NotificationCompat.Action {
        return generateAction(android.R.drawable.ic_media_play, "Play",
            ACTION_PLAY
        )
    }

    private fun generateAction(
        icon: Int,
        title: String,
        intentAction: String
    ): NotificationCompat.Action {
        val intent = Intent(context, PlayerService::class.java).apply {
            action = intentAction
        }
        val pendingIntent = PendingIntent.getService(context, 1, intent, 0)
        return NotificationCompat.Action.Builder(icon, title, pendingIntent).build()
    }

    private fun getPendingIntent(): PendingIntent? {
        val notificationIntent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(context, 0, notificationIntent, 0)
    }

    override fun initNotification(podcastName: String, episodeName: String) {
        createChannel()
        val action = generatePauseAction()
        val notification = buildNotification(podcastName, episodeName, action)
        context.startForeground(PLAYER_NOTIFICATION_ID, notification)
    }

    private fun createChannel() {
        if (channel != null || Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            return
        }

        channel = NotificationChannel(
            PLAYER_CHANNEL_ID,
            PLAYER_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setSound(null, null)
        }

        context.createNotificationChannel(channel ?: return)
    }
}

class TestPlayerNotificationHandlerImpl : PlayerNotificationHandler {
    override fun showPauseNotification(podcastName: String, episodeName: String) {}
    override fun showPlayNotification(podcastName: String, episodeName: String) {}
    override fun initNotification(podcastName: String, episodeName: String) {}
}