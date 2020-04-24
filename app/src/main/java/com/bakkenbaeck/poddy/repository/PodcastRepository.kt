package com.bakkenbaeck.poddy.repository

import com.bakkenbaeck.poddy.db.handlers.EpisodeDBHandler
import com.bakkenbaeck.poddy.db.handlers.PodcastDBHandler
import com.bakkenbaeck.poddy.db.handlers.SubscriptionDBHandler
import com.bakkenbaeck.poddy.network.Result
import com.bakkenbaeck.poddy.network.SearchApi
import com.bakkenbaeck.poddy.network.model.toDbModel
import com.bakkenbaeck.poddy.network.model.toEpisodeDbModel
import com.bakkenbaeck.poddy.network.model.toViewModel
import com.bakkenbaeck.poddy.network.safeApiCall
import com.bakkenbaeck.poddy.presentation.mappers.mapFromNetworkToView
import com.bakkenbaeck.poddy.presentation.mappers.mapToViewPodcastFromDB
import com.bakkenbaeck.poddy.presentation.model.*
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import org.db.Episode
import org.db.Podcast

const val PODCAST = "podcast"
const val EPISODE = "episode"
val CATEGORIES_TO_IGNORE = listOf("144", "151") // Returns an empty array

class PodcastRepository(
    private val searchApi: SearchApi,
    private val podcastDBHandler: PodcastDBHandler,
    private val episodeDBHandler: EpisodeDBHandler,
    private val subscriptionDBHandler: SubscriptionDBHandler,
    private val subscriptionsChannel: ConflatedBroadcastChannel<List<ViewPodcast>>
) {

    suspend fun search(query: String): Result<ViewPodcastSearch> {
        return safeApiCall {
            val result = searchApi.search(query, PODCAST)
            return@safeApiCall mapFromNetworkToView(result)
        }
    }

    suspend fun getEpisode(episodeId: String): ViewEpisode? {
        return episodeDBHandler.getEpisode(episodeId)?.toViewModel()
    }

    suspend fun getCategories(): Result<List<ViewCategory>> {
        return safeApiCall {
            coroutineScope {
                val genresResult = async { searchApi.getGenres() }
                val topPodcastResult = async { searchApi.getBestPodcasts().toViewModel() }
                val genres = genresResult.await()
                val topPodcasts = ViewCategory(0, "Top", topPodcastResult.await())
                val categoryPodcasts = genres.genres
                    .filter { !CATEGORIES_TO_IGNORE.contains(it.id) }
                    .take(5)
                    .map { it.id }
                    .map { async { searchApi.getCategoryById(it) } }
                    .map { it.await() }
                    .map { ViewCategory(it.id, it.name, it.toViewModel()) }

                return@coroutineScope listOf(topPodcasts) + categoryPodcasts
            }
        }
    }

    suspend fun getPodcast(podcastId: String, nextDate: Long? = null): Flow<ViewPodcast> {
        return flow {
            val (dbPodcast, dbEpisodes) = podcastDBHandler.getPodcastWithEpisodes(podcastId)
            val hasSubscribed = subscriptionDBHandler.hasSubscribed(podcastId)
            if (dbPodcast != null) {
                val mappedPodcast = mapToViewPodcastFromDB(
                    dbPodcast,
                    dbEpisodes.toPodcastEpisodeViewModel(),
                    hasSubscribed
                )
                emit(mappedPodcast)
            }

            val podcastResponse = safeApiCall { searchApi.getEpisodes(podcastId, EPISODE, nextDate) }

            if (podcastResponse is Result.Success) {
                val podcast = podcastResponse.value.toDbModel()
                val episodes = podcastResponse.value.toEpisodeDbModel()

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
            val hasAlreadySubscribed = subscriptionDBHandler.hasSubscribed(podcast.id)
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
