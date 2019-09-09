package com.bakkenbaeck.poddy.extensions

import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bakkenbaeck.poddy.MainActivity
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.service.*

fun Service.notifyNotification(id: Int, notification: Notification) {
    NotificationManagerCompat.from(this).notify(id, notification)
}

fun Service.createNotificationChannel(channel: NotificationChannel) {
    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}
