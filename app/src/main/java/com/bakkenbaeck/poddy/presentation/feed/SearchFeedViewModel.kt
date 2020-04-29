package com.bakkenbaeck.poddy.presentation.feed

import com.bakkenbaeck.poddy.network.ProgressEvent
import com.bakkenbaeck.poddy.useCase.DownloadStateFlowUseCase
import com.bakkenbaeck.poddy.useCase.GetEpisodeUseCase
import com.bakkenbaeck.poddy.useCase.GetPodcastUseCase
import com.bakkenbaeck.poddy.useCase.ToggleSubscriptionUseCase
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

class SearchFeedViewModel(
    downloadStateFlowUseCase: DownloadStateFlowUseCase,
    downloadProgressChannel: ConflatedBroadcastChannel<ProgressEvent>,
    getPodcastUseCase: GetPodcastUseCase,
    getEpisodeUseCase: GetEpisodeUseCase,
    toggleSubscriptionUseCase: ToggleSubscriptionUseCase,
    podcastId: String?
) : BaseFeedViewModel(
    downloadProgressChannel,
    getPodcastUseCase,
    getEpisodeUseCase,
    toggleSubscriptionUseCase,
    downloadStateFlowUseCase,
    podcastId
)
