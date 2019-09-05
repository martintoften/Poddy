package com.bakkenbaeck.poddy.repository

import com.bakkenbaeck.poddy.download.DownloadHandler
import java.io.File

class DownloadRepository(
    private val downloadHandler: DownloadHandler
) {
    suspend fun downloadPodcast(id: String, url: String, file: File) {
        downloadHandler.downloadPodcast(id, url, file)
    }
}
