package com.bakkenbaeck.poddy.db.handlers

import com.bakkenbaeck.poddy.db.mockData.podcastMockList
import com.bakkenbaeck.poddy.db.mockData.replyAllMock
import com.bakkenbaeck.poddy.di.testDBModule
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

class SubDbHandlerTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(testDBModule)
    }

    private val dbHandler: SubscriptionDBHandler by inject()

    @Test
    fun `select all - single subscribed podcast`() = runBlockingTest {
        dbHandler.insertSubscribedPodcast(replyAllMock)

        val podcasts = dbHandler.getSubscribedPodcasts()

        assertEquals(1, podcasts.count())
        assertEquals("11", podcasts[0].id)
    }

    @Test
    fun `select all - multiple subscribed podcast`() = runBlockingTest {
        podcastMockList.forEach {
            dbHandler.insertSubscribedPodcast(it)
        }

        val podcasts = dbHandler.getSubscribedPodcasts()

        assertEquals(podcastMockList.count(), podcasts.count())
        assertEquals("11", podcasts[0].id)
        assertEquals("12", podcasts[1].id)
        assertEquals("13", podcasts[2].id)
    }

    @Test
    fun `select all - no subscribed podcast`() = runBlockingTest {
        val podcasts = dbHandler.getSubscribedPodcasts()
        assertEquals(0, podcasts.count())
    }

    @Test
    fun `has subscribed - existing podcast`() = runBlockingTest {
        podcastMockList.forEach {
            dbHandler.insertSubscribedPodcast(it)
        }

        val hasSubscribed = dbHandler.hasSubscribed("11")

        assertEquals(true, hasSubscribed)
    }

    @Test
    fun `has subscribed - non existing podcast`() = runBlockingTest {
        podcastMockList.forEach {
            dbHandler.insertSubscribedPodcast(it)
        }

        val hasSubscribed = dbHandler.hasSubscribed("112")

        assertEquals(false, hasSubscribed)
    }

    @Test
    fun `delete - existing podcast`() = runBlockingTest {
        podcastMockList.forEach {
            dbHandler.insertSubscribedPodcast(it)
        }

        dbHandler.deleteSubscribedPodcast("11")
        val podcasts = dbHandler.getSubscribedPodcasts()
        val isSubscriptionDeleted = podcasts.filter { it.id == "11" }.count() == 0

        assertEquals(2, podcasts.count())
        assertEquals(true, isSubscriptionDeleted)
    }

    @Test
    fun `delete - non existing podcast`() = runBlockingTest {
        podcastMockList.forEach {
            dbHandler.insertSubscribedPodcast(it)
        }

        dbHandler.deleteSubscribedPodcast("112")
        val podcasts = dbHandler.getSubscribedPodcasts()

        assertEquals(podcastMockList.count(), podcasts.count())
    }
}