package com.bakkenbaeck.poddy.db

import db.PoddyDB
import kotlinx.coroutines.withContext
import org.db.Episode
import org.db.Podcast
import org.db.Queue
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

    suspend fun insertPodcast(podcast: Podcast) {
        return withContext(context) {
            db.podcastQueries.insert(podcast)
        }
    }

    suspend fun deletePodcast(podcastId: String) {
        return withContext(context) {
            db.podcastQueries.delete(podcastId)
        }
    }

    suspend fun deleteEpisode(episodeId: String) {
        return withContext(context) {
            db.episodeQueries.delete(episodeId)
            db.queueQueries.delete(episodeId)
        }
    }
}
