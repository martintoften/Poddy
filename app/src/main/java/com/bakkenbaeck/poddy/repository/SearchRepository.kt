package com.bakkenbaeck.poddy.repository

import com.bakkenbaeck.poddy.model.Search
import com.bakkenbaeck.poddy.network.SearchApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepository(
    private val searchApi: SearchApi
) {
    suspend fun search(query: String): Flow<Search> {
        return flow {
            val result = searchApi.search(query)
            emit(result)
        }
    }
}
