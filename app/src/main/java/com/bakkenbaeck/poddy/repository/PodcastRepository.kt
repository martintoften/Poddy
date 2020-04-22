package com.bakkenbaeck.poddy.repository

import com.bakkenbaeck.poddy.db.handlers.EpisodeDBHandler
import com.bakkenbaeck.poddy.db.handlers.PodcastDBHandler
import com.bakkenbaeck.poddy.db.handlers.SubscriptionDBHandler
import com.bakkenbaeck.poddy.network.SearchApi
import com.bakkenbaeck.poddy.network.model.SearchResponse
import com.bakkenbaeck.poddy.presentation.mappers.mapToViewPodcastFromDB
import com.bakkenbaeck.poddy.presentation.model.*
import com.bakkenbaeck.poddy.repository.mappers.mapEpisodesFromNetworkToDB
import com.bakkenbaeck.poddy.repository.mappers.mapPodcastFromNetworkToDB
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
    private val subscriptionDBHandler: SubscriptionDBHandler,
    private val subscriptionsChannel: ConflatedBroadcastChannel<List<ViewPodcast>>
) {

    suspend fun search(query: String): Flow<SearchResponse> {
        return flow {
            val result = searchApi.search(query, PODCAST)
            emit(result)
        }
    }

    suspend fun getEpisode(episodeId: String): ViewEpisode? {
        return episodeDBHandler.getEpisode(episodeId)?.toViewModel()
    }

    suspend fun getPodcastFlow(podcastId: String, nextDate: Long? = null): Flow<ViewPodcast> {
        return flow {
            val (dbPodcast, dbEpisodes) = podcastDBHandler.getPodcastWithEpisodes(podcastId)
            val hasSubscribed = subscriptionDBHandler.doesSubscribedPodcastAlreadyExist(podcastId)
            if (dbPodcast != null) {
                val mappedPodcast = mapToViewPodcastFromDB(
                    dbPodcast,
                    dbEpisodes.toPodcastEpisodeViewModel(),
                    hasSubscribed
                )
                emit(mappedPodcast)
            }

            val podcastResponse = searchApi.getEpisodes(podcastId, EPISODE, nextDate)
            val podcast = mapPodcastFromNetworkToDB(podcastResponse)
            val episodes = mapEpisodesFromNetworkToDB(podcastResponse)

            val isFirstRequest = nextDate == null
            if (isFirstRequest) updateEpisodes(podcast, episodes)
            else podcastDBHandler.insertPodcast(podcast, episodes)

            val updatedDbEpisodes = episodeDBHandler.getEpisodes(podcast.id)

            val mappedPodcast = mapToViewPodcastFromDB(
                podcast,
                updatedDbEpisodes.toPodcastEpisodeViewModel(),
                hasSubscribed
            )

            emit(mappedPodcast)
        }
    }

    // Temp, find a way to update episodes
    private suspend fun updateEpisodes(
        podcast: Podcast,
        episodes: List<Episode>
    ) {
        val hasAllLatestEpisodes = episodeDBHandler.doesEpisodesAlreadyExist(episodes.map { it.id })
        val hasAlmostAllEpisodes = podcast.total_episodes - episodes.count() <= 10

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

    suspend fun toggleSubscription(podcast: ViewPodcast): Flow<Boolean> {
        return flow {
            val hasAlreadySubscribed =
                subscriptionDBHandler.doesSubscribedPodcastAlreadyExist(podcast.id)
            val dbPodcast = podcast.toDbModel()

            if (hasAlreadySubscribed) subscriptionDBHandler.deleteSubscribedPodcast(podcast.id)
            else subscriptionDBHandler.insertSubscribedPodcast(dbPodcast)

            emit(hasAlreadySubscribed)

            val dbPodcasts = subscriptionDBHandler.getSubscribedPodcasts().toPodcastViewModel()
            subscriptionsChannel.send(dbPodcasts)
        }
    }

    suspend fun getSubscribedPodcasts(): Flow<List<ViewPodcast>> {
        val podcasts = subscriptionDBHandler.getSubscribedPodcasts().toPodcastViewModel()
        subscriptionsChannel.send(podcasts)
        return subscriptionsChannel.asFlow()
    }
}
