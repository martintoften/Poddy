package com.bakkenbaeck.poddy.repository

import com.bakkenbaeck.poddy.model.EpisodeResponse
import com.bakkenbaeck.poddy.model.SearchResponse
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

    suspend fun getEpisodes(id: String): Flow<EpisodeResponse> {
        return flow {
            val result = searchApi.getEpisodes(id, EPISODE)
            emit(result)
        }
    }
}
