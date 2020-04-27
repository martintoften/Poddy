package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.usecase.GetPodcastRecommendationsUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetPodcastRecommendationsUseCase(get()) }
}