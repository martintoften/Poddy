package com.bakkenbaeck.poddy.db.handlers

import db.PoddyDB
import kotlinx.coroutines.withContext
import org.db.ByIdsEpisodes
import org.db.Episode
import org.db.Queue
import kotlin.coroutines.CoroutineContext

interface QueueDBHandler {
    suspend fun getQueue(): List<ByIdsEpisodes>
    suspend fun insertQueueItem(episode: Episode)
    suspend fun reorderQueue(queueIds: List<String>)
    suspend fun doesEpisodeAlreadyExist(episodeId: String): Boolean
}

class QueueDBHandlerImpl(
    private val db: PoddyDB,
    private val context: CoroutineContext
) : QueueDBHandler {
    override suspend fun getQueue(): List<ByIdsEpisodes> {
        return withContext(context) {
            val queue = db.queueQueries.selectEpisodeIdAll().executeAsList()
            val episodeResult = db.episodeQueries.byIdsEpisodes(queue).executeAsList()
            return@withContext queue.mapNotNull {
                episodeResult.firstOrNull { episode -> it == episode.id } // Query order is ignored for episodes
            }
        }
    }

    override suspend fun insertQueueItem(episode: Episode) {
        return withContext(context) {
            db.episodeQueries.insert(episode)
            val queueItem = Queue.Impl(episode.id, episode.podcast_id, -1)
            db.queueQueries.insert(queueItem)
            db.queueQueries
                .selectAll()
                .executeAsList()
                .forEachIndexed { index, ep ->
                    db.queueQueries.updateIndex(index.toLong() + 1, ep.episode_id)
                }
        }
    }

    override suspend fun reorderQueue(queueIds: List<String>) {
        return withContext(context) {
            queueIds.forEachIndexed { index, id ->
                db.queueQueries.updateIndex(index.toLong(), id)
            }
        }
    }

    override suspend fun doesEpisodeAlreadyExist(episodeId: String): Boolean {
        return withContext(context) {
            val result = db.queueQueries.alreadyExist(episodeId).executeAsOne()
            return@withContext result > 0
        }
    }
}
