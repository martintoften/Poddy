package com.bakkenbaeck.poddy.repository

import com.bakkenbaeck.poddy.db.DBReader
import com.bakkenbaeck.poddy.db.DBWriter
import com.bakkenbaeck.poddy.model.EpisodeItem
import com.bakkenbaeck.poddy.model.EpisodeResponse
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.db.Episode
import org.db.Podcast
import org.db.Queue

class FeedRepository(
    private val dbWriter: DBWriter,
    private val dbReader: DBReader
) {

    private val queueChannel = ConflatedBroadcastChannel<List<Episode>>()
    private val podcastChannel = ConflatedBroadcastChannel<List<Podcast>>()

    suspend fun addToQueue(channel: EpisodeResponse, episode: EpisodeItem) {
        val episodeId = episode.id
        val channelId = channel.id
        val dbQueueItem = Queue.Impl(episodeId, channelId, -1)
        val dbEpisode = Episode.Impl(
            id = episodeId,
            channel_id = channelId,
            title = episode.title,
            description = episode.description,
            pub_date = episode.pub_date_ms,
            duration = episode.audio_length_sec.toLong(),
            image = episode.image
        )

        val doesAlreadyExist = dbReader.doesAlreadyExist(episodeId)

        if (!doesAlreadyExist) {
            dbWriter.insertQueueItem(dbQueueItem, dbEpisode)
            queueChannel.send(listOf(dbEpisode))
        }
    }

    suspend fun getQueue(): Flow<List<Episode>> {
        val queue = dbReader.getQueue()
        queueChannel.send(queue)
        return queueChannel.asFlow()
    }

    suspend fun reorderQueue(queue: List<Episode>) {
        dbWriter.reorderQueue(queue.map { it.id })
    }

    suspend fun addPodcast(channel: EpisodeResponse) {
        val podcast = Podcast.Impl(
            id = channel.id,
            title = channel.title,
            description = channel.description,
            image = channel.image
        )
        dbWriter.insertPodcast(podcast)

        podcastChannel.send(listOf(podcast))
    }

    suspend fun getPodcasts(): Flow<List<Podcast>> {
        val podcasts = dbReader.getPodcasts()
        podcastChannel.send(podcasts)
        return podcastChannel.asFlow()
    }
}
