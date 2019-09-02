package com.bakkenbaeck.poddy.repository

import com.bakkenbaeck.poddy.db.DBReader
import com.bakkenbaeck.poddy.db.DBWriter
import com.bakkenbaeck.poddy.network.model.EpisodeItem
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
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

    suspend fun addToQueue(podcast: ViewPodcast, episode: ViewEpisode) {
        val episodeId = episode.id
        val channelId = podcast.id
        val dbQueueItem = Queue.Impl(episodeId, channelId, -1)
        val dbEpisode = Episode.Impl(
            id = episodeId,
            channel_id = channelId,
            title = episode.title,
            description = episode.description,
            pub_date = episode.pubDate,
            duration = episode.duration.toLong(),
            image = episode.image
        )

        val doesAlreadyExist = dbReader.doesEpisodeAlreadyExist(episodeId)

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

    suspend fun reorderQueue(queue: List<ViewEpisode>) {
        dbWriter.reorderQueue(queue.map { it.id })
    }

    suspend fun addPodcast(podcast: ViewPodcast) {
        val podcast = Podcast.Impl(
            id = podcast.id,
            title = podcast.title,
            description = podcast.description,
            image = podcast.image
        )

        val doesAlreadyExist = dbReader.doesPodcastAlreadyExist(podcast.id)

        if (!doesAlreadyExist) {
            dbWriter.insertPodcast(podcast)
            podcastChannel.send(listOf(podcast))
        }
    }

    suspend fun getPodcasts(): Flow<List<Podcast>> {
        val podcasts = dbReader.getPodcasts()
        podcastChannel.send(podcasts)
        return podcastChannel.asFlow()
    }
}
