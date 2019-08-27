package com.bakkenbaeck.poddy.util

fun parseSecondsToMinutes(seconds: Int): String {
    val minutes = seconds / 60
    return "$minutes min"
}
