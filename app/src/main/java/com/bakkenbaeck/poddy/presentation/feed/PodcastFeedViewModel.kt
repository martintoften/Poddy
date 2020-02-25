package com.bakkenbaeck.poddy.presentation.feed

import com.bakkenbaeck.poddy.repository.PodcastRepository

class PodcastFeedViewModel(
    podcastRepository: PodcastRepository
) : BaseFeedViewModel(podcastRepository)
