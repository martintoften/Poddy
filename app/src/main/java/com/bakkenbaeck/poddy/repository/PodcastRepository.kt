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
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
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
    private val singlePodcastChannel = ConflatedBroadcastChannel<Pair<Podcast, List<Episode>>?>()

    suspend fun search(query: String): Flow<SearchResponse> {
        return flow {
            val result = searchApi.search(query, PODCAST)
            emit(result)
        }
    }

    suspend fun listenForPodcastUpdates(): Flow<Pair<Podcast, List<Episode>>?> {
        singlePodcastChannel.send(null)
        return singlePodcastChannel.asFlow()
    }

    suspend fun getPodcast(podcastId: String, nextDate: Long? = null) {
        val (dbPodcast, dbEpisodes) = dbReader.getPodcastWithEpisodes(podcastId)

        if (dbPodcast != null) {
            singlePodcastChannel.send(Pair(dbPodcast, dbEpisodes))
        }

        val podcastResponse = searchApi.getEpisodes(podcastId, EPISODE, nextDate)
        val podcast = mapPodcastFromNetworkToDB(podcastResponse)
        val episodes = mapEpisodesFromNetworkToDB(podcastResponse)

        val isFirstRequest = nextDate == null

        if (isFirstRequest) {
            updateEpisodes(podcast, episodes, dbEpisodes)
        } else {
            dbWriter.insertPodcast(podcast, episodes)
        }

        val updatedDbEpisodes = dbReader.getEpisodes(podcast.id)

        singlePodcastChannel.send(Pair(podcast, updatedDbEpisodes))
    }

    // Temp, find a way to update episodes
    private suspend fun updateEpisodes(
        podcast: Podcast,
        episodes: List<Episode>,
        dbEpisodes: List<Episode>
    ) {
        val hasAllLatestEpisodes = dbReader.alreadyExists(episodes.map { it.id })
        val hasAlmostAllEpisodes = podcast.total_episodes - dbEpisodes.count() <= 10

        if (hasAllLatestEpisodes && hasAlmostAllEpisodes) {
            dbWriter.insertPodcast(podcast, episodes)
        } else if (!hasAllLatestEpisodes && hasAlmostAllEpisodes) {
            dbWriter.insertPodcast(podcast, episodes)
        } else if (hasAllLatestEpisodes && !hasAlmostAllEpisodes) {
            dbWriter.insertPodcast(podcast, episodes)
        } else {
            dbWriter.deletePodcastEpisodes(podcast.id)
            dbWriter.insertPodcast(podcast, episodes)
        }
    }

    suspend fun hasSubscribed(podcast: Podcast): Flow<Boolean> {
        return flow {
            val result = dbReader.doesSubscribedPodcastAlreadyExist(podcast.id)
            emit(result)
        }
    }

    suspend fun subscribeOrUnsubscribeToPodcast(podcast: ViewPodcast) {
        val hasAlreadySubscribed = dbReader.doesSubscribedPodcastAlreadyExist(podcast.id)
        val dbPodcast = mapPodcastFromViewToDB(podcast)

        if (hasAlreadySubscribed) dbWriter.deleteSubscribedPodcast(podcast.id)
        else dbWriter.insertSubscribedPodcast(dbPodcast)

        val dbPodcasts = dbReader.getSubscribedPodcasts()
        podcastChannel.send(dbPodcasts)

        val dbEpisodes = mapEpisodesFromViewToDB(podcast)
        singlePodcastChannel.send(Pair(dbPodcast, dbEpisodes))
    }

    suspend fun getSubscribedPodcasts(): Flow<List<Podcast>> {
        val podcasts = dbReader.getSubscribedPodcasts()
        podcastChannel.send(podcasts)
        return podcastChannel.asFlow()
    }
}
