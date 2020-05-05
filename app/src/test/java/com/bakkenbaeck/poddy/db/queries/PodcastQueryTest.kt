package com.bakkenbaeck.poddy.db.queries

import com.bakkenbaeck.poddy.db.mockData.*
import com.bakkenbaeck.poddy.di.testDBModule
import org.db.EpisodeQueries
import org.db.PodcastQueries
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

class PodcastQueryTest: KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(testDBModule)
    }

    private val podcastQueries: PodcastQueries by inject()
    private val episodeQueries: EpisodeQueries by inject()

    @Test
    fun `select all - single podcast`() {
        podcastQueries.insert(replyAllMock)
        val podcasts = podcastQueries.selectAll().executeAsList()
        assertEquals(1, podcasts.count())
    }

    @Test
    fun `select all - multiple podcasts`() {
        podcastMockList.forEach {
            podcastQueries.insert(it)
        }
        val podcasts = podcastQueries.selectAll().executeAsList()
        assertEquals(3, podcasts.count())
    }

    @Test
    fun `delete - single podcast`() {
        listOf(replyAllMock, serialMock).forEach {
            podcastQueries.insert(it)
        }

        podcastQueries.delete("11")
        val podcasts = podcastQueries.selectAll().executeAsList()

        assertEquals(1, podcasts.count())
        assertEquals("13", podcasts[0].id)
    }

    @Test
    fun `select by ids - two podcasts`() {
        podcastMockList.forEach {
            podcastQueries.insert(it)
        }

        val podcasts = podcastQueries.selectByIds(listOf("11", "12")).executeAsList()

        assertEquals(2, podcasts.count())
        assertEquals("11", podcasts[0].id)
        assertEquals("12", podcasts[1].id)
    }

    @Test
    fun `select by ids - no podcasts`() {
        podcastMockList.forEach {
            podcastQueries.insert(it)
        }

        val podcasts = podcastQueries.selectByIds(listOf("9", "10")).executeAsList()

        assertEquals(0, podcasts.count())
    }

    @Test
    fun `already exists - single podcast`() {
        podcastMockList.forEach {
            podcastQueries.insert(it)
        }

        val result = podcastQueries.doesAlreadyExist("11").executeAsOne()

        assertEquals(1, result)
    }

    @Test
    fun `already exists - no podcast`() {
        podcastMockList.forEach {
            podcastQueries.insert(it)
        }

        val result = podcastQueries.doesAlreadyExist("10").executeAsOne()

        assertEquals(0, result)
    }

    @Test
    fun `select by episode id - single podcast`() {
        episodeQueries.insert(replyAllMockEpisode)
        podcastMockList.forEach {
            podcastQueries.insert(it)
        }

        val result = podcastQueries.selectByEpisodeId("1").executeAsList()

        assertEquals(1, result.count())
        assertEquals("11", result[0].id)
    }

    @Test
    fun `select by episode id - no podcast`() {
        episodeQueries.insert(replyAllMockEpisode)
        podcastMockList.forEach {
            podcastQueries.insert(it)
        }

        val result = podcastQueries.selectByEpisodeId("101").executeAsList()

        assertEquals(0, result.count())
    }
}
