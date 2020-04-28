package com.bakkenbaeck.poddy.presentation.feed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.network.ProgressEvent
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction
import com.bakkenbaeck.poddy.useCase.AddToQueueUseCase
import com.bakkenbaeck.poddy.presentation.player.PlayerQueue
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class DetailViewModel(
    private val progressChannel: ConflatedBroadcastChannel<ProgressEvent>,
    private val playerChannel: ConflatedBroadcastChannel<ViewPlayerAction?>,
    private val playerQueue: PlayerQueue,
    private val addToQueueUseCase: AddToQueueUseCase
) : ViewModel() {

    private var selectedEpisode: ViewEpisode? = null
    private val downloadUpdates by lazy { MutableLiveData<ProgressEvent>() }
    val playerUpdates by lazy { MutableLiveData<ViewPlayerAction>() }

    init {
        listenForPlayerAction()
        listenForDownloadUpdates()
    }

    private fun listenForPlayerAction() {
        viewModelScope.launch {
            playerChannel.asFlow()
                .filterNotNull()
                .filter { it.episode.id == selectedEpisode?.id }
                .collect { playerUpdates.value = it }
        }
    }

    fun isEpisodePlaying(episode: ViewEpisode): Boolean {
        val currentEpisode = playerQueue.current() ?: return false
        val action = playerChannel.valueOrNull ?: return false
        return episode.id == currentEpisode.id &&
                (action is ViewPlayerAction.Play ||
                action is ViewPlayerAction.Start ||
                action is ViewPlayerAction.Progress)
    }

    private fun listenForDownloadUpdates() {
        viewModelScope.launch {
            progressChannel
                .asFlow()
                .collect { downloadUpdates.value = it }
        }
    }

    fun setCurrentEpisode(episode: ViewEpisode) {
        selectedEpisode = episode
    }

    fun addToQueue() {
        val episode = selectedEpisode ?: return

        viewModelScope.launch {
            addToQueueUseCase.execute(episode)
        }
    }
}
