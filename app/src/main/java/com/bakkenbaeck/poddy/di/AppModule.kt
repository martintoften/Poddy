package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.download.DownloadHandler
import com.bakkenbaeck.poddy.network.DownloadProgressInterceptor
import com.bakkenbaeck.poddy.network.ProgressEvent
import com.bakkenbaeck.poddy.network.buildDownloadApi
import com.bakkenbaeck.poddy.network.buildSearchApi
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import org.db.Episode
import org.db.Podcast
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    // Network
    single { buildSearchApi() }
    single { buildDownloadApi(get()) }
    single { DownloadProgressInterceptor(get(named("progressChannel"))) }

    // Dispatchers
    factory(named("IO")) { Dispatchers.IO }

    // Channels
    single(named("progressChannel")) { ConflatedBroadcastChannel<ProgressEvent>() }
    single(named("playerChannel")) { ConflatedBroadcastChannel<ViewPlayerAction>() }
    single(named("subscriptionChannel")) { ConflatedBroadcastChannel<List<Podcast>>() }
    single(named("podcastChannel")) { ConflatedBroadcastChannel<Pair<Podcast, List<Episode>>?>() }
    single(named("downloadStateChannel")) { ConflatedBroadcastChannel<String?>() }
    single(named("queueChannel")) { ConflatedBroadcastChannel<List<Episode>>() }

    // Download
    factory { DownloadHandler(get(), get(named("IO"))) }
}
