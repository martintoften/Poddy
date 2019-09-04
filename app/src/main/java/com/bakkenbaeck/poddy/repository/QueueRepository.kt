package com.bakkenbaeck.poddy.repository

import com.bakkenbaeck.poddy.db.DBReader
import com.bakkenbaeck.poddy.db.DBWriter
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import com.bakkenbaeck.poddy.repository.mappers.mapEpisodeFromViewToDB
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.db.Episode
import org.db.Queue

class QueueRepository(
    private val dbWriter: DBWriter,
    private val dbReader: DBReader
) {
    private val queueChannel = ConflatedBroadcastChannel<List<Episode>>()

    suspend fun addToQueue(podcast: ViewPodcast, episode: ViewEpisode) {
        val episodeId = episode.id
        val channelId = podcast.id
        val dbQueueItem = Queue.Impl(episodeId, channelId, -1)
        val dbEpisode = mapEpisodeFromViewToDB(podcast, episode)

        val doesAlreadyExist = dbReader.doesEpisodeAlreadyExist(episodeId)

        if (!doesAlreadyExist) {
            dbWriter.insertQueueItem(dbQueueItem, dbEpisode)
            val queue = dbReader.getQueue()
            queueChannel.send(queue)
        }
    }

    suspend fun getQueue(): Flow<List<Episode>> {
        val queue = dbReader.getQueue()
        queueChannel.send(queue)
        return queueChannel.asFlow()
    }

    suspend fun reorderQueue(queue: List<ViewEpisode>) {
        dbWriter.reorderQueue(queue.map { it.id })
    }

    suspend fun deleteEpisodeFromQueue(episode: ViewEpisode) {
        dbWriter.deleteEpisode(episode.id)
        val queue = dbReader.getQueue()
        queueChannel.send(queue)
    }
}
