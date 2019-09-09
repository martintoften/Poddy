package com.bakkenbaeck.poddy.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bakkenbaeck.poddy.MainActivity
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.createNotificationChannel
import com.bakkenbaeck.poddy.extensions.notifyNotification
import com.bakkenbaeck.poddy.service.*

class PlayerNotificationHandler(
    private val context: Service
) {

    private var channel: NotificationChannel? = null

    fun buildNotification(podcastName: String, action: NotificationCompat.Action) {
        val style = androidx.media.app.NotificationCompat.MediaStyle()
            .setShowActionsInCompactView(0, 1, 2)

        val builder = NotificationCompat.Builder(context, PLAYER_CHANNEL_ID)
            .setContentTitle(podcastName)
            .setContentText("")
            .setSmallIcon(R.drawable.ic_queue)
            .setContentIntent(getPendingIntent())
            .setStyle(style)
            .addAction(generateAction(android.R.drawable.ic_media_rew, "Rewind", ACTION_REWIND))
            .addAction(action)
            .addAction(generateAction(android.R.drawable.ic_media_ff, "Fast Forward", ACTION_FAST_FORWARD))

        context.notifyNotification(PLAYER_NOTIFICATION_ID, builder.build())
    }

    fun generateAction(icon: Int, title: String, intentAction: String): NotificationCompat.Action {
        val intent = Intent(context, PlayerService::class.java).apply {
            action = intentAction
        }
        val pendingIntent = PendingIntent.getService(context.applicationContext, 1, intent, 0)
        return NotificationCompat.Action.Builder(icon, title, pendingIntent).build()
    }

    private fun getPendingIntent(): PendingIntent? {
        val notificationIntent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(context, 0, notificationIntent, 0)
    }

    fun createChannel() {
        if (channel != null || Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            return
        }

        channel = NotificationChannel(
            PLAYER_CHANNEL_ID,
            PLAYER_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        context.createNotificationChannel(channel ?: return)
    }
}
