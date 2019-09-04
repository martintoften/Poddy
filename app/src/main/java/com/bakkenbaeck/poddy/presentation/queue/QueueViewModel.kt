package com.bakkenbaeck.poddy.presentation.queue

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.presentation.mappers.mapToViewEpisodeFromDB
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.repository.QueueRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class QueueViewModel(
    private val queueRepository: QueueRepository
) : ViewModel() {

    init {
        getQueue()
    }

    val queue by lazy { MutableLiveData<List<ViewEpisode>>() }

    private fun getQueue() {
        viewModelScope.launch {
            queueRepository.getQueue()
                .flowOn(Dispatchers.IO)
                .map { mapToViewEpisodeFromDB(it) }
                .collect { handleQueue(it) }
        }
    }

    private fun handleQueue(queueResult: List<ViewEpisode>) {
        queue.value = queueResult
    }

    fun reorderQueue(queue: List<ViewEpisode>) {
        viewModelScope.launch {
            queueRepository.reorderQueue(queue)
        }
    }

    fun deleteEpisode(episode: ViewEpisode) {
        viewModelScope.launch {
            queueRepository.deleteEpisodeFromQueue(episode)
        }
    }
}
