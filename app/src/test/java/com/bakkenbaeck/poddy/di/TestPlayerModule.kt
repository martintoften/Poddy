package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.presentation.notification.PlayerNotificationHandler
import com.bakkenbaeck.poddy.presentation.notification.TestPlayerNotificationHandlerImpl
import com.bakkenbaeck.poddy.presentation.player.*
import com.bakkenbaeck.poddy.util.EpisodePathHelper
import com.bakkenbaeck.poddy.util.TestEpisodePathHelperImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.koin.core.qualifier.named
import org.koin.dsl.module

val testPlayerModule = module {
    single { PlayerQueue() }
    single<PodcastPlayer> { TestPodcastPlayerImpl() }
    single<PlayerNotificationHandler> { TestPlayerNotificationHandlerImpl() }
    factory<EpisodePathHelper> { TestEpisodePathHelperImpl() }
    factory { PlayerActionBuilder(get()) }
    single { PlayerHandler(
        get(),
        get(named(PLAYER_CHANNEL)),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        Dispatchers.IO,
        TestCoroutineDispatcher()
    ) }
}