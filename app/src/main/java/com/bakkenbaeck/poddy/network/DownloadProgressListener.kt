package com.bakkenbaeck.poddy.network

interface DownloadProgressListener {
    fun update(downloadIdentifier: String, bytesRead: Long, contentLength: Long, done: Boolean)
}
