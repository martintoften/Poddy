package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.PodcastPlayer
import com.bakkenbaeck.poddy.PodcastPlayerImpl
import com.bakkenbaeck.poddy.util.EpisodePathHelper
import com.bakkenbaeck.poddy.util.EpisodePathHelperImpl
import com.bakkenbaeck.poddy.util.PlayerQueue
import org.koin.dsl.module

val playerModule = module {
    single { PlayerQueue() }
    factory<PodcastPlayer> { PodcastPlayerImpl() }
    factory<EpisodePathHelper> { EpisodePathHelperImpl() }
}