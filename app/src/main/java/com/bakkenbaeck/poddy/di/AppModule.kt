package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.download.DownloadHandler
import com.bakkenbaeck.poddy.network.DownloadProgressInterceptor
import com.bakkenbaeck.poddy.network.ProgressEvent
import com.bakkenbaeck.poddy.network.buildDownloadApi
import com.bakkenbaeck.poddy.network.buildSearchApi
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single { buildSearchApi() }
    single { buildDownloadApi(get()) }
    single { DownloadProgressInterceptor(get(named("progressChannel"))) }
    factory(named("IO")) { Dispatchers.IO }
    single { DownloadHandler(get(), get(named("IO"))) }
    single(named("progressChannel")) { ConflatedBroadcastChannel<ProgressEvent>() }
    single(named("playerChannel")) { ConflatedBroadcastChannel<ViewPlayerAction>() }
}
