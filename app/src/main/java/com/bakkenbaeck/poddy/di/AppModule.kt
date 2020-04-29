package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.network.*
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import org.db.Episode
import org.db.Podcast
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val PROGRESS_CHANNEL = "progressChannel"
const val PLAYER_CHANNEL = "playerChannel"
const val SUBSCRIPTION_CHANNEL = "subscriptionChannel"
const val DOWNLOAD_CHANNEL = "downloadStateChannel"
const val QUEUE_CHANNEL = "queueChannel"

val appModule = module {
    // Network
    single { buildSearchApi() }
    single { buildDownloadApi(get()) }
    single { DownloadProgressInterceptor(get(named(PROGRESS_CHANNEL))) }

    // Channels
    single(named(PROGRESS_CHANNEL)) { ConflatedBroadcastChannel<ProgressEvent?>() }
    single(named(PLAYER_CHANNEL)) { ConflatedBroadcastChannel<ViewPlayerAction>() }
    single(named(SUBSCRIPTION_CHANNEL)) { ConflatedBroadcastChannel<List<Podcast>>() }
    single(named(DOWNLOAD_CHANNEL)) { ConflatedBroadcastChannel<String?>() }
    single(named(QUEUE_CHANNEL)) { ConflatedBroadcastChannel<List<Episode>>() }

    // Download
    factory {
        DownloadHandler(
            get(),
            Dispatchers.IO
        )
    }
}
