package com.bakkenbaeck.poddy.db.handlers

import com.bakkenbaeck.poddy.db.mockData.episodeMockList
import com.bakkenbaeck.poddy.db.mockData.podcastMockList
import com.bakkenbaeck.poddy.db.mockData.replyAllMock
import com.bakkenbaeck.poddy.db.mockData.replyAllMockEpisode
import com.bakkenbaeck.poddy.di.testDBModule
import kotlinx.coroutines.test.runBlockingTest
import org.db.PodcastQueries
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

class QueueDbHandlerTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(testDBModule)
    }

    private val queueDbHandler: QueueDBHandler by inject()
    private val podcastQueries: PodcastQueries by inject()

    @Test
    fun `get queue - single episode`() = runBlockingTest {
        podcastQueries.insert(replyAllMock)
        queueDbHandler.insertQueueItem(replyAllMockEpisode)
        val queue = queueDbHandler.getQueue()
        assertEquals(1, queue.count())
    }

    @Test
    fun `get queue - multiple episodes`() = runBlockingTest {
        podcastMockList.forEach {
            podcastQueries.insert(it)
        }
        episodeMockList.forEach {
            queueDbHandler.insertQueueItem(it)
        }

        val queue = queueDbHandler.getQueue()
        assertEquals(episodeMockList.count(), queue.count())
    }

    @Test
    fun `get queue - multiple episodes no podcast`() = runBlockingTest {
        episodeMockList.forEach {
            queueDbHandler.insertQueueItem(it)
        }

        val queue = queueDbHandler.getQueue()
        assertEquals(0, queue.count())
    }

    @Test
    fun `reorder queue - multiple episodes`() = runBlockingTest {
        podcastMockList.forEach {
            podcastQueries.insert(it)
        }
        episodeMockList.forEach {
            queueDbHandler.insertQueueItem(it)
        }
        val queue = queueDbHandler.getQueue().map { it.id }
        val reversedQueue = queue.reversed()
        queueDbHandler.reorderQueue(reversedQueue)
        val reorderedQueue = queueDbHandler.getQueue()

        assertEquals(3, reorderedQueue.count())
        assertEquals("1", reorderedQueue[0].id)
        assertEquals("2", reorderedQueue[1].id)
        assertEquals("3", reorderedQueue[2].id)
    }

    @Test
    fun `reorder queue - no episodes`() = runBlockingTest {
        val queue = queueDbHandler.getQueue().map { it.id }
        val reversedQueue = queue.reversed()
        queueDbHandler.reorderQueue(reversedQueue)
        val reorderedQueue = queueDbHandler.getQueue()

        assertEquals(0, reorderedQueue.count())
    }

    @Test
    fun `already exists - existing episode`() = runBlockingTest {
        episodeMockList.forEach {
            queueDbHandler.insertQueueItem(it)
        }
        val alreadyExists = queueDbHandler.doesEpisodeAlreadyExist("1")
        assertEquals(true, alreadyExists)
    }

    @Test
    fun `already exists - non existing episode`() = runBlockingTest {
        episodeMockList.forEach {
            queueDbHandler.insertQueueItem(it)
        }
        val alreadyExists = queueDbHandler.doesEpisodeAlreadyExist("112")
        assertEquals(false, alreadyExists)
    }

}