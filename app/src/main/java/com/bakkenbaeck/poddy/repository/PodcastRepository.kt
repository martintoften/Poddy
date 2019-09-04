package com.bakkenbaeck.poddy.repository

import com.bakkenbaeck.poddy.db.handlers.EpisodeDBHandler
import com.bakkenbaeck.poddy.db.handlers.PodcastDBHandler
import com.bakkenbaeck.poddy.db.handlers.SubscriptionDBHandler
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
    private val podcastDBHandler: PodcastDBHandler,
    private val episodeDBHandler: EpisodeDBHandler,
    private val subscriptionDBHandler: SubscriptionDBHandler
) {

    private val subscriptionsChannel = ConflatedBroadcastChannel<List<Podcast>>()
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
        val (dbPodcast, dbEpisodes) = podcastDBHandler.getPodcastWithEpisodes(podcastId)

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
            podcastDBHandler.insertPodcast(podcast, episodes)
        }

        val updatedDbEpisodes = episodeDBHandler.getEpisodes(podcast.id)

        singlePodcastChannel.send(Pair(podcast, updatedDbEpisodes))
    }

    // Temp, find a way to update episodes
    private suspend fun updateEpisodes(
        podcast: Podcast,
        episodes: List<Episode>,
        dbEpisodes: List<Episode>
    ) {
        val hasAllLatestEpisodes = episodeDBHandler.doesEpisodesAlreadyExist(episodes.map { it.id })
        val hasAlmostAllEpisodes = podcast.total_episodes - dbEpisodes.count() <= 10

        if (hasAllLatestEpisodes && hasAlmostAllEpisodes) {
            podcastDBHandler.insertPodcast(podcast, episodes)
        } else if (!hasAllLatestEpisodes && hasAlmostAllEpisodes) {
            podcastDBHandler.insertPodcast(podcast, episodes)
        } else if (hasAllLatestEpisodes && !hasAlmostAllEpisodes) {
            podcastDBHandler.insertPodcast(podcast, episodes)
        } else {
            episodeDBHandler.deletePodcastEpisodes(podcast.id)
            podcastDBHandler.insertPodcast(podcast, episodes)
        }
    }

    suspend fun hasSubscribed(podcast: Podcast): Flow<Boolean> {
        return flow {
            val result = subscriptionDBHandler.doesSubscribedPodcastAlreadyExist(podcast.id)
            emit(result)
        }
    }

    suspend fun subscribeOrUnsubscribeToPodcast(podcast: ViewPodcast) {
        val hasAlreadySubscribed = subscriptionDBHandler.doesSubscribedPodcastAlreadyExist(podcast.id)
        val dbPodcast = mapPodcastFromViewToDB(podcast)

        if (hasAlreadySubscribed) subscriptionDBHandler.deleteSubscribedPodcast(podcast.id)
        else subscriptionDBHandler.insertSubscribedPodcast(dbPodcast)

        val dbPodcasts = subscriptionDBHandler.getSubscribedPodcasts()
        subscriptionsChannel.send(dbPodcasts)

        val dbEpisodes = mapEpisodesFromViewToDB(podcast)
        singlePodcastChannel.send(Pair(dbPodcast, dbEpisodes))
    }

    suspend fun getSubscribedPodcasts(): Flow<List<Podcast>> {
        val podcasts = subscriptionDBHandler.getSubscribedPodcasts()
        subscriptionsChannel.send(podcasts)
        return subscriptionsChannel.asFlow()
    }
}
