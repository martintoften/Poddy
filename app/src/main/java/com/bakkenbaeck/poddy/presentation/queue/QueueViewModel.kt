package com.bakkenbaeck.poddy.presentation.queue

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.repository.FeedRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.db.Episode

class QueueViewModel(
    private val feedRepository: FeedRepository
) : ViewModel() {

    init {
        getQueue()
    }

    val queue by lazy { MutableLiveData<List<Episode>>() }

    private fun getQueue() {
        viewModelScope.launch {
            feedRepository.getQueue()
                .flowOn(Dispatchers.IO)
                .collect { handleQueue(it) }
        }
    }

    private fun handleQueue(queueResult: List<Episode>) {
        val newQueue = mutableListOf<Episode>().apply {
            addAll(queueResult)
            addAll(queue.value ?: emptyList())
        }

        queue.value = newQueue
    }

    fun reorderQueue(queue: List<Episode>) {
        viewModelScope.launch {
            feedRepository.reorderQueue(queue)
        }
    }
}
