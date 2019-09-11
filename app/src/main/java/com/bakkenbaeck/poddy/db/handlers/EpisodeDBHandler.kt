package com.bakkenbaeck.poddy.db.handlers

import com.bakkenbaeck.poddy.presentation.model.DownloadState
import db.PoddyDB
import kotlinx.coroutines.withContext
import org.db.Episode
import kotlin.coroutines.CoroutineContext

class EpisodeDBHandler(
    private val db: PoddyDB,
    private val context: CoroutineContext
) {
    suspend fun getEpisodes(podcastId: String): List<Episode> {
        return withContext(context) {
            return@withContext db.episodeQueries.selectByPodcastId(podcastId).executeAsList()
        }
    }

    suspend fun deleteEpisode(episodeId: String) {
        return withContext(context) {
            db.episodeQueries.deleteByEpisodeId(episodeId)
            db.queueQueries.delete(episodeId)
        }
    }

    suspend fun insertEpisodes(episodes: List<Episode>) {
        return withContext(context) {
            episodes.forEach {
                db.episodeQueries.insert(it)
            }
        }
    }

    suspend fun deletePodcastEpisodes(podcastId: String) {
        return withContext(context) {
            db.episodeQueries.deleteByPodcastId(podcastId)
        }
    }

    suspend fun doesEpisodesAlreadyExist(episodeIds: List<String>): Boolean {
        return withContext(context) {
            val result = db.episodeQueries.alreadyExists(episodeIds).executeAsOne()
            return@withContext result.toInt() == episodeIds.count()
        }
    }

    suspend fun updateDownloadState(episodeId: String, downloadState: DownloadState) {
        return withContext(context) {
            db.episodeQueries.updateDownloadState(downloadState.value.toLong(), episodeId)
        }
    }

    suspend fun updateProgress(episodeId: String, progress: Long) {
        return withContext(context) {
            db.episodeQueries.updateProgressState(progress, episodeId)
        }
    }
}
