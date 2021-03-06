package com.bakkenbaeck.poddy.db.handlers

import com.bakkenbaeck.poddy.db.model.PodcastWithEpisodes
import com.bakkenbaeck.poddy.db.model.toJoinedModel
import db.PoddyDB
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.db.Episode
import org.db.Podcast
import kotlin.coroutines.CoroutineContext

interface PodcastDBHandler {
    suspend fun getPodcastWithEpisodes(id: String): PodcastWithEpisodes?
    suspend fun getPodcast(id: String): Podcast?
    suspend fun deletePodcast(podcastId: String)
    suspend fun insertPodcast(podcast: Podcast, episodes: List<Episode>)
    suspend fun getPodcastFromEpisodeId(episodeId: String): Podcast?
}

class PodcastDBHandlerImpl(
    private val db: PoddyDB,
    private val context: CoroutineContext
) : PodcastDBHandler {
    override suspend fun getPodcastWithEpisodes(id: String): PodcastWithEpisodes? {
        return withContext(context) {
            val podcast = async { db.podcastQueries.selectById(id).executeAsOneOrNull() }
            val episodes = async { db.episodeQueries.byPodcastIdEpisodes(id).executeAsList().toJoinedModel() }
            return@withContext PodcastWithEpisodes(
                podcast.await() ?: return@withContext null,
                episodes.await()
            )
        }
    }

    override suspend fun getPodcast(id: String): Podcast? {
        return withContext(context) {
            return@withContext db.podcastQueries.selectById(id).executeAsOneOrNull()
        }
    }

    override suspend fun deletePodcast(podcastId: String) {
        return withContext(context) {
            db.podcastQueries.delete(podcastId)
        }
    }

    override suspend fun insertPodcast(podcast: Podcast, episodes: List<Episode>) {
        return withContext(context) {
            db.podcastQueries.insert(podcast)
            episodes.forEach { db.episodeQueries.insert(it) }
        }
    }

    override suspend fun getPodcastFromEpisodeId(episodeId: String): Podcast? {
        return withContext(context) {
            return@withContext db.podcastQueries.selectByEpisodeId(episodeId).executeAsOneOrNull()
        }
    }
}
