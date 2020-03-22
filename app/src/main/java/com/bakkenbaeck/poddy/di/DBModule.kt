package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.db.buildDatabase
import com.bakkenbaeck.poddy.db.handlers.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dbModule = module {
    single { buildDatabase(get()) }

    // Database handlers
    factory<EpisodeDBHandler> { EpisodeDBHandlerImpl(get(), get(named("IO"))) }
    factory<PodcastDBHandler> { PodcastDBHandlerImpl(get(), get(named("IO"))) }
    factory<QueueDBHandler> { QueueDBHandlerImpl(get(), get(named("IO"))) }
    factory<SubscriptionDBHandler> { SubscriptionDBHandlerImpl(get(), get(named("IO"))) }
}
