package com.bakkenbaeck.poddy.di


import com.bakkenbaeck.poddy.db.buildTestDB
import db.PoddyDB
import org.db.EpisodeQueries
import org.koin.core.qualifier.named
import org.koin.dsl.module

val testDBModule = module {
    single { buildTestDB() }
    factory { get<PoddyDB>().episodeQueries }
    factory { get<PoddyDB>().podcastQueries }
    factory { get<PoddyDB>().queueQueries }
    factory { get<PoddyDB>().subQueries }
}
