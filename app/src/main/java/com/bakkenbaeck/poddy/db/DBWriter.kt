package com.bakkenbaeck.poddy.db

import db.PoddyDB
import kotlinx.coroutines.withContext
import org.db.Episode
import org.db.Queue
import kotlin.coroutines.CoroutineContext

class DBWriter(
    private val db: PoddyDB,
    private val context: CoroutineContext
) {
    suspend fun insertQueueItem(queue: Queue, episode: Episode) {
        return withContext(context) {
            db.episodeQueries.insert(episode)
            db.queueQueries.insert(queue)
        }
    }
}
