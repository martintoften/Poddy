package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.db.DBReader
import com.bakkenbaeck.poddy.db.DBWriter
import com.bakkenbaeck.poddy.db.buildDatabase
import com.bakkenbaeck.poddy.network.buildRssApi
import com.bakkenbaeck.poddy.network.buildSearchApi
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single { buildRssApi() }
    single { buildSearchApi() }
    single { buildDatabase(get()) }
    single { DBWriter(get(), get(named("IO"))) }
    single { DBReader(get(), get(named("IO"))) }
    factory(named("IO")) { Dispatchers.IO }
}
