package com.bakkenbaeck.poddy.presentation.queue

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.usecase.DeleteQueueUseCase
import com.bakkenbaeck.poddy.usecase.QueueFlowUseCase
import com.bakkenbaeck.poddy.usecase.ReorderQueueUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class QueueViewModel(
    private val queueFlowUseCase: QueueFlowUseCase,
    private val reorderQueueUseCase: ReorderQueueUseCase,
    private val deleteQueueUseCase: DeleteQueueUseCase
) : ViewModel() {

    val queue by lazy { MutableLiveData<List<ViewEpisode>>() }

    init {
        listenForQueueUpdates()
    }

    private fun listenForQueueUpdates() {
        viewModelScope.launch {
            queueFlowUseCase.execute()
                .flowOn(Dispatchers.IO)
                .catch { Log.e("Handing error", it.toString()) }
                .collect { handleQueue(it) }
        }
    }

    private fun handleQueue(queueResult: List<ViewEpisode>) {
        queue.value = queueResult
    }

    fun reorderQueue(queue: List<ViewEpisode>) {
        viewModelScope.launch {
            reorderQueueUseCase.execute(queue)
        }
    }

    fun deleteEpisode(episode: ViewEpisode) {
        viewModelScope.launch {
            deleteQueueUseCase.execute(episode.id)
        }
    }
}
