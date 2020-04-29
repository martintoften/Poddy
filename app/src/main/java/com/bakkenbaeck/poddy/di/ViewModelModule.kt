package com.bakkenbaeck.poddy.di

import androidx.lifecycle.SavedStateHandle
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
            playerChannel = get(named(PLAYER_CHANNEL)),
            queueFlowUseCase = get(),
            queueUseCase = get()
        )
    }
    viewModel { (savedState: SavedStateHandle) -> SearchViewModel(
        savedState,
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
            progressChannel = get(named(PROGRESS_CHANNEL)),
            playerChannel = get(named(PLAYER_CHANNEL)),
            playerQueue = get(),
            addToQueueUseCase = get()
        )
    }
}
