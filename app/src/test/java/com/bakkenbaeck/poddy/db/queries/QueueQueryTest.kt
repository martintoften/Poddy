package com.bakkenbaeck.poddy.db.queries

import com.bakkenbaeck.poddy.db.mockData.queueMockList
import com.bakkenbaeck.poddy.di.testDBModule
import org.db.QueueQueries
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

class QueueQueryTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(testDBModule)
    }

    private val queueQueries: QueueQueries by inject()

    @Test
    fun `select all - multiple items`() {
        queueMockList.forEach {
            queueQueries.insert(it)
        }

        val queue = queueQueries.selectAll().executeAsList()
        assertEquals(3, queue.count())
    }

    @Test
    fun `select all - empty queue`() {
        val queue = queueQueries.selectAll().executeAsList()
        assertEquals(0, queue.count())
    }

    @Test
    fun `update index`() {
        queueMockList.forEachIndexed { index, it ->
            queueQueries.insert(it)
            queueQueries.updateIndex(index.toLong(), it.episode_id)
        }

        queueQueries.updateIndex(0, "3")
        queueQueries.updateIndex(1, "1")
        queueQueries.updateIndex(2, "2")
        val queue = queueQueries.selectAll().executeAsList()

        assertEquals(3, queue.count())
        assertEquals("3", queue[0].episode_id)
        assertEquals(0, queue[0].queue_index)

        assertEquals("1", queue[1].episode_id)
        assertEquals(1, queue[1].queue_index)

        assertEquals("2", queue[2].episode_id)
        assertEquals(2, queue[2].queue_index)
    }

    @Test
    fun `update index - for non queue item`() {
        queueMockList.forEachIndexed { index, it ->
            queueQueries.insert(it)
            queueQueries.updateIndex(index.toLong(), it.episode_id)
        }

        queueQueries.updateIndex(0, "4")
        val queue = queueQueries.selectAll().executeAsList()

        assertEquals(3, queue.count())
        assertEquals("1", queue[0].episode_id)
        assertEquals(0, queue[0].queue_index)
        assertEquals("2", queue[1].episode_id)
        assertEquals(1, queue[1].queue_index)
        assertEquals("3", queue[2].episode_id)
        assertEquals(2, queue[2].queue_index)
    }

    @Test
    fun `select ordered queue - reversed episode ids`() {
        queueMockList.reversed().forEachIndexed { index, it ->
            queueQueries.insert(it)
            queueQueries.updateIndex(index.toLong(), it.episode_id)
        }

        val orderedQueue = queueQueries.selectEpisodeIdAll().executeAsList()

        assertEquals(3, orderedQueue.count())
        assertEquals("3", orderedQueue[0])
        assertEquals("2", orderedQueue[1])
        assertEquals("1", orderedQueue[2])
    }

    @Test
    fun `select ordered queue - empty`() {
        val orderedQueue = queueQueries.selectEpisodeIdAll().executeAsList()
        assertEquals(0, orderedQueue.count())
    }

    @Test
    fun `select ordered queue - reversed`() {
        queueMockList.reversed().forEachIndexed { index, it ->
            queueQueries.insert(it)
            queueQueries.updateIndex(index.toLong(), it.episode_id)
        }

        val orderedQueue = queueQueries.selectAll().executeAsList()

        assertEquals(3, orderedQueue.count())
        assertEquals("3", orderedQueue[0].episode_id)
        assertEquals(0, orderedQueue[0].queue_index)
        assertEquals("2", orderedQueue[1].episode_id)
        assertEquals(1, orderedQueue[1].queue_index)
        assertEquals("1", orderedQueue[2].episode_id)
        assertEquals(2, orderedQueue[2].queue_index)
    }

    @Test
    fun `already exists - existing item`() {
        queueMockList.forEach {
            queueQueries.insert(it)
        }

        val count = queueQueries.alreadyExist("1").executeAsOneOrNull()
        assertEquals(1L, count)
    }

    @Test
    fun `already exists - non existing item`() {
        queueMockList.forEach {
            queueQueries.insert(it)
        }

        val count = queueQueries.alreadyExist("1443").executeAsOneOrNull()
        assertEquals(0L, count)
    }

    @Test
    fun `delete - existing item`() {
        queueMockList.forEach {
            queueQueries.insert(it)
        }

        queueQueries.delete("1")
        val queue = queueQueries.selectAll().executeAsList()
        val isDeleted = queue.filter { it.episode_id == "1" }.count() == 0

        assertEquals(2, queue.count())
        assertEquals(true, isDeleted)
    }

    @Test
    fun `delete - non existing item`() {
        queueMockList.forEach {
            queueQueries.insert(it)
        }

        queueQueries.delete("1123")
        val queue = queueQueries.selectAll().executeAsList()

        assertEquals(3, queue.count())
    }
}