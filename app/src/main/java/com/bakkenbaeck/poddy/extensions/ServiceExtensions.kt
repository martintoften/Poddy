package com.bakkenbaeck.poddy.extensions

import android.app.Notification
import android.app.NotificationChannel
import android.app.Service
import androidx.core.app.NotificationManagerCompat

fun Service.notifyNotification(id: Int, notification: Notification) {
    NotificationManagerCompat.from(this).notify(id, notification)
}

fun Service.createNotificationChannel(channel: NotificationChannel) {
    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}
