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
import kotlinx.coroutines.flow.onStart
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
                .onStart { addTopEpisodeFromQueueIfPlayerIsEmpty() }
                .collect { handlePlayerAction(it) }
        }
    }

    // Add a player action if channel is empty or if the current action is a progress action
    private suspend fun addTopEpisodeFromQueueIfPlayerIsEmpty() {
        val queue = queueRepository.getQueue()

        if (queue.isNotEmpty() && playerChannel.valueOrNull == null) {
            val topEpisode = queue.first()
            val playerAction = ViewPlayerAction.Pause(topEpisode)
            playerChannel.send(playerAction)
        } else if (queue.isNotEmpty() && playerChannel.valueOrNull != null) {
            val currentPlayerAction = playerChannel.value
            val topEpisode = queue.first()

            if (currentPlayerAction is ViewPlayerAction.Progress) {
                val playerAction = ViewPlayerAction.Play(topEpisode)
                playerChannel.send(playerAction)
            }
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
            queueRepository.getQueue()
            queueRepository.listenForQueueUpdates()
                .distinctUntilChanged()
                .collect {
                    queueSize.value = it.size
                }
        }
    }
}
