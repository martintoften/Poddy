package com.bakkenbaeck.poddy.network

import com.bakkenbaeck.poddy.model.Search
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    @GET("search")
    suspend fun search(
        @Query("term") query: String,
        @Query("entity") entity: String = "podcast"
    ): Search
}
