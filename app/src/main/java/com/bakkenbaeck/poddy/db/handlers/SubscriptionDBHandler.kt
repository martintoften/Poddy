package com.bakkenbaeck.poddy.db.handlers

import db.PoddyDB
import kotlinx.coroutines.withContext
import org.db.Podcast
import org.db.Subscription
import kotlin.coroutines.CoroutineContext

class SubscriptionDBHandler(
    private val db: PoddyDB,
    private val context: CoroutineContext
) {
    suspend fun getSubscribedPodcasts(): List<Podcast> {
        return withContext(context) {
            val ids = db.subQueries.selectAll().executeAsList()
            return@withContext db.podcastQueries.selectByIds(ids).executeAsList()
        }
    }

    suspend fun doesSubscribedPodcastAlreadyExist(podcastId: String): Boolean {
        return withContext(context) {
            val result = db.subQueries.doesAlreadyExist(podcastId).executeAsOne()
            return@withContext result > 0
        }
    }

    suspend fun insertSubscribedPodcast(podcast: Podcast) {
        return withContext(context) {
            db.subQueries.insert(Subscription.Impl(podcast.id))
            db.podcastQueries.insert(podcast)
        }
    }

    suspend fun deleteSubscribedPodcast(podcastId: String) {
        return withContext(context) {
            db.podcastQueries.delete(podcastId)
            db.subQueries.delete(podcastId)
        }
    }
}
