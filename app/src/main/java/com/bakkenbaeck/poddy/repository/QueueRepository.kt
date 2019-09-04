package com.bakkenbaeck.poddy.repository

import com.bakkenbaeck.poddy.db.handlers.EpisodeDBHandler
import com.bakkenbaeck.poddy.db.handlers.QueueDBHandler
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import com.bakkenbaeck.poddy.repository.mappers.mapEpisodeFromViewToDB
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.db.Episode
import org.db.Queue

class QueueRepository(
    private val queueDBHandler: QueueDBHandler,
    private val episodeDBHandler: EpisodeDBHandler
) {
    private val queueChannel = ConflatedBroadcastChannel<List<Episode>>()

    suspend fun addToQueue(podcast: ViewPodcast, episode: ViewEpisode) {
        val episodeId = episode.id
        val channelId = podcast.id
        val dbQueueItem = Queue.Impl(episodeId, channelId, -1)
        val dbEpisode = mapEpisodeFromViewToDB(podcast, episode)

        val doesAlreadyExist = episodeDBHandler.doesEpisodeAlreadyExist(episodeId)

        if (!doesAlreadyExist) {
            queueDBHandler.insertQueueItem(dbQueueItem, dbEpisode)
            val queue = queueDBHandler.getQueue()
            queueChannel.send(queue)
        }
    }

    suspend fun getQueue(): Flow<List<Episode>> {
        val queue = queueDBHandler.getQueue()
        queueChannel.send(queue)
        return queueChannel.asFlow()
    }

    suspend fun reorderQueue(queue: List<ViewEpisode>) {
        queueDBHandler.reorderQueue(queue.map { it.id })
    }

    suspend fun deleteEpisodeFromQueue(episode: ViewEpisode) {
        episodeDBHandler.deleteEpisode(episode.id)
        val queue = queueDBHandler.getQueue()
        queueChannel.send(queue)
    }
}
