package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.db.buildDatabase
import com.bakkenbaeck.poddy.db.handlers.*
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dbModule = module {
    single { buildDatabase(get()) }

    // Database handlers
    factory<EpisodeDBHandler> { EpisodeDBHandlerImpl(get(), Dispatchers.IO) }
    factory<PodcastDBHandler> { PodcastDBHandlerImpl(get(), Dispatchers.IO) }
    factory<QueueDBHandler> { QueueDBHandlerImpl(get(), Dispatchers.IO) }
    factory<SubscriptionDBHandler> { SubscriptionDBHandlerImpl(get(), Dispatchers.IO) }
}
