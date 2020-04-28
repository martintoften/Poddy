package com.bakkenbaeck.poddy.presentation.feed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.network.ProgressEvent
import com.bakkenbaeck.poddy.network.Result
import com.bakkenbaeck.poddy.presentation.model.*
import com.bakkenbaeck.poddy.usecase.*
import com.bakkenbaeck.poddy.util.Failure
import com.bakkenbaeck.poddy.util.Loading
import com.bakkenbaeck.poddy.util.Resource
import com.bakkenbaeck.poddy.util.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseFeedViewModel(
    private val downloadProgressChannel: ConflatedBroadcastChannel<ProgressEvent>,
    private val getPodcastUseCase: GetPodcastUseCase,
    private val getEpisodeUseCase: GetEpisodeUseCase,
    private val toggleSubscriptionUseCase: ToggleSubscriptionUseCase,
    private val downloadStateFlowUseCase: DownloadStateFlowUseCase
) : ViewModel() {

    val downloadResult by lazy { MutableLiveData<ViewEpisode>() }
    val downloadProgress by lazy { MutableLiveData<ViewEpisode>() }
    val feedResult by lazy { MutableLiveData<Resource<ViewPodcast>>() }
    val subscriptionState by lazy { MutableLiveData<SubscriptionState>() }

    init {
        listenForDownloadUpdates()
        listenForDownloadProgressUpdates()
    }

    fun getFeed(id: String, lastTimestamp: Long? = null) {
        if (feedResult.value is Loading) return

        viewModelScope.launch {
            feedResult.value = Loading()
            getPodcastUseCase.execute(GetPodcastQuery(id, lastTimestamp))
                .filterNotNull()
                .flowOn(Dispatchers.IO)
                .catch { handleFeedError(it) }
                .collect { handleFeed(it) }
        }
    }

    private fun handleFeedError(error: Throwable) {
        subscriptionState.value = Unsubscribed()
        feedResult.value = Failure(error)
    }

    private fun handleFeed(podcast: Result<ViewPodcast>) {
        when (podcast) {
            is Result.Success -> {
                val subState = if (podcast.value.hasSubscribed) Subscribed() else Unsubscribed()
                subscriptionState.value = subState
                feedResult.value = Success(podcast.value)
            }
            is Result.Error -> {} // Handle
        }
    }

    fun addPodcast() {
        val podcast = getPodcast() ?: return

        viewModelScope.launch {
            toggleSubscriptionUseCase.execute(podcast)
                .collect {
                    subscriptionState.value = it
                }
        }
    }

    fun getPodcast(): ViewPodcast? {
        return when (val podcast = feedResult.value) {
            is Success -> podcast.data
            else -> null
        }
    }

    private fun listenForDownloadUpdates() {
        viewModelScope.launch {
            downloadStateFlowUseCase.execute()
                .filterNotNull()
                .map { getEpisodeUseCase.execute(it) }
                .filterNotNull()
                .flowOn(Dispatchers.IO)
                .collect { downloadResult.value = it }
        }
    }

    private fun listenForDownloadProgressUpdates() {
        viewModelScope.launch {
            downloadProgressChannel.asFlow()
                .map {
                    val episode = getEpisodeUseCase.execute(it.identifier) ?: return@map null
                    return@map episode.copy(downloadProgress = it.getFormattedProgress())
                }
                .filterNotNull()
                .flowOn(Dispatchers.IO)
                .collect { downloadProgress.value = it }
        }
    }
}
