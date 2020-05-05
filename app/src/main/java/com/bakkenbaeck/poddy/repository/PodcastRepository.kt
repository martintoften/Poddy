package com.bakkenbaeck.poddy.repository

import com.bakkenbaeck.poddy.db.handlers.EpisodeDBHandler
import com.bakkenbaeck.poddy.db.handlers.PodcastDBHandler
import com.bakkenbaeck.poddy.db.handlers.SubscriptionDBHandler
import com.bakkenbaeck.poddy.db.model.JoinedEpisode
import com.bakkenbaeck.poddy.db.model.PodcastWithEpisodes
import com.bakkenbaeck.poddy.network.Result
import com.bakkenbaeck.poddy.network.SearchApi
import com.bakkenbaeck.poddy.network.handleApiError
import com.bakkenbaeck.poddy.network.model.*
import com.bakkenbaeck.poddy.network.safeApiCall
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import com.bakkenbaeck.poddy.presentation.model.toDbModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import org.db.Episode
import org.db.Podcast
import kotlin.coroutines.CoroutineContext

const val PODCAST = "podcast"
const val EPISODE = "episode"
val CATEGORIES_TO_IGNORE = listOf("144", "151") // Returns an empty array

class PodcastRepository(
    private val searchApi: SearchApi,
    private val podcastDBHandler: PodcastDBHandler,
    private val episodeDBHandler: EpisodeDBHandler,
    private val subscriptionDBHandler: SubscriptionDBHandler,
    private val subscriptionsChannel: ConflatedBroadcastChannel<List<Podcast>>,
    private val context: CoroutineContext = Dispatchers.IO
) {

    suspend fun search(query: String): Result<SearchResponse> {
        return safeApiCall(context) { searchApi.search(query, PODCAST) }
    }

    suspend fun getEpisode(episodeId: String): JoinedEpisode? {
        return episodeDBHandler.getEpisode(episodeId)
    }

    suspend fun getPodcastRecommendations(podcastId: String): Result<PodcastRecommendationResponse> {
        return safeApiCall(context) { searchApi.getPodcastRecommendations(podcastId) }
    }

    suspend fun getCategories(): Result<List<CategoryPodcastReponse>> {
        return coroutineScope {
            try {
                val genresResult = async(context) { searchApi.getGenres() }
                val topPodcastResult = async(context) { searchApi.getBestPodcasts() }
                val genres = genresResult.await()
                val topPodcasts = topPodcastResult.await()
                val categoryPodcasts = genres.genres
                    .filter { !CATEGORIES_TO_IGNORE.contains(it.id) }
                    .take(5)
                    .map { it.id }
                    .map { async(context) { searchApi.getCategoryById(it) } }
                    .map { it.await() }

                return@coroutineScope Result.Success(listOf(topPodcasts) + categoryPodcasts)
            } catch (e: Throwable) {
                return@coroutineScope handleApiError(e)
            }
        }
    }

    suspend fun hasSubscribed(podcastId: String): Boolean {
        return subscriptionDBHandler.hasSubscribed(podcastId)
    }

    suspend fun getPodcastFlow(podcastId: String, nextDate: Long? = null): Flow<Result<PodcastWithEpisodes>> {
        return flow {
            val dbPodcast = podcastDBHandler.getPodcastWithEpisodes(podcastId)
            if (dbPodcast != null) {
                emit(Result.Success(dbPodcast))
            }

            val podcastResponse = getPodcastRemote(podcastId, nextDate)
            emit(podcastResponse)
        }
    }

    suspend fun getPodcastRemoteFlow(podcastId: String, nextDate: Long? = null): Flow<Result<PodcastWithEpisodes>> {
        return flow {
            val podcastResponse = getPodcastRemote(podcastId, nextDate)
            emit(podcastResponse)
        }
    }

    private suspend fun getPodcastRemote(podcastId: String, nextDate: Long? = null): Result<PodcastWithEpisodes> {
        val podcastResponse = safeApiCall { searchApi.getEpisodes(podcastId, EPISODE, nextDate) }
        return when (podcastResponse) {
            is Result.Success -> {
                val updatedPodcast = updatePodcastEpisodes(podcastResponse.value, nextDate)
                Result.Success(updatedPodcast)
            }
            is Result.Error -> podcastResponse
        }
    }

    private suspend fun updatePodcastEpisodes(
        podcastResponse: PodcastResponse,
        nextDate: Long?
    ): PodcastWithEpisodes {
        val podcast = podcastResponse.toDbModel()
        val episodes = podcastResponse.toEpisodeDbModel()

        val isFirstRequest = nextDate == null
        if (isFirstRequest) updateEpisodes(podcast, episodes)
        else podcastDBHandler.insertPodcast(podcast, episodes)

        val dbEpisodes = episodeDBHandler.getEpisodes(podcast.id)

        return PodcastWithEpisodes(podcast, dbEpisodes)
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

            val dbPodcasts = subscriptionDBHandler.getSubscribedPodcasts()
            subscriptionsChannel.send(dbPodcasts)
        }
    }

    suspend fun getSubscribedPodcasts(): Flow<List<Podcast>> {
        val podcasts = subscriptionDBHandler.getSubscribedPodcasts()
        subscriptionsChannel.send(podcasts)
        return subscriptionsChannel.asFlow()
    }
}
