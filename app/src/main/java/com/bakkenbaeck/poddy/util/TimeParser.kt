package com.bakkenbaeck.poddy.util

fun parseSecondsToMinutes(seconds: String?): String {
    if (seconds == null) return ""

    return try {
        val parsedSeconds = Integer.parseInt(seconds)
        val minutes = parsedSeconds / 60
        "$minutes min"
    } catch (e: NumberFormatException) {
        seconds
    }
}
