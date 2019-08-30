package com.bakkenbaeck.poddy.repository

import com.bakkenbaeck.poddy.db.DBReader
import com.bakkenbaeck.poddy.db.DBWriter
import com.bakkenbaeck.poddy.db.IdBuilder
import com.bakkenbaeck.poddy.model.Channel
import com.bakkenbaeck.poddy.model.Rss
import com.bakkenbaeck.poddy.network.RssApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import org.db.Episode
import org.db.Queue

class FeedRepository(
    private val rssApi: RssApi,
    private val dbWriter: DBWriter,
    private val dbReader: DBReader
) {

    private val idBuilder by lazy { IdBuilder() }
    private val queueChannel = ConflatedBroadcastChannel<List<Episode>>()

    fun getFeed(url: String): Flow<Rss> {
        return flow {
            val result = rssApi.getFeed(url)
            emit(result)
        }
    }

    suspend fun addToQueue(episode: Channel.Item, channel: Channel, channelImage: String?) {
        val episodeId = idBuilder.buildQueueId(episode, channel)
        val channelId = idBuilder.buildChannelId(channel)
        val dbQueueItem = Queue.Impl(episodeId, channelId, -1) // FIRST / LAST?
        val dbEpisode = Episode.Impl(
            id = episodeId,
            channel_id = channelId,
            title = episode.title,
            description = episode.description,
            pub_date = episode.pubDate,
            duration = episode.duration,
            image = channelImage.orEmpty()
        )
        dbWriter.insertQueueItem(dbQueueItem, dbEpisode)

        queueChannel.send(listOf(dbEpisode))
    }

    suspend fun reorderQueue(queue: List<Episode>) {
        dbWriter.reorderQueue(queue.map { it.id })
    }

    suspend fun getQueue(): Flow<List<Episode>> {
        val queue = dbReader.getQueue()
        queueChannel.send(queue)
        return queueChannel.asFlow()
    }
}
