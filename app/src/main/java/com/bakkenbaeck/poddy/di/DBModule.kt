package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.db.buildDatabase
import com.bakkenbaeck.poddy.db.handlers.EpisodeDBHandler
import com.bakkenbaeck.poddy.db.handlers.PodcastDBHandler
import com.bakkenbaeck.poddy.db.handlers.QueueDBHandler
import com.bakkenbaeck.poddy.db.handlers.SubscriptionDBHandler
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dbModule = module {
    single { buildDatabase(get()) }

    // Database handlers
    factory { EpisodeDBHandler(get(), get(named("IO"))) }
    factory { PodcastDBHandler(get(), get(named("IO"))) }
    factory { QueueDBHandler(get(), get(named("IO"))) }
    factory { SubscriptionDBHandler(get(), get(named("IO"))) }
}
