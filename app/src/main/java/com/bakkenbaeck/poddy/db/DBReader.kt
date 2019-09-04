package com.bakkenbaeck.poddy.db

import db.PoddyDB
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.db.Episode
import org.db.Podcast
import kotlin.coroutines.CoroutineContext

class DBReader(
    private val db: PoddyDB,
    private val context: CoroutineContext
) {
    suspend fun getQueue(): List<Episode> {
        return withContext(context) {
            val queue = db.queueQueries.selectEpisodeIdAll().executeAsList()
            val episodeResult = db.episodeQueries.selectByIds(queue).executeAsList()
            return@withContext queue
                .map { episodeResult.first { episode ->  it == episode.id } } // Query order is ignored for episodes
        }
    }

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

    suspend fun getEpisodes(podcastId: String): List<Episode> {
        return withContext(context) {
            return@withContext db.episodeQueries.selectByPodcastId(podcastId).executeAsList()
        }
    }

    suspend fun doesEpisodeAlreadyExist(id: String): Boolean {
        return withContext(context) {
            val result = db.queueQueries.doesAlreadyExist(id).executeAsOne()
            return@withContext result > 0
        }
    }

    suspend fun getSubscribedPodcasts(): List<Podcast> {
        return withContext(context) {
            val ids = db.subQueries.selectAll().executeAsList()
            return@withContext db.podcastQueries.selectByIds(ids).executeAsList()
        }
    }

    suspend fun doesSubscribedPodcastAlreadyExist(podcastId: String): Boolean {
        return withContext(context) {
            val result = db.subQueries.doesAlreadyExist(podcastId).executeAsOne()
            return@withContext result > 0
        }
    }

    suspend fun alreadyExists(episodeIds: List<String>): Boolean {
        return withContext(context) {
            val result = db.episodeQueries.alreadyExists(episodeIds).executeAsOne()
            return@withContext result.toInt() == episodeIds.count()
        }
    }
}
