package com.bakkenbaeck.poddy.di


import com.bakkenbaeck.poddy.db.buildTestDB
import org.koin.core.qualifier.named
import org.koin.dsl.module

/*val testDBModule = module {
    single { buildTestDB() }
    single { DBWriter(get(), get(named("IO"))) }
    single { DBReader(get(), get(named("IO"))) }
}*/
