package com.bakkenbaeck.poddy.db.handlers

import db.PoddyDB
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.db.Episode
import org.db.Podcast
import kotlin.coroutines.CoroutineContext

class PodcastDBHandler(
    private val db: PoddyDB,
    private val context: CoroutineContext
) {
    suspend fun getPodcastWithEpisodes(id: String): Pair<Podcast?, List<Episode>> {
        return withContext(context) {
            val podcast = async { db.podcastQueries.selectById(id).executeAsOneOrNull() }
            val episodes = async { db.episodeQueries.selectByPodcastId(id).executeAsList() }
            return@withContext Pair(podcast.await(), episodes.await())
        }
    }

    suspend fun getPodcast(id: String): Podcast? {
        return withContext(context) {
            return@withContext db.podcastQueries.selectById(id).executeAsOneOrNull()
        }
    }

    suspend fun deletePodcast(podcastId: String) {
        return withContext(context) {
            db.podcastQueries.delete(podcastId)
        }
    }

    suspend fun insertPodcast(podcast: Podcast, episodes: List<Episode>) {
        return withContext(context) {
            db.podcastQueries.insert(podcast)
            episodes.forEach { db.episodeQueries.insert(it) }
        }
    }

    suspend fun getPodcastFromEpisodeId(episodeId: String): Podcast? {
        return withContext(context) {
            return@withContext db.podcastQueries.selectByEpisodeId(episodeId).executeAsOneOrNull()
        }
    }
}
