package com.bakkenbaeck.poddy.di

import androidx.lifecycle.SavedStateHandle
import com.bakkenbaeck.poddy.presentation.MainViewModel
import com.bakkenbaeck.poddy.presentation.feed.DetailViewModel
import com.bakkenbaeck.poddy.presentation.feed.PodcastDetailsViewModel
import com.bakkenbaeck.poddy.presentation.feed.PodcastFeedViewModel
import com.bakkenbaeck.poddy.presentation.feed.SearchFeedViewModel
import com.bakkenbaeck.poddy.presentation.podcast.PodcastViewModel
import com.bakkenbaeck.poddy.presentation.queue.QueueViewModel
import com.bakkenbaeck.poddy.presentation.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        MainViewModel(
            playerChannel = get(named("playerChannel")),
            queueRepository = get()
        )
    }
    viewModel { SearchViewModel(podcastRepository = get()) }
    viewModel { QueueViewModel(queueRepository = get()) }
    viewModel { PodcastViewModel(podcastRepository = get()) }
    viewModel {
        DetailViewModel(
            queueRepository = get(),
            progressChannel = get(named("progressChannel")),
            playerChannel = get(named("playerChannel")),
            playerQueue = get()
        )
    }

    viewModel { SearchFeedViewModel(
        podcastRepository = get(),
        downloadRepository = get(),
        downloadProgressChannel = get(named("progressChannel"))
    ) }
    viewModel { PodcastFeedViewModel(
        podcastRepository = get(),
        downloadRepository = get(),
        downloadProgressChannel = get(named("progressChannel"))
    ) }
}
