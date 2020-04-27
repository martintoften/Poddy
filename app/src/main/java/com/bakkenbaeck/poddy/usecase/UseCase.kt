package com.bakkenbaeck.poddy.usecase

import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.presentation.model.toByIdsViewModel
import com.bakkenbaeck.poddy.repository.QueueRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
        queueRepository.addToQueue(input)
    }
}

class ReorderQueueUseCase(
    private val queueRepository: QueueRepository
) : UseCase<List<ViewEpisode>, Unit> {
    override suspend fun execute(input: List<ViewEpisode>) {
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