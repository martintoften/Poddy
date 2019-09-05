package com.bakkenbaeck.poddy.presentation.feed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.network.ProgressEvent
import com.bakkenbaeck.poddy.presentation.mappers.mapToViewPodcastFromDB
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import com.bakkenbaeck.poddy.repository.DownloadRepository
import com.bakkenbaeck.poddy.repository.PodcastRepository
import com.bakkenbaeck.poddy.repository.QueueRepository
import com.bakkenbaeck.poddy.util.Loading
import com.bakkenbaeck.poddy.util.Resource
import com.bakkenbaeck.poddy.util.Success
import com.bakkenbaeck.poddy.util.createNewFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class FeedViewModel(
    private val podcastRepository: PodcastRepository,
    private val queueRepository: QueueRepository,
    private val downloadRepository: DownloadRepository,
    private val progressChannel: ConflatedBroadcastChannel<ProgressEvent>
) : ViewModel() {

    var selectedEpisode: ViewEpisode? = null
    val feedResult by lazy { MutableLiveData<Resource<ViewPodcast>>() }
    val downloadUpdates by lazy { MutableLiveData<ProgressEvent>() }

    init {
        listenForDownloadUpdates()
        listenForPodcastUpdates()
    }

    private fun listenForDownloadUpdates() {
        viewModelScope.launch {
            progressChannel
                .asFlow()
                .collect { handleDownloadResult(it) }
        }
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

    fun downloadFile(id: String, url: String, dir: File) {
        viewModelScope.launch {
            val name = id.plus(".mp3")
            val podcastFile = createNewFile(dir, name)
            downloadRepository.downloadPodcast(id, url, podcastFile)
        }
    }
}
