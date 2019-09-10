package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.network.buildSearchApi
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

/*val testAppModule = module {
    single { buildRssApi() }
    single { buildSearchApi() }
    factory(named("IO")) { Dispatchers.IO }
}*/
