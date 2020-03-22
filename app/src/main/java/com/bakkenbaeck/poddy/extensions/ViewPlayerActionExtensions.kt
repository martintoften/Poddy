package com.bakkenbaeck.poddy.extensions

import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction


fun ViewPlayerAction.getPlayIcon(): Int? {
    return when (this) {
        is ViewPlayerAction.Start -> R.drawable.ic_player_pause
        is ViewPlayerAction.Play -> R.drawable.ic_player_pause
        is ViewPlayerAction.Pause -> R.drawable.ic_player_play
        is ViewPlayerAction.Progress -> null
    }
}

fun ViewPlayerAction.Progress.getProgressInPercent(): Int {
    return ((progress.toDouble() / duration.toDouble()) * 100).toInt()
}

fun ViewPlayerAction.Progress.getProgressInFraction(): Double {
    return ((progress.toDouble() / duration.toDouble()))
}

fun ViewPlayerAction.Progress.getFormattedProgress(): String {
    return getFormattedTime(progress.toLong())
}

fun ViewPlayerAction.Progress.getFormattedDuration(): String {
    return getFormattedTime(duration.toLong())
}

fun getFormattedTime(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / 1000) / 60 % 60
    val hours = (millis / 1000) / (60 * 60) % 24

    return if (hours > 0L) "%02d:%02d:%02d".format(hours, minutes, seconds)
    else "%02d:%02d".format(minutes, seconds)
}
