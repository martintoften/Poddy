package com.bakkenbaeck.poddy.repository

import com.bakkenbaeck.poddy.db.handlers.EpisodeDBHandler
import com.bakkenbaeck.poddy.download.DownloadHandler
import com.bakkenbaeck.poddy.presentation.model.DownloadState
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.io.File

class DownloadRepository(
    private val downloadHandler: DownloadHandler,
    private val episodeDBHandler: EpisodeDBHandler
) {
    private val downloadStateChannel by lazy { ConflatedBroadcastChannel<String?>() }

    suspend fun listenForDownloadStateUpdates(): Flow<String?> {
        downloadStateChannel.send(null)
        return downloadStateChannel.asFlow()
    }

    suspend fun downloadPodcast(episodeId: String, url: String, file: File): String {
        episodeDBHandler.updateDownloadState(episodeId, DownloadState.IN_PROGRESS)
        downloadStateChannel.send(episodeId)

        val downloadedPodcastId = downloadHandler.downloadPodcast(episodeId, url, file)
        episodeDBHandler.updateDownloadState(downloadedPodcastId, DownloadState.DOWNLOADED)
        downloadStateChannel.send(downloadedPodcastId)

        return downloadedPodcastId
    }
}