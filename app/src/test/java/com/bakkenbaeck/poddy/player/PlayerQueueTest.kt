package com.bakkenbaeck.poddy.player

import com.bakkenbaeck.poddy.player.mockData.serialMockEpisode
import com.bakkenbaeck.poddy.player.mockData.viewEpisodeMockList
import com.bakkenbaeck.poddy.presentation.player.PlayerQueue
import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerQueueTest {

    @Test
    fun `current - empty state`() {
        val playerQueue = PlayerQueue()
        assertEquals(null, playerQueue.current())
    }

    @Test
    fun `current - with episode as current`() {
        val playerQueue = PlayerQueue()
        playerQueue.setCurrent(serialMockEpisode)
        assertEquals(serialMockEpisode, playerQueue.current())
    }

    @Test
    fun `current - with multiple episodes queue, no current`() {
        val playerQueue = PlayerQueue()
        playerQueue.setQueue(viewEpisodeMockList)
        assertEquals(null, playerQueue.current())
    }

    @Test
    fun `current - with multiple episodes queue, with current`() {
        val playerQueue = PlayerQueue()
        playerQueue.setQueue(viewEpisodeMockList)
        playerQueue.setCurrent(viewEpisodeMockList[0])
        assertEquals(viewEpisodeMockList[0], playerQueue.current())
    }

    @Test
    fun `current - with multiple episodes queue, with current, clear first`() {
        val playerQueue = PlayerQueue()
        playerQueue.setQueue(viewEpisodeMockList)
        playerQueue.setCurrent(viewEpisodeMockList[0])
        playerQueue.clearCurrentEpisode()
        assertEquals(null, playerQueue.current())
    }

    @Test
    fun `has current - empty state`() {
        val playerQueue = PlayerQueue()
        assertEquals(false, playerQueue.hasCurrent())
    }

    @Test
    fun `has current - with episodes queue`() {
        val playerQueue = PlayerQueue()
        playerQueue.setQueue(viewEpisodeMockList)
        assertEquals(false, playerQueue.hasCurrent())
    }

    @Test
    fun `has current - with episodes queue, cleared current`() {
        val playerQueue = PlayerQueue()
        playerQueue.setQueue(viewEpisodeMockList)
        playerQueue.setCurrent(viewEpisodeMockList[0])
        playerQueue.clearCurrentEpisode()
        assertEquals(false, playerQueue.hasCurrent())
    }

    @Test
    fun `first - empty state`() {
        val playerQueue = PlayerQueue()
        assertEquals(null, playerQueue.first())
    }

    @Test
    fun `first - with episodes queue`() {
        val playerQueue = PlayerQueue()
        playerQueue.setQueue(viewEpisodeMockList)
        assertEquals(viewEpisodeMockList[0], playerQueue.first())
    }

    @Test
    fun `set queue - twice`() {
        val playerQueue = PlayerQueue()
        playerQueue.setQueue(viewEpisodeMockList)
        playerQueue.setQueue(viewEpisodeMockList.subList(1, 2))
        assertEquals(viewEpisodeMockList.last(), playerQueue.first())
    }
}