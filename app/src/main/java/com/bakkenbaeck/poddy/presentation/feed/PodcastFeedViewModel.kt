package com.bakkenbaeck.poddy.presentation.feed

import com.bakkenbaeck.poddy.network.ProgressEvent
import com.bakkenbaeck.poddy.repository.DownloadRepository
import com.bakkenbaeck.poddy.repository.PodcastRepository
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

class PodcastFeedViewModel(
    podcastRepository: PodcastRepository,
    downloadRepository: DownloadRepository,
    downloadProgressChannel: ConflatedBroadcastChannel<ProgressEvent>
) : BaseFeedViewModel(podcastRepository, downloadRepository, downloadProgressChannel)
