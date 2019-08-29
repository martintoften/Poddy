package com.bakkenbaeck.poddy.presentation.feed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.model.Channel
import com.bakkenbaeck.poddy.model.Rss
import com.bakkenbaeck.poddy.repository.FeedRepository
import com.bakkenbaeck.poddy.util.Loading
import com.bakkenbaeck.poddy.util.Resource
import com.bakkenbaeck.poddy.util.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class FeedViewModel(
    private val feedRepository: FeedRepository
) : ViewModel() {

    val feedResult = MutableLiveData<Resource<Rss>>()
    var selectedEpisode: Channel.Item? = null
    var channelImage: String? = null

    fun getFeed(feedUrl: String) {
        viewModelScope.launch {
            feedResult.value = Loading()
            feedRepository.getFeed(feedUrl)
                .flowOn(Dispatchers.IO)
                .collect { handleFeedResult(it) }
        }
    }

    private fun handleFeedResult(rss: Rss) {
        feedResult.value = Success(rss)
    }

    fun setCurrentEpisode(episode: Channel.Item, imageUrl: String?) {
        selectedEpisode = episode
        channelImage = imageUrl
    }

    fun addToQueue() {
        val episode = selectedEpisode ?: return
        val channel = getChannel() ?: return

        viewModelScope.launch {
            feedRepository.addToQueue(episode, channel, channelImage)
        }
    }

    private fun getChannel(): Channel? {
        return when (val rss = feedResult.value) {
            is Success -> rss.data.channel
            else -> null
        }
    }
}
