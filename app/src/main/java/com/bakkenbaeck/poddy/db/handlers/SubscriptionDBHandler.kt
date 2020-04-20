package com.bakkenbaeck.poddy.db.handlers

import db.PoddyDB
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.withContext
import org.db.Podcast
import org.db.Subscription

interface SubscriptionDBHandler {
    suspend fun getSubscribedPodcasts(): List<Podcast>
    suspend fun doesSubscribedPodcastAlreadyExist(podcastId: String): Boolean
    suspend fun insertSubscribedPodcast(podcast: Podcast)
    suspend fun deleteSubscribedPodcast(podcastId: String)
}

class SubscriptionDBHandlerImpl(
    private val db: PoddyDB,
    private val context: CoroutineContext
) : SubscriptionDBHandler {
    override suspend fun getSubscribedPodcasts(): List<Podcast> {
        return withContext(context) {
            val ids = db.subQueries.selectAll().executeAsList()
            return@withContext db.podcastQueries.selectByIds(ids).executeAsList()
        }
    }

    override suspend fun doesSubscribedPodcastAlreadyExist(podcastId: String): Boolean {
        return withContext(context) {
            val result = db.subQueries.doesAlreadyExist(podcastId).executeAsOne()
            return@withContext result > 0
        }
    }

    override suspend fun insertSubscribedPodcast(podcast: Podcast) {
        return withContext(context) {
            db.subQueries.insert(Subscription.Impl(podcast.id))
            db.podcastQueries.insert(podcast)
        }
    }

    override suspend fun deleteSubscribedPodcast(podcastId: String) {
        return withContext(context) {
            db.podcastQueries.delete(podcastId)
            db.subQueries.delete(podcastId)
        }
    }
}
