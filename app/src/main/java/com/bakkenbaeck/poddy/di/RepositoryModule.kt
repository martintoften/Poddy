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
            get(named(SUBSCRIPTION_CHANNEL))
        )
    }
    factory { QueueRepository(get(), get(), get(named(QUEUE_CHANNEL))) }
    factory { DownloadRepository(get(), get(), get(named(DOWNLOAD_CHANNEL))) }
    factory { ProgressRepository(get()) }
}
