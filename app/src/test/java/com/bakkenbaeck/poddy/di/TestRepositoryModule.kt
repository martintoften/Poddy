package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.repository.QueueRepository
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import org.db.Episode
import org.koin.core.qualifier.named
import org.koin.dsl.module

val testChannelModule = module {
    single(named(QUEUE_CHANNEL)) { ConflatedBroadcastChannel<List<Episode>>() }
}

val testRepositoryModule = module {
    factory { QueueRepository(get(), get(), get(named(QUEUE_CHANNEL))) }
}