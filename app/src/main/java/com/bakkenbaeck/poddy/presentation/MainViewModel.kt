package com.bakkenbaeck.poddy.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction
import com.bakkenbaeck.poddy.repository.QueueRepository
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class MainViewModel(
    private val playerChannel: ConflatedBroadcastChannel<ViewPlayerAction>,
    private val queueRepository: QueueRepository
) : ViewModel() {

    val playerUpdates by lazy { MutableLiveData<ViewPlayerAction>() }
    val queueSize by lazy { MutableLiveData<Int>() }

    init {
        listenForPlayerAction()
        listenForQueueUpdates()
    }

    private fun listenForPlayerAction() {
        viewModelScope.launch {
            playerChannel.asFlow()
                .collect { handlePlayerAction(it) }
        }
    }

    private fun handlePlayerAction(action: ViewPlayerAction) {
        playerUpdates.value = action
    }

    fun getCurrentEpisode(): ViewPlayerAction? {
        return playerChannel.valueOrNull
    }

    private fun listenForQueueUpdates() {
        viewModelScope.launch {
            queueRepository.listenForQueueUpdates()
                .distinctUntilChanged()
                .collect {
                    queueSize.value = it.size
                }
            queueRepository.getQueue()
        }
    }
}
