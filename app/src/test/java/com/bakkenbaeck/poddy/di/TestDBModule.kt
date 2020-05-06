package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.db.buildTestDB
import com.bakkenbaeck.poddy.db.handlers.*
import db.PoddyDB
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.koin.dsl.module

val testDBModule = module {
    single { buildTestDB() }
    factory { get<PoddyDB>().episodeQueries }
    factory { get<PoddyDB>().podcastQueries }
    factory { get<PoddyDB>().queueQueries }
    factory { get<PoddyDB>().subQueries }

    factory<SubscriptionDBHandler> {
        SubscriptionDBHandlerImpl(
            db = get(),
            context = TestCoroutineDispatcher()
        )
    }

    factory<QueueDBHandler> {
        QueueDBHandlerImpl(
            db = get(),
            context = TestCoroutineDispatcher()
        )
    }

    factory<EpisodeDBHandler> {
        EpisodeDBHandlerImpl(
            db = get(),
            context = TestCoroutineDispatcher()
        )
    }

    factory<PodcastDBHandler> {
        PodcastDBHandlerImpl(
            db = get(),
            context = TestCoroutineDispatcher()
        )
    }
}
