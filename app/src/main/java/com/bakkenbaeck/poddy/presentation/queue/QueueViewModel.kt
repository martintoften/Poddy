package com.bakkenbaeck.poddy.presentation.queue

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.presentation.mappers.mapToViewEpisodeFromDB
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.repository.FeedRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.db.Episode

class QueueViewModel(
    private val feedRepository: FeedRepository
) : ViewModel() {

    init {
        getQueue()
    }

    val queue by lazy { MutableLiveData<List<ViewEpisode>>() }

    private fun getQueue() {
        viewModelScope.launch {
            feedRepository.getQueue()
                .flowOn(Dispatchers.IO)
                .map { mapToViewEpisodeFromDB(it) }
                .collect { handleQueue(it) }
        }
    }

    private fun handleQueue(queueResult: List<ViewEpisode>) {
        val newQueue = mutableListOf<ViewEpisode>().apply {
            addAll(queueResult)
            addAll(queue.value ?: emptyList())
        }

        queue.value = newQueue
    }

    fun reorderQueue(queue: List<ViewEpisode>) {
        viewModelScope.launch {
            feedRepository.reorderQueue(queue)
        }
    }
}
