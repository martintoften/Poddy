package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.usecase.*
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetPodcastRecommendationsUseCase(get()) }
    factory { QueueFlowUseCase(get()) }
    factory { AddToQueueUseCase(get()) }
    factory { QueueUseCase(get()) }
    factory { PodcastSearchUseCase(get()) }
    factory { GetCategoriesUseCase(get()) }
    factory { ReorderQueueUseCase(get()) }
    factory { DeleteQueueUseCase(get()) }
    factory { GetSubscribedPodcastsUseCase(get()) }
    factory { GetPodcastUseCase(get()) }
    factory { GetEpisodeUseCase(get()) }
    factory { ToggleSubscriptionUseCase(get()) }
}