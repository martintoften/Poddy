package com.bakkenbaeck.poddy.db

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import db.PoddyDB
import org.db.Episode
import org.junit.Assert.assertEquals
import org.junit.Test

class EpisodeTest {

    private val inMemorySqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
        PoddyDB.Schema.create(this)
    }
    private val queries = PoddyDB(inMemorySqlDriver).episodeQueries

    /*@Test
    fun episodeTest() {
        val episode = Episode.Impl("1", "11", "Reply All", "Podcast about the internet", "1.1.1.1", 1000, "url")
        queries.insert(episode)
        val episodes = queries.selectAll().executeAsList()

        assertEquals(1, episodes.count())
    }*/
}
