package com.bakkenbaeck.poddy.presentation.queue

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.repository.QueueRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class QueueViewModel(
    private val queueRepository: QueueRepository
) : ViewModel() {

    val queue by lazy { MutableLiveData<List<ViewEpisode>>() }

    init {
        listenForQueueUpdates()
        getQueue()
    }

    private fun listenForQueueUpdates() {
        viewModelScope.launch {
            queueRepository.listenForQueueUpdates()
                .flowOn(Dispatchers.IO)
                .catch { Log.e("Handing error", it.toString()) }
                .collect { handleQueue(it) }
        }
    }

    private fun handleQueue(queueResult: List<ViewEpisode>) {
        queue.value = queueResult
    }

    private fun getQueue() {
        viewModelScope.launch {
            queueRepository.getQueue()
        }
    }

    fun reorderQueue(queue: List<ViewEpisode>) {
        viewModelScope.launch {
            queueRepository.reorderQueue(queue)
        }
    }

    fun deleteEpisode(episode: ViewEpisode) {
        viewModelScope.launch {
            queueRepository.deleteEpisodeFromQueue(episode.id)
        }
    }
}
