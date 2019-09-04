package com.bakkenbaeck.poddy.repository

import com.bakkenbaeck.poddy.db.DBReader
import com.bakkenbaeck.poddy.db.DBWriter
import com.bakkenbaeck.poddy.network.SearchApi
import com.bakkenbaeck.poddy.network.model.SearchResponse
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import com.bakkenbaeck.poddy.repository.mappers.mapEpisodesFromNetworkToDB
import com.bakkenbaeck.poddy.repository.mappers.mapEpisodesFromViewToDB
import com.bakkenbaeck.poddy.repository.mappers.mapPodcastFromNetworkToDB
import com.bakkenbaeck.poddy.repository.mappers.mapPodcastFromViewToDB
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import org.db.Episode
import org.db.Podcast

const val PODCAST = "podcast"
const val EPISODE = "episode"

class PodcastRepository(
    private val searchApi: SearchApi,
    private val dbWriter: DBWriter,
    private val dbReader: DBReader
) {

    private val podcastChannel = ConflatedBroadcastChannel<List<Podcast>>()
    private val singlePodcastChannel = ConflatedBroadcastChannel<Pair<Podcast, List<Episode>>>()

    suspend fun search(query: String): Flow<SearchResponse> {
        return flow {
            val result = searchApi.search(query, PODCAST)
            emit(result)
        }
    }

    suspend fun getPodcast(id: String, nextDate: Long? = null): Flow<Pair<Podcast, List<Episode>>> {
        val podcastResponse = searchApi.getEpisodes(id, EPISODE, nextDate)
        val podcast = mapPodcastFromNetworkToDB(podcastResponse)
        val episodes = mapEpisodesFromNetworkToDB(podcastResponse)

        dbWriter.insertEpisodes(episodes)
        val dbEpisodes = dbReader.getEpisodes(podcast.id)

        singlePodcastChannel.send(Pair(podcast, dbEpisodes))
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
        val dbPodcast = mapPodcastFromViewToDB(podcast)

        if (hasAlreadySubscribed) dbWriter.deletePodcast(podcast.id)
        else dbWriter.insertPodcast(dbPodcast)

        val dbPodcasts = dbReader.getPodcasts()
        podcastChannel.send(dbPodcasts)

        val dbEpisodes = mapEpisodesFromViewToDB(podcast)
        singlePodcastChannel.send(Pair(dbPodcast, dbEpisodes))
    }

    suspend fun getPodcasts(): Flow<List<Podcast>> {
        val podcasts = dbReader.getPodcasts()
        podcastChannel.send(podcasts)
        return podcastChannel.asFlow()
    }
}
