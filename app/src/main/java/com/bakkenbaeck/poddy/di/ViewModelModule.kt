package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.presentation.MainViewModel
import com.bakkenbaeck.poddy.presentation.feed.DetailViewModel
import com.bakkenbaeck.poddy.presentation.feed.PodcastFeedViewModel
import com.bakkenbaeck.poddy.presentation.feed.SearchFeedViewModel
import com.bakkenbaeck.poddy.presentation.podcast.PodcastViewModel
import com.bakkenbaeck.poddy.presentation.queue.QueueViewModel
import com.bakkenbaeck.poddy.presentation.search.SearchViewModel
import com.bakkenbaeck.poddy.usecase.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        MainViewModel(
            playerChannel = get(named("playerChannel")),
            queueFlowUseCase = QueueFlowUseCase(get()),
            queueUseCase = QueueUseCase(get())
        )
    }
    viewModel { SearchViewModel(
        searchPodcastUseCase = PodcastSearchUseCase(get()),
        getCategoriesUseCase = GetCategoriesUseCase(get())
    ) }
    viewModel { QueueViewModel(
        queueFlowUseCase = QueueFlowUseCase(get()),
        reorderQueueUseCase = ReorderQueueUseCase(get()),
        deleteQueueUseCase = DeleteQueueUseCase(get())
    ) }
    viewModel { PodcastViewModel(getSubscribedPodcastsUseCase = GetSubscribedPodcastsUseCase(get())) }
    viewModel {
        DetailViewModel(
            progressChannel = get(named("progressChannel")),
            playerChannel = get(named("playerChannel")),
            playerQueue = get(),
            addToQueueUseCase = AddToQueueUseCase(get())
        )
    }

    viewModel { SearchFeedViewModel(
        downloadRepository = get(),
        downloadProgressChannel = get(named("progressChannel")),
        getPodcastUseCase = GetPodcastUseCase(get()),
        getEpisodeUseCase = GetEpisodeUseCase(get()),
        toggleSubscriptionUseCase = ToggleSubscriptionUseCase(get())
    ) }
    viewModel { PodcastFeedViewModel(
        downloadRepository = get(),
        downloadProgressChannel = get(named("progressChannel")),
        getPodcastUseCase = GetPodcastUseCase(get()),
        getEpisodeUseCase = GetEpisodeUseCase(get()),
        toggleSubscriptionUseCase = ToggleSubscriptionUseCase(get())
    ) }
}
