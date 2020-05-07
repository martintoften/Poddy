package com.bakkenbaeck.poddy.repository

import com.bakkenbaeck.poddy.db.handlers.EpisodeDBHandler
import com.bakkenbaeck.poddy.db.handlers.QueueDBHandler
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.db.ByIdsEpisodes
import org.db.Episode

class QueueRepository(
    private val queueDBHandler: QueueDBHandler,
    private val episodeDBHandler: EpisodeDBHandler,
    private val queueChannel: ConflatedBroadcastChannel<List<ByIdsEpisodes>>
) {
    suspend fun getQueueFlow(): Flow<List<ByIdsEpisodes>> {
        val queue = queueDBHandler.getQueue()
        queueChannel.send(queue)
        return queueChannel.asFlow()
    }

    suspend fun addToQueue(episode: Episode) {
        val alreadyExists = queueDBHandler.doesEpisodeAlreadyExist(episode.id)
        if (alreadyExists) return

        queueDBHandler.insertQueueItem(episode)
        val queue = queueDBHandler.getQueue()
        queueChannel.send(queue)
    }

    suspend fun reorderQueue(queueIds: List<String>) {
        queueDBHandler.reorderQueue(queueIds)
    }

    suspend fun getQueue(): List<ByIdsEpisodes> {
        return queueDBHandler.getQueue()
    }

    suspend fun deleteEpisodeFromQueue(episodeId: String) {
        episodeDBHandler.deleteEpisode(episodeId)
        val queue = queueDBHandler.getQueue()
        queueChannel.send(queue)
    }
}
