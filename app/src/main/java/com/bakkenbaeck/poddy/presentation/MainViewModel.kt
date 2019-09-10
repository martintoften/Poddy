package com.bakkenbaeck.poddy.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(
    private val playerChannel: ConflatedBroadcastChannel<ViewPlayerAction>
) : ViewModel() {

    val playerUpdates by lazy { MutableLiveData<ViewPlayerAction>() }

    init {
        listenForPlayerAction()
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
}
