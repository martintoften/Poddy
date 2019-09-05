package com.bakkenbaeck.poddy.repository

import com.bakkenbaeck.poddy.db.handlers.DownloadState
import com.bakkenbaeck.poddy.db.handlers.EpisodeDBHandler
import com.bakkenbaeck.poddy.download.DownloadHandler
import java.io.File

class DownloadRepository(
    private val downloadHandler: DownloadHandler,
    private val episodeDBHandler: EpisodeDBHandler
) {
    suspend fun downloadPodcast(id: String, url: String, file: File): String {
        val downloadedPodcastId = downloadHandler.downloadPodcast(id, url, file)
        episodeDBHandler.updateDownloadState(downloadedPodcastId, DownloadState.DOWNLOADED)
        return id
    }
}
