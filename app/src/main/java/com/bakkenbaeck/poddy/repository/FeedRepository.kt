package com.bakkenbaeck.poddy.repository

import com.bakkenbaeck.poddy.db.DBReader
import com.bakkenbaeck.poddy.db.DBWriter
import com.bakkenbaeck.poddy.db.IdBuilder
import com.bakkenbaeck.poddy.model.Channel
import com.bakkenbaeck.poddy.model.Rss
import com.bakkenbaeck.poddy.network.RssApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.db.Episode
import org.db.Queue

class FeedRepository(
    private val rssApi: RssApi,
    private val dbWriter: DBWriter,
    private val dbReader: DBReader
) {

    private val idBuilder by lazy { IdBuilder() }

    fun getFeed(url: String): Flow<Rss> {
        return flow {
            val result = rssApi.getFeed(url)
            emit(result)
        }
    }

    suspend fun addToQueue(episode: Channel.Item, channel: Channel) {
        val episodeId = idBuilder.buildQueueId(episode, channel)
        val channelId = idBuilder.buildChannelId(channel)
        val dbQueueItem = Queue.Impl(episodeId, channelId)
        val dbEpisode = Episode.Impl(
            id = episodeId,
            channel_id = channelId,
            title = episode.title,
            description = episode.description,
            pub_date = episode.pubDate,
            duration = episode.duration.toLong()
        )
        dbWriter.insertQueueItem(dbQueueItem, dbEpisode)
    }

    suspend fun getQueue(): List<Episode> {
        return dbReader.getQueue()
    }
}
