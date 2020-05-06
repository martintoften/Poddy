package com.bakkenbaeck.poddy.db.queries

import com.bakkenbaeck.poddy.db.mockData.episodeMockList
import com.bakkenbaeck.poddy.db.mockData.multiplePerPodcastMockList
import com.bakkenbaeck.poddy.db.mockData.podcastMockList
import com.bakkenbaeck.poddy.db.mockData.replyAllMockEpisode
import com.bakkenbaeck.poddy.di.testDBModule
import org.db.EpisodeQueries
import org.db.PodcastQueries
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

class EpisodeQueryTest: KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(testDBModule)
    }

    private val episodeQueries: EpisodeQueries by inject()
    private val podcastQueries: PodcastQueries by inject()

    @Test
    fun `select all - single episode`() {
        episodeQueries.insert(replyAllMockEpisode)
        val episodes = episodeQueries.selectAll().executeAsList()
        assertEquals(1, episodes.count())
    }

    @Test
    fun `select all - multiple episodes`() {
        episodeMockList.forEach {
            episodeQueries.insert(it)
        }

        val episodes = episodeQueries.selectAll().executeAsList()
        assertEquals(3, episodes.count())
    }

    @Test
    fun `all episodes - multiple episodes`() {
        podcastMockList.forEach {
            podcastQueries.insert(it)
        }
        episodeMockList.forEach {
            episodeQueries.insert(it)
        }

        val episodes = episodeQueries.allEpisodes().executeAsList()
        assertEquals(3, episodes.count())
        assertEquals("1", episodes[0].id)
        assertEquals("2", episodes[1].id)
        assertEquals("3", episodes[2].id)
    }

    @Test
    fun `all episodes - no episodes`() {
        val episodes = episodeQueries.allEpisodes().executeAsList()
        assertEquals(0, episodes.count())
    }

    @Test
    fun `by id episode - single episode`() {
        podcastMockList.forEach {
            podcastQueries.insert(it)
        }
        episodeMockList.forEach {
            episodeQueries.insert(it)
        }

        val episodes = episodeQueries.byIdEpisode("1").executeAsList()
        assertEquals(1, episodes.count())
        assertEquals("1", episodes[0].id)
    }

    @Test
    fun `by id episode - no episodes`() {
        val episodes = episodeQueries.byIdEpisode("1").executeAsList()
        assertEquals(0, episodes.count())
    }

    @Test
    fun `by ids episode - multiple episode`() {
        podcastMockList.forEach {
            podcastQueries.insert(it)
        }
        episodeMockList.forEach {
            episodeQueries.insert(it)
        }

        val episodes = episodeQueries.byIdsEpisodes(listOf("1", "2")).executeAsList()
        assertEquals(2, episodes.count())
        assertEquals("1", episodes[0].id)
        assertEquals("2", episodes[1].id)
    }

    @Test
    fun `by ids episode - no episode`() {
        val episodes = episodeQueries.byIdsEpisodes(listOf("1", "2")).executeAsList()
        assertEquals(0, episodes.count())
    }

    @Test
    fun `by podcast id episode - multiple episodes`() {
        podcastMockList.forEach {
            podcastQueries.insert(it)
        }
        multiplePerPodcastMockList.forEach {
            episodeQueries.insert(it)
        }

        val episodes = episodeQueries.byPodcastIdEpisodes("11").executeAsList()
        assertEquals(2, episodes.count())
        assertEquals("1", episodes[0].id)
        assertEquals("4", episodes[1].id)
    }

    @Test
    fun `by podcast id episode - no episodes`() {
        val episodes = episodeQueries.byPodcastIdEpisodes("11").executeAsList()
        assertEquals(0, episodes.count())
    }

    @Test
    fun `by podcast id episode - incorrect podcast id`() {
        podcastMockList.forEach {
            podcastQueries.insert(it)
        }
        multiplePerPodcastMockList.forEach {
            episodeQueries.insert(it)
        }

        val episodes = episodeQueries.byPodcastIdEpisodes("14931").executeAsList()
        assertEquals(0, episodes.count())
    }

    @Test
    fun `delete by episode id, single episode`() {
        episodeMockList.forEach {
            episodeQueries.insert(it)
        }

        episodeQueries.deleteByEpisodeId("1")
        val episodes = episodeQueries.selectAll().executeAsList()
        val deletedEpisode = episodes.firstOrNull { it.id == "1" }
        assertEquals(2, episodes.count())
        assertEquals(null, deletedEpisode)
    }

    @Test
    fun `delete by episode id, no episode`() {
        episodeMockList.forEach {
            episodeQueries.insert(it)
        }

        episodeQueries.deleteByEpisodeId("13243242")
        val episodes = episodeQueries.selectAll().executeAsList()
        assertEquals(episodeMockList.count(), episodes.count())
    }

    @Test
    fun `delete by podcast id, multiple episodes`() {
        multiplePerPodcastMockList.forEach {
            episodeQueries.insert(it)
        }

        episodeQueries.deleteByPodcastId("11")
        val episodes = episodeQueries.selectAll().executeAsList()
        val isEpisodesDeleted = episodes.filter { it.id == "11" }.count() == 0
        assertEquals(2, episodes.count())
        assertEquals(true, isEpisodesDeleted)
    }

    @Test
    fun `delete by podcast id, single episode`() {
        multiplePerPodcastMockList.forEach {
            episodeQueries.insert(it)
        }

        episodeQueries.deleteByPodcastId("12")
        val episodes = episodeQueries.selectAll().executeAsList()
        val isEpisodesDeleted = episodes.filter { it.id == "12" }.count() == 0
        assertEquals(3, episodes.count())
        assertEquals(true, isEpisodesDeleted)
    }

    @Test
    fun `delete by podcast id, no episodes`() {
        multiplePerPodcastMockList.forEach {
            episodeQueries.insert(it)
        }

        episodeQueries.deleteByPodcastId("50")
        val episodes = episodeQueries.selectAll().executeAsList()
        assertEquals(multiplePerPodcastMockList.count(), episodes.count())
    }

    @Test
    fun `already exists, single episode`() {
        multiplePerPodcastMockList.forEach {
            episodeQueries.insert(it)
        }

        val alreadyExists = episodeQueries.alreadyExists(listOf("1")).executeAsOneOrNull()
        assertEquals(1L, alreadyExists)
    }

    @Test
    fun `already exists, multiple episodes`() {
        multiplePerPodcastMockList.forEach {
            episodeQueries.insert(it)
        }

        val alreadyExists = episodeQueries.alreadyExists(listOf("1", "2")).executeAsOneOrNull()
        assertEquals(2L, alreadyExists)
    }

    @Test
    fun `already exists, no episodes`() {
        multiplePerPodcastMockList.forEach {
            episodeQueries.insert(it)
        }

        val alreadyExists = episodeQueries.alreadyExists(listOf("90")).executeAsOneOrNull()
        assertEquals(0L, alreadyExists)
    }

    @Test
    fun `update download state, single episode`() {
        val episodeId = "1"

        podcastMockList.forEach {
            podcastQueries.insert(it)
        }
        episodeMockList.forEach {
            episodeQueries.insert(it)
        }

        episodeQueries.updateDownloadState(0L, episodeId)
        val episodeNotDownloaded = episodeQueries.byIdEpisode(episodeId).executeAsOneOrNull()
        episodeQueries.updateDownloadState(1L, episodeId)
        val episodeInProgress = episodeQueries.byIdEpisode(episodeId).executeAsOneOrNull()
        episodeQueries.updateDownloadState(2L, episodeId)
        val episodeDownloaded = episodeQueries.byIdEpisode(episodeId).executeAsOneOrNull()

        assertEquals(0L, episodeNotDownloaded?.is_downloaded)
        assertEquals(1L, episodeInProgress?.is_downloaded)
        assertEquals(2L, episodeDownloaded?.is_downloaded)
    }

    @Test
    fun `update download state, no episode`() {
        val episodeId = "3489"

        podcastMockList.forEach {
            podcastQueries.insert(it)
        }
        episodeMockList.forEach {
            episodeQueries.insert(it)
        }

        episodeQueries.updateDownloadState(0L, episodeId)
        val episodeNotDownloaded = episodeQueries.byIdEpisode(episodeId).executeAsOneOrNull()
        episodeQueries.updateDownloadState(1L, episodeId)
        val episodeInProgress = episodeQueries.byIdEpisode(episodeId).executeAsOneOrNull()
        episodeQueries.updateDownloadState(2L, episodeId)
        val episodeDownloaded = episodeQueries.byIdEpisode(episodeId).executeAsOneOrNull()

        assertEquals(null, episodeNotDownloaded)
        assertEquals(null, episodeInProgress)
        assertEquals(null, episodeDownloaded)
    }

    @Test
    fun `update progress state, single episode`() {
        val episodeId = "1"

        podcastMockList.forEach {
            podcastQueries.insert(it)
        }
        episodeMockList.forEach {
            episodeQueries.insert(it)
        }

        episodeQueries.updateProgressState(0L, episodeId)
        val episodeZero = episodeQueries.byIdEpisode(episodeId).executeAsOneOrNull()
        episodeQueries.updateProgressState(15L, episodeId)
        val episodeFifteen = episodeQueries.byIdEpisode(episodeId).executeAsOneOrNull()
        episodeQueries.updateProgressState(90L, episodeId)
        val episodeNinety = episodeQueries.byIdEpisode(episodeId).executeAsOneOrNull()

        assertEquals(0L, episodeZero?.progress)
        assertEquals(15L, episodeFifteen?.progress)
        assertEquals(90L, episodeNinety?.progress)
    }

    @Test
    fun `update progress state, no episode`() {
        val episodeId = "234324"

        podcastMockList.forEach {
            podcastQueries.insert(it)
        }
        episodeMockList.forEach {
            episodeQueries.insert(it)
        }

        episodeQueries.updateProgressState(0L, episodeId)
        val episodeZero = episodeQueries.byIdEpisode(episodeId).executeAsOneOrNull()
        episodeQueries.updateProgressState(15L, episodeId)
        val episodeFifteen = episodeQueries.byIdEpisode(episodeId).executeAsOneOrNull()
        episodeQueries.updateProgressState(90L, episodeId)
        val episodeNinety = episodeQueries.byIdEpisode(episodeId).executeAsOneOrNull()

        assertEquals(null, episodeZero)
        assertEquals(null, episodeFifteen)
        assertEquals(null, episodeNinety)
    }
}
