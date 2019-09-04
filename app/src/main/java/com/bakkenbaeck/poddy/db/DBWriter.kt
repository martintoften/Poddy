package com.bakkenbaeck.poddy.db

import db.PoddyDB
import kotlinx.coroutines.withContext
import org.db.Episode
import org.db.Podcast
import org.db.Queue
import org.db.Subscription
import kotlin.coroutines.CoroutineContext

class DBWriter(
    private val db: PoddyDB,
    private val context: CoroutineContext
) {
    suspend fun insertQueueItem(queue: Queue, episode: Episode) {
        return withContext(context) {
            db.episodeQueries.insert(episode)
            db.queueQueries.insert(queue)
            db.queueQueries
                .selectAll()
                .executeAsList()
                .forEachIndexed { index, ep ->
                    db.queueQueries.updateIndex(index.toLong() + 1, ep.episode_id)
            }
        }
    }

    suspend fun reorderQueue(queueIds: List<String>) {
        return withContext(context) {
            queueIds.forEachIndexed { index, id ->
                db.queueQueries.updateIndex(index.toLong(), id)
            }
        }
    }

    suspend fun insertSubscribedPodcast(podcast: Podcast) {
        return withContext(context) {
            db.subQueries.insert(Subscription.Impl(podcast.id))
            db.podcastQueries.insert(podcast)
        }
    }

    suspend fun insertPodcast(podcast: Podcast, episodes: List<Episode>) {
        return withContext(context) {
            db.podcastQueries.insert(podcast)
            episodes.forEach { db.episodeQueries.insert(it) }
        }
    }

    suspend fun insertEpisodes(episodes: List<Episode>) {
        return withContext(context) {
            episodes.forEach {
                db.episodeQueries.insert(it)
            }
        }
    }

    suspend fun deleteSubscribedPodcast(podcastId: String) {
        return withContext(context) {
            db.podcastQueries.delete(podcastId)
            db.subQueries.delete(podcastId)
        }
    }

    suspend fun deletePodcast(podcastId: String) {
        return withContext(context) {
            db.podcastQueries.delete(podcastId)
        }
    }

    suspend fun deleteEpisode(episodeId: String) {
        return withContext(context) {
            db.episodeQueries.deleteByEpisodeId(episodeId)
            db.queueQueries.delete(episodeId)
        }
    }

    suspend fun deletePodcastEpisodes(podcastId: String) {
        return withContext(context) {
            db.episodeQueries.deleteByPodcastId(podcastId)
        }
    }
}
