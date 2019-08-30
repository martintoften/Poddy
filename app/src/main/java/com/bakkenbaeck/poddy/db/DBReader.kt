package com.bakkenbaeck.poddy.db

import db.PoddyDB
import kotlinx.coroutines.withContext
import org.db.Episode
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
}
