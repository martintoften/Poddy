package com.bakkenbaeck.poddy.db

import android.util.Log
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
            db.queueQueries.selectEpisodeIdAll()
            val queue = db.queueQueries.selectEpisodeIdAll().executeAsList()
            val episodes = db.episodeQueries.selectByIds(queue).executeAsList()

            Log.d("Yo", episodes.size.toString())

            return@withContext episodes
        }
    }
}
