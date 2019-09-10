package com.bakkenbaeck.poddy.extensions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

inline fun <reified T> AppCompatActivity.startForegroundService(intent: Intent.() -> Intent) {
    ContextCompat.startForegroundService(this, Intent(this, T::class.java).intent())
}
