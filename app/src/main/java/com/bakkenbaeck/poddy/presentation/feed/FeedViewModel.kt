package com.bakkenbaeck.poddy.presentation.feed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.network.ProgressEvent
import com.bakkenbaeck.poddy.presentation.mappers.mapToViewPodcastFromDB
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import com.bakkenbaeck.poddy.repository.DownloadRepository
import com.bakkenbaeck.poddy.repository.PodcastRepository
import com.bakkenbaeck.poddy.repository.QueueRepository
import com.bakkenbaeck.poddy.util.PlayerQueue
import com.bakkenbaeck.poddy.util.Resource
import com.bakkenbaeck.poddy.util.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FeedViewModel(
    private val podcastRepository: PodcastRepository,
    private val queueRepository: QueueRepository,
    private val downloadRepository: DownloadRepository,
    private val progressChannel: ConflatedBroadcastChannel<ProgressEvent>,
    private val playerChannel: ConflatedBroadcastChannel<ViewPlayerAction>,
    private val playerQueue: PlayerQueue
) : ViewModel() {

    private var selectedEpisode: ViewEpisode? = null
    private val downloadUpdates by lazy { MutableLiveData<ProgressEvent>() }
    val playerUpdates by lazy { MutableLiveData<ViewPlayerAction>() }

    init {
        listenForPlayerAction()
        listenForDownloadUpdates()
        listenForFinishedDownloads()
    }

    private fun listenForPlayerAction() {
        viewModelScope.launch {
            playerChannel.asFlow()
                .filter { it.episode.id == selectedEpisode?.id }
                .collect { playerUpdates.value = it }
        }
    }

    fun isEpisodePlaying(episode: ViewEpisode): Boolean {
        val currentEpisode = playerQueue.current() ?: return false
        val action = playerChannel.valueOrNull ?: return false
        return episode.id == currentEpisode.id
                && (action is ViewPlayerAction.Play
                || action is ViewPlayerAction.Start
                || action is ViewPlayerAction.Progress)
    }

    private fun listenForDownloadUpdates() {
        viewModelScope.launch {
            progressChannel
                .asFlow()
                .collect { downloadUpdates.value = it }
        }
    }

    private fun listenForFinishedDownloads() {
        viewModelScope.launch {
            downloadRepository.listenForDownloadStateUpdates()
                .filterNotNull()
                .collect { podcastRepository.broadcastUpdatedPodcast(it) }
        }
    }

    fun setCurrentEpisode(episode: ViewEpisode) {
        selectedEpisode = episode
    }

    fun addToQueue() {
        val episode = selectedEpisode ?: return

        viewModelScope.launch {
            queueRepository.addToQueue(episode)
        }
    }
}
