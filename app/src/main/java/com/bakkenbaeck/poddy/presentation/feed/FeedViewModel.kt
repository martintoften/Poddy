package com.bakkenbaeck.poddy.presentation.feed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.model.EpisodeItem
import com.bakkenbaeck.poddy.model.PodcastResponse
import com.bakkenbaeck.poddy.repository.FeedRepository
import com.bakkenbaeck.poddy.repository.SearchRepository
import com.bakkenbaeck.poddy.util.Loading
import com.bakkenbaeck.poddy.util.Resource
import com.bakkenbaeck.poddy.util.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class FeedViewModel(
    private val searchRepository: SearchRepository,
    private val feedRepository: FeedRepository
) : ViewModel() {

    val feedResult = MutableLiveData<Resource<PodcastResponse>>()
    var selectedEpisode: EpisodeItem? = null

    fun getFeed(id: String) {
        viewModelScope.launch {
            feedResult.value = Loading()
            searchRepository.getEpisodes(id)
                .flowOn(Dispatchers.IO)
                .collect { handleFeedResult(it) }
        }
    }

    private fun handleFeedResult(rss: PodcastResponse) {
        feedResult.value = Success(rss)
    }

    fun setCurrentEpisode(episode: EpisodeItem) {
        selectedEpisode = episode
    }

    fun addToQueue() {
        val episode = selectedEpisode ?: return
        val channel = getChannel() ?: return

        viewModelScope.launch {
            feedRepository.addToQueue(channel, episode)
        }
    }

    fun addPodcast() {
        val channel = getChannel() ?: return

        viewModelScope.launch {
            feedRepository.addPodcast(channel)
        }
    }

    private fun getChannel(): PodcastResponse? {
        val channel = feedResult.value

        return when (channel) {
            is Success -> channel.data
            else -> null
        }
    }
}
