package com.bakkenbaeck.poddy.repository

import com.bakkenbaeck.poddy.model.Rss
import com.bakkenbaeck.poddy.network.RssApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FeedRepository(
    private val rssApi: RssApi
) {
    fun getFeed(url: String): Flow<Rss> {
        return flow {
            val result = rssApi.getFeed(url)
            emit(result)
        }
    }
}
