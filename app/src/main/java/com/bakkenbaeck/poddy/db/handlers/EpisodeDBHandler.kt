package com.bakkenbaeck.poddy.db.handlers

import com.bakkenbaeck.poddy.presentation.model.DownloadState
import db.PoddyDB
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.withContext
import org.db.Episode

interface EpisodeDBHandler {
    suspend fun getEpisode(episodeId: String): Episode?
    suspend fun getEpisodes(podcastId: String): List<Episode>
    suspend fun deleteEpisode(episodeId: String)
    suspend fun insertEpisodes(episodes: List<Episode>)
    suspend fun deletePodcastEpisodes(podcastId: String)
    suspend fun doesEpisodesAlreadyExist(episodeIds: List<String>): Boolean
    suspend fun updateDownloadState(episodeId: String, downloadState: DownloadState)
    suspend fun updateProgress(episodeId: String, progress: Long)
}

class EpisodeDBHandlerImpl(
    private val db: PoddyDB,
    private val context: CoroutineContext
) : EpisodeDBHandler {
    override suspend fun getEpisode(episodeId: String): Episode? {
        return withContext(context) {
            return@withContext db.episodeQueries.selectById(episodeId).executeAsOneOrNull()
        }
    }

    override suspend fun getEpisodes(podcastId: String): List<Episode> {
        return withContext(context) {
            return@withContext db.episodeQueries.selectByPodcastId(podcastId).executeAsList()
        }
    }

    override suspend fun deleteEpisode(episodeId: String) {
        return withContext(context) {
            db.episodeQueries.deleteByEpisodeId(episodeId)
            db.queueQueries.delete(episodeId)
        }
    }

    override suspend fun insertEpisodes(episodes: List<Episode>) {
        return withContext(context) {
            episodes.forEach {
                db.episodeQueries.insert(it)
            }
        }
    }

    override suspend fun deletePodcastEpisodes(podcastId: String) {
        return withContext(context) {
            db.episodeQueries.deleteByPodcastId(podcastId)
        }
    }

    override suspend fun doesEpisodesAlreadyExist(episodeIds: List<String>): Boolean {
        return withContext(context) {
            val result = db.episodeQueries.alreadyExists(episodeIds).executeAsOne()
            return@withContext result.toInt() == episodeIds.count()
        }
    }

    override suspend fun updateDownloadState(episodeId: String, downloadState: DownloadState) {
        return withContext(context) {
            db.episodeQueries.updateDownloadState(downloadState.value.toLong(), episodeId)
        }
    }

    override suspend fun updateProgress(episodeId: String, progress: Long) {
        return withContext(context) {
            db.episodeQueries.updateProgressState(progress, episodeId)
        }
    }
}
