package com.bakkenbaeck.poddy.db

import com.squareup.sqldelight.Query
import db.PoddyDB
import kotlinx.coroutines.withContext
import org.db.Episode
import org.db.Podcast
import kotlin.coroutines.CoroutineContext

class DBReader(
    private val db: PoddyDB,
    private val context: CoroutineContext
) {
    suspend fun getQueue(): List<Episode> {
        return withContext(context) {
            val queue = db.queueQueries.selectEpisodeIdAll().executeAsList()
            val episodeResult = db.episodeQueries.selectByIds(queue).executeAsList()
            return@withContext queue
                .map { episodeResult
                .first { episode ->  it == episode.id } } // Query order is ignored for episodes
        }
    }

    suspend fun getPodcasts(): List<Podcast> {
        return withContext(context) {
            return@withContext db.podcastQueries.selectAll().executeAsList()
        }
    }

    suspend fun doesAlreadyExist(id: String): Boolean {
        return withContext(context) {
            val result = db.queueQueries.doesAlreadyExist(id).executeAsOne()
            return@withContext result > 0
        }
    }
}
