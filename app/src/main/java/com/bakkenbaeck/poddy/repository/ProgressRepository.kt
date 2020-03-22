package com.bakkenbaeck.poddy.repository

import com.bakkenbaeck.poddy.db.handlers.EpisodeDBHandler

class ProgressRepository(
    private val episodeDBHandler: EpisodeDBHandler
) {
    suspend fun updateProgress(episodeId: String, progress: Long) {
        episodeDBHandler.updateProgress(episodeId, progress)
    }
}
