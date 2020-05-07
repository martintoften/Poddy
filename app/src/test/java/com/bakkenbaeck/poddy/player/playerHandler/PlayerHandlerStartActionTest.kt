package com.bakkenbaeck.poddy.player.playerHandler

import com.bakkenbaeck.poddy.di.*
import com.bakkenbaeck.poddy.player.mockData.serialMockEpisode
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction
import com.bakkenbaeck.poddy.presentation.player.ACTION_START
import com.bakkenbaeck.poddy.presentation.player.PlayerHandler
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

class PlayerHandlerStartActionTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(testPlayerModule, testDBModule, testChannelModule, testRepositoryModule, testUseCaseModule)
    }

    private val playerHandler: PlayerHandler by inject()
    private val playerChannel: ConflatedBroadcastChannel<ViewPlayerAction?> by inject(named(PLAYER_CHANNEL))

    @Test
    fun `add start action`() = runBlocking {
        playerHandler.init()
        playerHandler.handlePlayerAction(ACTION_START, serialMockEpisode)
        val actions = playerChannel.asFlow().filterNotNull().take(3).toList()

        assertTrue(actions[0] is ViewPlayerAction.Start)
        assertEquals(serialMockEpisode.id, actions[0].episode.id)
        assertTrue(actions[1] is ViewPlayerAction.Progress)
        assertTrue(actions[2] is ViewPlayerAction.Progress)

        playerHandler.destroy()
    }
}