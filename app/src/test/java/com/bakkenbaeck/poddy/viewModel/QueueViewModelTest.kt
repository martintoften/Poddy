package com.bakkenbaeck.poddy.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bakkenbaeck.poddy.db.handlers.QueueDBHandler
import com.bakkenbaeck.poddy.db.mockData.episodeMockList
import com.bakkenbaeck.poddy.di.*
import com.bakkenbaeck.poddy.presentation.queue.QueueViewModel
import com.bakkenbaeck.poddy.viewModel.util.MainCoroutineRule
import com.bakkenbaeck.poddy.viewModel.util.getOrAwaitValue
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

class QueueViewModelTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(testDBModule, testRepositoryModule, testChannelModule, viewModelModule, useCaseModule)
    }

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val viewModel: QueueViewModel by inject()
    private val queueDBHandler: QueueDBHandler by inject()

    @Test
    fun `get queue - multiple episodes`() = coroutineRule.runBlockingTest {
        episodeMockList.forEach {
            queueDBHandler.insertQueueItem(it)
        }
        val queue = viewModel.queue.getOrAwaitValue()
        assertEquals(3, queue.count())
    }

    @Test
    fun `get queue - no episodes`() = coroutineRule.runBlockingTest {
        val queue = viewModel.queue.getOrAwaitValue()
        assertEquals(0, queue.count())
    }

    @Test
    fun `delete episode - multiple episodes`() = coroutineRule.runBlockingTest {
        episodeMockList.forEach {
            queueDBHandler.insertQueueItem(it)
        }

        viewModel.deleteEpisode("3")
        val queue = viewModel.queue.getOrAwaitValue()

        assertEquals(2, queue.count())
        assertEquals("2", queue[0].id)
        assertEquals("1", queue[1].id)
    }

    @Test
    fun `delete episode - no episodes`() = coroutineRule.runBlockingTest {
        viewModel.deleteEpisode("3")
        val queue = viewModel.queue.getOrAwaitValue()
        assertEquals(0, queue.count())
    }

    @Test
    fun `reorder queue - multiple episodes`() = coroutineRule.runBlockingTest {
        episodeMockList.forEach {
            queueDBHandler.insertQueueItem(it)
        }

        val reversedQueue = queueDBHandler.getQueue().reversed().map { it.id }
        viewModel.reorderQueue(reversedQueue)
        val queue = queueDBHandler.getQueue()

        assertEquals(3, queue.count())
        assertEquals("1", queue[0].id)
        assertEquals("2", queue[1].id)
        assertEquals("3", queue[2].id)
    }

    @Test
    fun `reorder queue - no episodes`() = coroutineRule.runBlockingTest {
        val reversedQueue = queueDBHandler.getQueue().reversed().map { it.id }
        viewModel.reorderQueue(reversedQueue)
        val queue = queueDBHandler.getQueue()
        assertEquals(0, queue.count())
    }
}