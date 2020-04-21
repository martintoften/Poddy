package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.repository.DownloadRepository
import com.bakkenbaeck.poddy.repository.PodcastRepository
import com.bakkenbaeck.poddy.repository.ProgressRepository
import com.bakkenbaeck.poddy.repository.QueueRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    factory {
        PodcastRepository(
            get(),
            get(),
            get(),
            get(),
            get(named("subscriptionChannel"))
        )
    }
    factory { QueueRepository(get(), get(), get(named("queueChannel"))) }
    factory { DownloadRepository(get(), get(), get(named("downloadStateChannel"))) }
    factory { ProgressRepository(get()) }
}
