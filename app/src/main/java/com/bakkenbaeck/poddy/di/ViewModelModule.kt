package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.presentation.feed.FeedViewModel
import com.bakkenbaeck.poddy.presentation.podcast.PodcastViewModel
import com.bakkenbaeck.poddy.presentation.queue.QueueViewModel
import com.bakkenbaeck.poddy.presentation.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SearchViewModel(podcastRepository = get()) }
    viewModel { QueueViewModel(queueRepository = get()) }
    viewModel { PodcastViewModel(podcastRepository = get()) }
    viewModel {
        FeedViewModel(
            podcastRepository = get(),
            queueRepository = get(),
            downloadRepository = get(),
            progressChannel = get(named("progressChannel")),
            playerChannel = get(named("playerChannel")))
    }
}
