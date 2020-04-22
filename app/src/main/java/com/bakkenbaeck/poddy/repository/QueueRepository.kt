package com.bakkenbaeck.poddy.repository

import com.bakkenbaeck.poddy.db.handlers.EpisodeDBHandler
import com.bakkenbaeck.poddy.db.handlers.QueueDBHandler
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.presentation.model.toByIdsViewModel
import com.bakkenbaeck.poddy.presentation.model.toDbModel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.db.Queue

class QueueRepository(
    private val queueDBHandler: QueueDBHandler,
    private val episodeDBHandler: EpisodeDBHandler,
    private val queueChannel: ConflatedBroadcastChannel<List<ViewEpisode>>
) {
    fun listenForQueueUpdates(): Flow<List<ViewEpisode>> {
        return queueChannel.asFlow()
    }

    suspend fun addToQueue(episode: ViewEpisode) {
        val (id, podcastId) = episode
        val dbQueueItem = Queue.Impl(id, podcastId, -1)
        val dbEpisode = episode.toDbModel()

        queueDBHandler.insertQueueItem(dbQueueItem, dbEpisode)
        val queue = queueDBHandler.getQueue().toByIdsViewModel()
        queueChannel.send(queue)
    }

    suspend fun getQueue(): List<ViewEpisode> {
        val queue = queueDBHandler.getQueue().toByIdsViewModel()
        queueChannel.send(queue)
        return queue
    }

    suspend fun reorderQueue(queue: List<ViewEpisode>) {
        queueDBHandler.reorderQueue(queue.map { it.id })
    }

    suspend fun deleteEpisodeFromQueue(episodeId: String) {
        episodeDBHandler.deleteEpisode(episodeId)
        val queue = queueDBHandler.getQueue().toByIdsViewModel()
        queueChannel.send(queue)
    }
}
