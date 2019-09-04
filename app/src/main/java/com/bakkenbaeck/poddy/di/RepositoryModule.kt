package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.repository.PodcastRepository
import com.bakkenbaeck.poddy.repository.QueueRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { PodcastRepository(get(), get(), get()) }
    single { QueueRepository(get(), get()) }
}
