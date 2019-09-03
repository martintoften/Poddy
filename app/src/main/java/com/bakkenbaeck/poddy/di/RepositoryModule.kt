package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.repository.PodcastRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { PodcastRepository(get(), get(), get()) }
}
