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
import com.bakkenbaeck.poddy.service.ACTION_PLAY
import com.bakkenbaeck.poddy.service.ACTION_START
import com.bakkenbaeck.poddy.util.Loading
import com.bakkenbaeck.poddy.util.Resource
import com.bakkenbaeck.poddy.util.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

class FeedViewModel(
    private val podcastRepository: PodcastRepository,
    private val queueRepository: QueueRepository,
    private val downloadRepository: DownloadRepository,
    private val progressChannel: ConflatedBroadcastChannel<ProgressEvent>,
    private val playerChannel: ConflatedBroadcastChannel<ViewPlayerAction>
) : ViewModel() {

    var selectedEpisode: ViewEpisode? = null
    val feedResult by lazy { MutableLiveData<Resource<ViewPodcast>>() }
    val downloadUpdates by lazy { MutableLiveData<ProgressEvent>() }
    val playerUpdates by lazy { MutableLiveData<ViewPlayerAction>() }

    init {
        listenForPlayerAction()
        listenForDownloadUpdates()
        listenForFinishedDownloads()
        listenForPodcastUpdates()
    }

    private fun listenForPlayerAction() {
        viewModelScope.launch {
            playerChannel.asFlow()
                .filter { filterEpisode(it.episode, selectedEpisode) }
                .collect { handlePlayerAction(it) }
        }
    }

    fun isEpisodePlaying(episode: ViewEpisode): Boolean {
        val action = playerChannel.valueOrNull ?: return false
        return episode.id == action.episode.id
                && (action is ViewPlayerAction.Play || action is ViewPlayerAction.Start)
    }

    private fun filterEpisode(episode: ViewEpisode, selectedEpisode: ViewEpisode?): Boolean {
        return episode.id == selectedEpisode?.id
    }

    private fun listenForDownloadUpdates() {
        viewModelScope.launch {
            progressChannel
                .asFlow()
                .collect { handleDownloadResult(it) }
        }
    }

    private fun listenForFinishedDownloads() {
        viewModelScope.launch {
            downloadRepository.listenForDownloadStateUpdates()
                .filterNotNull()
                .collect { podcastRepository.updatePodcast(it) }
        }
    }

    private fun handlePlayerAction(action: ViewPlayerAction) {
        playerUpdates.value = action
    }

    private fun handleDownloadResult(progressEvent: ProgressEvent) {
        downloadUpdates.value = progressEvent
    }

    private fun listenForPodcastUpdates() {
        viewModelScope.launch {
            podcastRepository.listenForPodcastUpdates()
                .filterNotNull()
                .flatMapMerge { podcast -> podcastRepository.hasSubscribed(podcast.first)
                    .map { hasSubscribed -> mapToViewPodcastFromDB(podcast.first, podcast.second, hasSubscribed) }
                }
                .flowOn(Dispatchers.IO)
                .collect { handleFeedResult(it) }
        }
    }

    private fun handleFeedResult(podcast: ViewPodcast) {
        feedResult.value = Success(podcast)
    }

    fun getFeed(id: String, lastTimestamp: Long? = null) {
        if (feedResult.value is Loading) return

        viewModelScope.launch {
            feedResult.value = Loading()
            podcastRepository.getPodcast(id, lastTimestamp)
        }
    }

    fun setCurrentEpisode(episode: ViewEpisode) {
        selectedEpisode = episode
    }

    fun addToQueue() {
        val episode = selectedEpisode ?: return
        val channel = getPodcast() ?: return

        viewModelScope.launch {
            queueRepository.addToQueue(channel, episode)
        }
    }

    fun addPodcast() {
        val podcast = getPodcast() ?: return

        viewModelScope.launch {
            podcastRepository.subscribeOrUnsubscribeToPodcast(podcast)
        }
    }

    private fun getPodcast(): ViewPodcast? {
        return when (val podcast = feedResult.value) {
            is Success -> podcast.data
            else -> null
        }
    }
}
