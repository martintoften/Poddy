package com.bakkenbaeck.poddy.repository

import com.bakkenbaeck.poddy.db.mockData.episodeMockList
import com.bakkenbaeck.poddy.db.mockData.replyAll2MockEpisode
import com.bakkenbaeck.poddy.db.mockData.replyAllMockEpisode
import com.bakkenbaeck.poddy.di.testChannelModule
import com.bakkenbaeck.poddy.di.testDBModule
import com.bakkenbaeck.poddy.di.testRepositoryModule
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

class QueueRepositoryTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(testDBModule, testRepositoryModule, testChannelModule)
    }

    private val queueRepository: QueueRepository by inject()

    @Test
    fun `add episode to queue - single episode`() = runBlockingTest {
        queueRepository.addToQueue(replyAllMockEpisode)
        val queue = queueRepository.getQueue()
        assertEquals(1, queue.count())
    }

    @Test
    fun `add episode to queue - multiple episodes`() = runBlockingTest {
        episodeMockList.forEach {
            queueRepository.addToQueue(it)
        }

        val queue = queueRepository.getQueue()

        assertEquals(3, queue.count())
        assertEquals("3", queue[0].id)
        assertEquals("2", queue[1].id)
        assertEquals("1", queue[2].id)
    }

    @Test
    fun `get queue - empty`() = runBlockingTest {
        val queue = queueRepository.getQueue()
        assertEquals(0, queue.count())
    }

    @Test
    fun `delete episode from queue - empty queue`() = runBlockingTest {
        queueRepository.deleteEpisodeFromQueue("11")
        val queue = queueRepository.getQueue()
        assertEquals(0, queue.count())
    }

    @Test
    fun `delete episode from queue - single episode`() = runBlockingTest {
        episodeMockList.forEach {
            queueRepository.addToQueue(it)
        }

        queueRepository.deleteEpisodeFromQueue("1")
        val queue = queueRepository.getQueue()
        val isEpisodeDeleted = queue.filter { it.id == "1" }.count() == 0

        assertEquals(2, queue.count())
        assertEquals(true, isEpisodeDeleted)
    }

    @Test
    fun `reorder queue`() = runBlockingTest {
        episodeMockList.forEach {
            queueRepository.addToQueue(it)
        }

        val queue = queueRepository.getQueue()
        val reversedQueue = queue.reversed().map { it.id }
        queueRepository.reorderQueue(reversedQueue)
        val resultQueue = queueRepository.getQueue()

        assertEquals(3, resultQueue.count())
        assertEquals("1", resultQueue[0].id)
        assertEquals("2", resultQueue[1].id)
        assertEquals("3", resultQueue[2].id)
    }

    @Test
    fun `queue flow - multiple episodes`() = runBlockingTest {
        episodeMockList.forEach {
            queueRepository.addToQueue(it)
        }

        val result = queueRepository.getQueueFlow().take(1).toList()

        assertEquals(1, result.count())
        assertEquals(3, result[0].count())
    }

    @Test
    fun `queue flow - no episodes`() = runBlockingTest {
        val result = queueRepository.getQueueFlow().take(1).toList()

        assertEquals(1, result.count())
        assertEquals(0, result[0].count())
    }

    @Test
    fun `queue flow - add`() = runBlockingTest {
        episodeMockList.forEach {
            queueRepository.addToQueue(it)
        }

        val queueFlow = queueRepository.getQueueFlow()

        val resultWithThreeEpisodes = queueFlow.take(1).toList()

        assertEquals(1, resultWithThreeEpisodes.count())
        assertEquals(3, resultWithThreeEpisodes[0].count())

        queueRepository.addToQueue(replyAll2MockEpisode)

        val resultWithFourEpisodes = queueFlow.take(1).toList()

        assertEquals(1, resultWithFourEpisodes.count())
        assertEquals(4, resultWithFourEpisodes[0].count())
    }

    @Test
    fun `queue flow - remove`() = runBlockingTest {
        episodeMockList.forEach {
            queueRepository.addToQueue(it)
        }

        val queueFlow = queueRepository.getQueueFlow()

        val resultWithThreeEpisodes = queueFlow.take(1).toList()

        assertEquals(1, resultWithThreeEpisodes.count())
        assertEquals(3, resultWithThreeEpisodes[0].count())

        queueRepository.deleteEpisodeFromQueue("1")

        val resultWithFourEpisodes = queueFlow.take(1).toList()

        assertEquals(1, resultWithFourEpisodes.count())
        assertEquals(2, resultWithFourEpisodes[0].count())
    }
}