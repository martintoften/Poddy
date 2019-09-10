package com.bakkenbaeck.poddy.db

import db.PoddyDB
import kotlinx.coroutines.runBlocking
import org.db.Episode
import org.db.Queue
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

class QueueTest: KoinTest {

    private val db: PoddyDB by inject()
    /*private val dbWriter: DBWriter by inject()
    private val dbReader: DBReader by inject()

    private val testEpisodes = listOf(
        Episode.Impl("4", "11", "#144 Dark Pattern", "Description", "Date", 1000, "image"),
        Episode.Impl("1", "11", "#127 The Crime Machine Part 1", "Description", "Date", 1000, "image"),
        Episode.Impl("3", "11", "#130 The Snapchat Thief", "Description", "Date", 1000, "image"),
        Episode.Impl("2", "11", "#128 The Crime Machine Part 2", "Description", "Date", 1000, "image")
    )

    private val testQueue = listOf(
        Queue.Impl("1", "11", 0),
        Queue.Impl("4", "11", 1),
        Queue.Impl("2", "11", 2),
        Queue.Impl("3", "11", 3)
    )

    private val newTestEpisode = Episode.Impl("5", "11", "#96 Fog of covfefe", "Description", "Date", 1000, "image")
    private val newQueueItem = Queue.Impl("5", "11", -1)

    @Before
    fun before() {
        startKoin {
            modules(listOf(testDBModule, testAppModule, testRepositoryModule))
        }

        testEpisodes.forEach {
            db.episodeQueries.insert(it)
        }

        testQueue.forEach {
            db.queueQueries.insert(it)
        }
    }

    @Test
    fun `add episode to queue`() = runBlocking {
        dbWriter.insertQueueItem(newQueueItem, newTestEpisode)

        val queue = dbReader.getQueue()
        assertEquals(5, queue.size)

        assertEquals("5", queue[0].id)
        assertEquals("#96 Fog of covfefe", queue[0].title)

        assertEquals("1", queue[1].id)
        assertEquals("#127 The Crime Machine Part 1", queue[1].title)

        assertEquals("3", queue.last().id)
        assertEquals("#130 The Snapchat Thief", queue.last().title)
    }

    @Test
    fun `reorder queue`() = runBlocking {
        val reorderedQueue = listOf("1", "2", "3", "4")
        dbWriter.reorderQueue(reorderedQueue)
        val queueResult = dbReader.getQueue()

        assertEquals("1", queueResult[0].id)
        assertEquals("2", queueResult[1].id)
        assertEquals("3", queueResult[2].id)
        assertEquals("4", queueResult[3].id)
    }

    @After
    fun after() {
        stopKoin()
    }*/
}
