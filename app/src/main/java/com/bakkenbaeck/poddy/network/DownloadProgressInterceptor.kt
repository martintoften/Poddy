package com.bakkenbaeck.poddy.network

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response

const val DOWNLOAD_IDENTIFIER_HEADER = "download-identifier"

class DownloadProgressInterceptor(
    private val channel: ConflatedBroadcastChannel<ProgressEvent>
) : Interceptor {

    val progressMap = mutableMapOf<String, Float>()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        val originalResponseBody = originalResponse.body ?: return originalResponse
        val builder = originalResponse.newBuilder()
        val downloadIdentifier = originalResponse.request.header(DOWNLOAD_IDENTIFIER_HEADER)
            ?: return originalResponse
        val isDownloadable = originalResponse.header("content-type", "")?.contains("audio/") ?: false

        if (isDownloadable && downloadIdentifier.isNotEmpty()) {
            builder.body(DownloadProgressResponseBody(
                downloadIdentifier,
                originalResponseBody,
                downloadListener
            ))
        } else {
            builder.body(originalResponseBody)
        }

        return builder.build()
    }

    private val downloadListener = object : DownloadProgressListener {
        override fun update(identifier: String, bytesRead: Long, contentLength: Long, done: Boolean) {
            val progress =  (bytesRead / (contentLength / 100f))
            val lasProgress = progressMap[identifier] ?: 0f

            if (progress != lasProgress && progress - lasProgress > 5 || done) {
                progressMap[identifier] = progress
                GlobalScope.launch {
                    channel.send(ProgressEvent(identifier, contentLength, bytesRead))
                }
            }
        }
    }
}
