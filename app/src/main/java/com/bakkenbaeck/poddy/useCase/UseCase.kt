package com.bakkenbaeck.poddy.useCase

import com.bakkenbaeck.poddy.db.model.PodcastWithEpisodes
import com.bakkenbaeck.poddy.network.Result
import com.bakkenbaeck.poddy.network.model.toViewModel
import com.bakkenbaeck.poddy.presentation.mappers.mapToViewPodcastFromDB
import com.bakkenbaeck.poddy.presentation.model.*
import com.bakkenbaeck.poddy.repository.DownloadRepository
import com.bakkenbaeck.poddy.repository.PodcastRepository
import com.bakkenbaeck.poddy.repository.QueueRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.db.Podcast
import java.io.File

interface NoInputUseCase<O> {
    suspend fun execute(): O
}

interface UseCase<I, O> {
    suspend fun execute(input: I): O
}

class QueueUseCase(
    private val queueRepository: QueueRepository
) : NoInputUseCase<List<ViewEpisode>> {
    override suspend fun execute(): List<ViewEpisode> {
        return queueRepository.getQueue().toByIdsViewModel()
    }
}

class QueueFlowUseCase(
    private val queueRepository: QueueRepository
) : NoInputUseCase<Flow<List<ViewEpisode>>> {
    override suspend fun execute(): Flow<List<ViewEpisode>> {
        return queueRepository.getQueueFlow().map { it.toByIdsViewModel() }
    }
}

class AddToQueueUseCase(
    private val queueRepository: QueueRepository
) : UseCase<ViewEpisode, Unit> {
    override suspend fun execute(input: ViewEpisode) {
        queueRepository.addToQueue(input.toDbModel())
    }
}

class ReorderQueueUseCase(
    private val queueRepository: QueueRepository
) : UseCase<List<String>, Unit> {
    override suspend fun execute(input: List<String>) {
        queueRepository.reorderQueue(input)
    }
}

class DeleteQueueUseCase(
    private val queueRepository: QueueRepository
) : UseCase<String, Unit> {
    override suspend fun execute(episodeId: String) {
        queueRepository.deleteEpisodeFromQueue(episodeId)
    }
}

class GetEpisodeUseCase(
    private val podcastRepository: PodcastRepository
) : UseCase<String, ViewEpisode?> {
    override suspend fun execute(episodeId: String): ViewEpisode? {
        return podcastRepository.getEpisode(episodeId)?.toViewModel()
    }
}

class ToggleSubscriptionUseCase(
    private val podcastRepository: PodcastRepository
) : UseCase<ViewPodcast, Flow<SubscriptionState>> {
    override suspend fun execute(podcast: ViewPodcast): Flow<SubscriptionState> {
        return podcastRepository.toggleSubscription(podcast)
            .map { if (it) Unsubscribed() else Subscribed() }
    }
}

class PodcastSearchUseCase(
    private val podcastRepository: PodcastRepository
) : UseCase<String, Result<ViewPodcastSearch>> {
    override suspend fun execute(query: String): Result<ViewPodcastSearch> {
        return when (val result = podcastRepository.search(query)) {
            is Result.Success -> Result.Success(result.value.toViewModel())
            is Result.Error -> result
        }
    }
}

class GetCategoriesUseCase(
    private val podcastRepository: PodcastRepository
) : NoInputUseCase<Result<List<ViewCategory>>> {
    override suspend fun execute(): Result<List<ViewCategory>> {
        return when (val result = podcastRepository.getCategories()) {
            is Result.Success -> Result.Success(result.value.toViewModel())
            is Result.Error -> result
        }
    }
}

class GetPodcastRecommendationsUseCase(
    private val podcastRepository: PodcastRepository
) : UseCase<String, Result<List<ViewPodcast>>> {
    override suspend fun execute(query: String): Result<List<ViewPodcast>> {
        return when (val result = podcastRepository.getPodcastRecommendations(query)) {
            is Result.Success -> Result.Success(result.value.recommendations.toViewModel())
            is Result.Error -> result
        }
    }
}

class GetSubscribedPodcastsUseCase(
    private val podcastRepository: PodcastRepository
) : NoInputUseCase<Flow<List<ViewPodcast>>> {
    override suspend fun execute(): Flow<List<ViewPodcast>> {
        return podcastRepository.getSubscribedPodcasts()
            .map { it.toPodcastViewModel() }
    }
}

data class GetPodcastQuery(
    val podcastId: String,
    val lastTimestamp: Long? = null,
    val remoteOnly: Boolean = false
)

class GetPodcastUseCase(
    private val podcastRepository: PodcastRepository
) : UseCase<GetPodcastQuery, Flow<Result<ViewPodcast>>> {
    private suspend fun mapTo(podcastId: String, it: Result<PodcastWithEpisodes>): Result<ViewPodcast> {
        val hasSubscribed = podcastRepository.hasSubscribed(podcastId)
        return when (it) {
            is Result.Success -> {
                val podcast = mapToViewPodcastFromDB(
                    it.value.podcast,
                    it.value.episodes,
                    hasSubscribed
                )
                Result.Success(podcast)
            }
            is Result.Error -> it
        }
    }

    override suspend fun execute(query: GetPodcastQuery): Flow<Result<ViewPodcast>> {
        if (query.remoteOnly) {
            return podcastRepository.getPodcastRemoteFlow(query.podcastId, query.lastTimestamp)
                .map { mapTo(query.podcastId, it) }
        }
        return podcastRepository.getPodcastFlow(query.podcastId, query.lastTimestamp)
            .map { mapTo(query.podcastId, it) }
    }
}

data class DownloadEpisodeTask(
    val episodeId: String,
    val url: String,
    val file: File
)

class DownloadEpisodeUseCase(
    private val downloadRepository: DownloadRepository
) : UseCase<DownloadEpisodeTask, String> {
    override suspend fun execute(input: DownloadEpisodeTask): String {
        return downloadRepository.downloadPodcast(input.episodeId, input.url, input.file)
    }
}

class DownloadStateFlowUseCase(
    private val downloadRepository: DownloadRepository
) : NoInputUseCase<Flow<String?>> {
    override suspend fun execute(): Flow<String?> {
        return downloadRepository.getDownloadStateFlow()
    }
}