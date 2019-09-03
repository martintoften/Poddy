package com.bakkenbaeck.poddy.presentation.feed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.presentation.mappers.mapToViewEpisodeFromNetwork
import com.bakkenbaeck.poddy.presentation.mappers.mapToViewPodcastFromDB
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import com.bakkenbaeck.poddy.repository.PodcastRepository
import com.bakkenbaeck.poddy.util.Loading
import com.bakkenbaeck.poddy.util.Resource
import com.bakkenbaeck.poddy.util.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FeedViewModel(
    private val podcastRepository: PodcastRepository
) : ViewModel() {

    private var selectedEpisode: ViewEpisode? = null
    val feedResult = MutableLiveData<Resource<ViewPodcast>>()

    fun getFeed(id: String, lastTimestamp: Long? = null) {
        if (feedResult.value is Loading) return

        viewModelScope.launch {
            feedResult.value = Loading()
            podcastRepository.getPodcast(id, lastTimestamp)
                .flowOn(Dispatchers.IO)
                .flatMapMerge { podcast -> podcastRepository.hasSubscribed(podcast.first)
                    .map { hasSubscribed -> mapToViewPodcastFromDB(podcast.first, podcast.second, hasSubscribed) }
                }
                .collect { handleFeedResult(it) }
        }
    }

    private fun handleFeedResult(podcast: ViewPodcast) {
        feedResult.value = Success(podcast)
    }

    fun setCurrentEpisode(episode: ViewEpisode) {
        selectedEpisode = episode
    }

    fun addToQueue() {
        val episode = selectedEpisode ?: return
        val channel = getPodcast() ?: return

        viewModelScope.launch {
            podcastRepository.addToQueue(channel, episode)
        }
    }

    fun addPodcast() {
        val podcast = getPodcast() ?: return

        viewModelScope.launch {
            podcastRepository.subscribeOrUnsubscribeToPodcast(podcast)
        }
    }

    private fun getPodcast(): ViewPodcast? {
        val podcast = feedResult.value

        return when (podcast) {
            is Success -> podcast.data
            else -> null
        }
    }
}
