package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.network.buildRssApi
import com.bakkenbaeck.poddy.network.buildSearchApi
import org.koin.dsl.module

val appModule = module {
    single { buildRssApi() }
    single { buildSearchApi() }
}
