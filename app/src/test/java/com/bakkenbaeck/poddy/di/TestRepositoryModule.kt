package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.repository.FeedRepository
import com.bakkenbaeck.poddy.repository.PodcastRepository
import org.koin.dsl.module

val testRepositoryModule = module {
    single { PodcastRepository(get()) }
    single { FeedRepository(get(), get(), get()) }
}
