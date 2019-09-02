package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.repository.FeedRepository
import com.bakkenbaeck.poddy.repository.SearchRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { SearchRepository(get()) }
    single { FeedRepository(get(), get()) }
}
