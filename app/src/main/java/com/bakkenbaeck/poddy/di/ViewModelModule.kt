package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.presentation.feed.FeedViewModel
import com.bakkenbaeck.poddy.presentation.podcast.PodcastViewModel
import com.bakkenbaeck.poddy.presentation.queue.QueueViewModel
import com.bakkenbaeck.poddy.presentation.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SearchViewModel(get()) }
    viewModel { FeedViewModel(get(), get()) }
    viewModel { QueueViewModel(get()) }
    viewModel { PodcastViewModel(get()) }
}
