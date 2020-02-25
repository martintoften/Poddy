package com.bakkenbaeck.poddy.presentation.feed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.presentation.mappers.mapToViewPodcastFromDB
import com.bakkenbaeck.poddy.presentation.model.Subscribed
import com.bakkenbaeck.poddy.presentation.model.SubscriptionState
import com.bakkenbaeck.poddy.presentation.model.Unsubscribed
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import com.bakkenbaeck.poddy.repository.PodcastRepository
import com.bakkenbaeck.poddy.util.Failure
import com.bakkenbaeck.poddy.util.Loading
import com.bakkenbaeck.poddy.util.Resource
import com.bakkenbaeck.poddy.util.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseFeedViewModel(
    private val podcastRepository: PodcastRepository
) : ViewModel() {
    val feedResult by lazy { MutableLiveData<Resource<ViewPodcast>>() }
    val subscriptionState by lazy { MutableLiveData<SubscriptionState>() }

    fun getFeed(id: String, lastTimestamp: Long? = null) {
        if (feedResult.value is Loading) return

        viewModelScope.launch {
            feedResult.value = Loading()
            podcastRepository.getPodcastFlow(id, lastTimestamp)
                .filterNotNull()
                .flatMapMerge { podcast -> podcastRepository.hasSubscribed(podcast.first)
                    .map { hasSubscribed -> mapToViewPodcastFromDB(podcast.first, podcast.second, hasSubscribed) }
                }
                .flowOn(Dispatchers.IO)
                .catch { handleFeedError(it) }
                .collect { handleFeed(it) }
        }
    }

    private fun handleFeedError(error: Throwable) {
        subscriptionState.value = Unsubscribed()
        feedResult.value = Failure(error)
    }

    private fun handleFeed(podcast: ViewPodcast) {
        val subState = if (podcast.hasSubscribed) Subscribed() else Unsubscribed()
        subscriptionState.value = subState
        feedResult.value = Success(podcast)
    }

    fun addPodcast() {
        val podcast = getPodcast() ?: return

        viewModelScope.launch {
            podcastRepository.toggleSubscription(podcast)
                .map { if (it) Unsubscribed() else Subscribed() }
                .collect {
                    subscriptionState.value = it
                }
        }
    }

    private fun getPodcast(): ViewPodcast? {
        return when (val podcast = feedResult.value) {
            is Success -> podcast.data
            else -> null
        }
    }
}
