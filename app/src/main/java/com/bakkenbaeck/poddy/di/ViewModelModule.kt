package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.presentation.MainViewModel
import com.bakkenbaeck.poddy.presentation.feed.DetailViewModel
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
            queueFlowUseCase = get(),
            queueUseCase = get()
        )
    }
    viewModel { SearchViewModel(
        searchPodcastUseCase = get(),
        getCategoriesUseCase = get()
    ) }
    viewModel { QueueViewModel(
        queueFlowUseCase = get(),
        reorderQueueUseCase = get(),
        deleteQueueUseCase = get()
    ) }
    viewModel { PodcastViewModel(getSubscribedPodcastsUseCase = get()) }
    viewModel {
        DetailViewModel(
            progressChannel = get(named("progressChannel")),
            playerChannel = get(named("playerChannel")),
            playerQueue = get(),
            addToQueueUseCase = get()
        )
    }

    viewModel { SearchFeedViewModel(
        downloadStateFlowUseCase = get(),
        downloadProgressChannel = get(named("progressChannel")),
        getPodcastUseCase = get(),
        getEpisodeUseCase = get(),
        toggleSubscriptionUseCase = get()
    ) }
    viewModel { PodcastFeedViewModel(
        downloadStateFlowUseCase = get(),
        downloadProgressChannel = get(named("progressChannel")),
        getPodcastUseCase = get(),
        getEpisodeUseCase = get(),
        toggleSubscriptionUseCase = get()
    ) }
}
