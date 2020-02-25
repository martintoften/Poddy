package com.bakkenbaeck.poddy.download

import com.bakkenbaeck.poddy.network.DownloadApi
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import java.io.File
import kotlin.coroutines.CoroutineContext

class DownloadHandler(
    private val downloadApi: DownloadApi,
    private val context: CoroutineContext
) {
    suspend fun downloadPodcast(id: String, url: String, file: File): String {
        return withContext(context) {
            return@withContext download(id, url, file)
        }
    }

    private suspend fun download(id: String, url: String, file: File): String {
        val response = downloadApi.download(url, id)

        if (response.isSuccessful) {
            val body = response.body() ?: throw IllegalStateException("Body is null")
            file.sink()
                .buffer()
                .use { sink -> sink.writeAll(body.source()) }

            return id
        }

        throw IllegalStateException("Response was not successful")
    }
}
