package com.bakkenbaeck.poddy.presentation.feed

import com.bakkenbaeck.poddy.repository.DownloadRepository
import com.bakkenbaeck.poddy.repository.PodcastRepository

class SearchFeedViewModel(
    podcastRepository: PodcastRepository,
    downloadRepository: DownloadRepository
) : BaseFeedViewModel(podcastRepository, downloadRepository)
