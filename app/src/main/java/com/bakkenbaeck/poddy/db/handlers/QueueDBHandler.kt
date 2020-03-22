package com.bakkenbaeck.poddy.db.handlers

import db.PoddyDB
import kotlinx.coroutines.withContext
import org.db.Episode
import org.db.Queue
import kotlin.coroutines.CoroutineContext

class QueueDBHandler(
    private val db: PoddyDB,
    private val context: CoroutineContext
) {
    suspend fun getQueue(): List<Episode> {
        return withContext(context) {
            val queue = db.queueQueries.selectEpisodeIdAll().executeAsList()
            val episodeResult = db.episodeQueries.selectByIds(queue).executeAsList()
            return@withContext queue.map {
                episodeResult.first { episode -> it == episode.id } // Query order is ignored for episodes
            }
        }
    }

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

    suspend fun doesEpisodeAlreadyExist(episodeId: String): Boolean {
        return withContext(context) {
            val result = db.queueQueries.alreadyExist(episodeId).executeAsOne()
            return@withContext result > 0
        }
    }
}
