package com.bakkenbaeck.poddy.network

data class ProgressEvent(
    val identifier: String,
    val contentLength: Long,
    val bytesRead: Long
) {
    fun getProgress(): Int {
        return (bytesRead / (contentLength / 100f)).toInt()
    }

    fun getFormattedProgress(): String {
        val progress = (bytesRead / (contentLength / 100f))
        return "%.0f".format(progress).plus("%")
    }
}
