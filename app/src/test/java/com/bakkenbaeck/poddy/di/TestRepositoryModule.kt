package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction
import com.bakkenbaeck.poddy.repository.ProgressRepository
import com.bakkenbaeck.poddy.repository.QueueRepository
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import org.db.Episode
import org.koin.core.qualifier.named
import org.koin.dsl.module

val testChannelModule = module {
    single(named(QUEUE_CHANNEL)) { ConflatedBroadcastChannel<List<Episode>>() }
    single(named(PLAYER_CHANNEL)) { ConflatedBroadcastChannel<ViewPlayerAction?>() }
}

val testRepositoryModule = module {
    factory { QueueRepository(get(), get(), get(named(QUEUE_CHANNEL))) }
    factory { ProgressRepository(get()) }
}