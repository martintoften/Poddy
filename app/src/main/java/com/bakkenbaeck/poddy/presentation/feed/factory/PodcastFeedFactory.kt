package com.bakkenbaeck.poddy.presentation.feed.factory

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.bakkenbaeck.poddy.network.ProgressEvent
import com.bakkenbaeck.poddy.presentation.feed.PodcastFeedViewModel
import com.bakkenbaeck.poddy.useCase.DownloadStateFlowUseCase
import com.bakkenbaeck.poddy.useCase.GetEpisodeUseCase
import com.bakkenbaeck.poddy.useCase.GetPodcastUseCase
import com.bakkenbaeck.poddy.useCase.ToggleSubscriptionUseCase
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

class PodcastFeedModelFactory(
    private val downloadStateFlowUseCase: DownloadStateFlowUseCase,
    private val downloadProgressChannel: ConflatedBroadcastChannel<ProgressEvent>,
    private val getPodcastUseCase: GetPodcastUseCase,
    private val getEpisodeUseCase: GetEpisodeUseCase,
    private val toggleSubscriptionUseCase: ToggleSubscriptionUseCase,
    private val podcastId: String?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PodcastFeedViewModel(
            downloadStateFlowUseCase,
            downloadProgressChannel,
            getPodcastUseCase,
            getEpisodeUseCase,
            toggleSubscriptionUseCase,
            podcastId
        ) as T
    }
}