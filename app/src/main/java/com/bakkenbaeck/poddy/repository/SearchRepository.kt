package com.bakkenbaeck.poddy.repository

import com.bakkenbaeck.poddy.network.model.PodcastResponse
import com.bakkenbaeck.poddy.network.model.SearchResponse
import com.bakkenbaeck.poddy.network.SearchApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

const val PODCAST = "podcast"
const val EPISODE = "episode"

class SearchRepository(
    private val searchApi: SearchApi
) {
    suspend fun search(query: String): Flow<SearchResponse> {
        return flow {
            val result = searchApi.search(query, PODCAST)
            emit(result)
        }
    }

    suspend fun getPodcast(id: String): Flow<PodcastResponse> {
        return flow {
            val result = searchApi.getEpisodes(id, EPISODE)
            emit(result)
        }
    }
}
