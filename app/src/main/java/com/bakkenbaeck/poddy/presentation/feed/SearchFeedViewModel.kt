package com.bakkenbaeck.poddy.presentation.feed

import com.bakkenbaeck.poddy.network.ProgressEvent
import com.bakkenbaeck.poddy.repository.DownloadRepository
import com.bakkenbaeck.poddy.usecase.GetEpisodeUseCase
import com.bakkenbaeck.poddy.usecase.GetPodcastUseCase
import com.bakkenbaeck.poddy.usecase.ToggleSubscriptionUseCase
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

class SearchFeedViewModel(
    downloadRepository: DownloadRepository,
    downloadProgressChannel: ConflatedBroadcastChannel<ProgressEvent>,
    getPodcastUseCase: GetPodcastUseCase,
    getEpisodeUseCase: GetEpisodeUseCase,
    toggleSubscriptionUseCase: ToggleSubscriptionUseCase
) : BaseFeedViewModel(downloadRepository, downloadProgressChannel, getPodcastUseCase, getEpisodeUseCase, toggleSubscriptionUseCase)
