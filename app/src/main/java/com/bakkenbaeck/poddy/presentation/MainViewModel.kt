package com.bakkenbaeck.poddy.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction
import com.bakkenbaeck.poddy.usecase.QueueFlowUseCase
import com.bakkenbaeck.poddy.usecase.QueueUseCase
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val playerChannel: ConflatedBroadcastChannel<ViewPlayerAction?>,
    private val queueFlowUseCase: QueueFlowUseCase,
    private val queueUseCase: QueueUseCase
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
                .filterNotNull()
                .collect { handlePlayerAction(it) }
        }
    }

    // Add a player action if channel is empty or if the current action is a progress action
    private suspend fun addTopEpisodeFromQueueIfPlayerIsEmpty() {
        val queue = queueUseCase.execute()

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
            queueFlowUseCase.execute()
                .distinctUntilChanged()
                .collect {
                    queueSize.value = it.size
                }
        }
    }
}
