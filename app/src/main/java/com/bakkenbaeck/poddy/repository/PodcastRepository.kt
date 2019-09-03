package com.bakkenbaeck.poddy.repository

import com.bakkenbaeck.poddy.db.DBReader
import com.bakkenbaeck.poddy.db.DBWriter
import com.bakkenbaeck.poddy.network.SearchApi
import com.bakkenbaeck.poddy.network.model.SearchResponse
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import org.db.Episode
import org.db.Podcast
import org.db.Queue

const val PODCAST = "podcast"
const val EPISODE = "episode"

class PodcastRepository(
    private val searchApi: SearchApi,
    private val dbWriter: DBWriter,
    private val dbReader: DBReader
) {

    private val queueChannel = ConflatedBroadcastChannel<List<Episode>>()
    private val podcastChannel = ConflatedBroadcastChannel<List<Podcast>>()
    private val singlePodcastChannel = ConflatedBroadcastChannel<Pair<Podcast, List<Episode>>>()

    suspend fun search(query: String): Flow<SearchResponse> {
        return flow {
            val result = searchApi.search(query, PODCAST)
            emit(result)
        }
    }

    suspend fun getPodcast(id: String): Flow<Pair<Podcast, List<Episode>>> {
        val podcastResponse = searchApi.getEpisodes(id, EPISODE)
        val podcast = Podcast.Impl(
            id = podcastResponse.id,
            title = podcastResponse.title,
            description = podcastResponse.description,
            image = podcastResponse.image
        )

        val episodes = podcastResponse.episodes.map { Episode.Impl(
            id = it.id,
            channel_id = podcast.id,
            title = it.title,
            description = it.description,
            pub_date = it.pub_date_ms,
            duration = it.audio_length_sec.toLong(),
            image = it.image
        ) }

        singlePodcastChannel.send(Pair(podcast, episodes))
        return singlePodcastChannel.asFlow()
    }

    suspend fun hasSubscribed(podcast: Podcast): Flow<Boolean> {
        return flow {
            val result = dbReader.doesPodcastAlreadyExist(podcast.id)
            emit(result)
        }
    }

    suspend fun subscribeOrUnsubscribeToPodcast(podcast: ViewPodcast) {
        val hasAlreadySubscribed = dbReader.doesPodcastAlreadyExist(podcast.id)

        if (hasAlreadySubscribed) {
            dbWriter.deletePodcast(podcast.id)
        } else {
            dbWriter.insertPodcast(Podcast.Impl(
                id = podcast.id,
                title = podcast.title,
                description = podcast.description,
                image = podcast.image
            ))
        }

        val dbPodcasts = dbReader.getPodcasts()
        podcastChannel.send(dbPodcasts)

        val dbPodcast = Podcast.Impl(
            id = podcast.id,
            title = podcast.title,
            description = podcast.description,
            image = podcast.image
        )

        val dbEpisodes = podcast.episodes.map { Episode.Impl(
            id = it.id,
            channel_id = podcast.id,
            title = it.title,
            description = it.description,
            pub_date = it.pubDate,
            duration = it.duration.toLong(),
            image = it.image
        ) }

        singlePodcastChannel.send(Pair(dbPodcast, dbEpisodes))
    }

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

    suspend fun getPodcasts(): Flow<List<Podcast>> {
        val podcasts = dbReader.getPodcasts()
        podcastChannel.send(podcasts)
        return podcastChannel.asFlow()
    }
}
