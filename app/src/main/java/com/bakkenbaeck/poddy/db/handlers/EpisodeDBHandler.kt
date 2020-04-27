package com.bakkenbaeck.poddy.db.handlers

import com.bakkenbaeck.poddy.presentation.model.DownloadState
import db.PoddyDB
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.withContext
import org.db.ByIdEpisode
import org.db.ByPodcastIdEpisodes
import org.db.Episode

data class JoinedEpisode(
    val id: String,
    val podcastId: String,
    val title: String,
    val description: String,
    val pubDate: Long,
    val audio: String,
    val duration: Long,
    val image: String,
    val timestamp: Long,
    val isDownloaded: Long,
    val progress: Long,
    val podcastTitle: String
)

fun ByIdEpisode.toJoinedModel(): JoinedEpisode {
    return JoinedEpisode(
        id = id,
        podcastId = podcast_id,
        title = title,
        description = description,
        pubDate = pub_date,
        audio = audio,
        duration = duration,
        image = image,
        timestamp = timestamp,
        isDownloaded = is_downloaded,
        progress = progress,
        podcastTitle = title_
    )
}

fun List<ByPodcastIdEpisodes>.toJoinedModel(): List<JoinedEpisode> {
    return map { JoinedEpisode(
        id = it.id,
        podcastId = it.podcast_id,
        title = it.title,
        description = it.description,
        pubDate = it.pub_date,
        audio = it.audio,
        duration = it.duration,
        image = it.image,
        timestamp = it.timestamp,
        isDownloaded = it.is_downloaded,
        progress = it.progress,
        podcastTitle = it.title_
    ) }
}


interface EpisodeDBHandler {
    suspend fun getEpisode(episodeId: String): JoinedEpisode?
    suspend fun getEpisodes(podcastId: String): List<JoinedEpisode>
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
    override suspend fun getEpisode(episodeId: String): JoinedEpisode? {
        return withContext(context) {
            return@withContext db.episodeQueries
                .byIdEpisode(episodeId)
                .executeAsOneOrNull()
                ?.toJoinedModel()
        }
    }

    override suspend fun getEpisodes(podcastId: String): List<JoinedEpisode> {
        return withContext(context) {
            return@withContext db.episodeQueries
                .byPodcastIdEpisodes(podcastId)
                .executeAsList()
                .toJoinedModel()
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
