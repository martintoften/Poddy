package com.bakkenbaeck.poddy.network

import com.bakkenbaeck.poddy.model.EpisodeResponse
import com.bakkenbaeck.poddy.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface SearchApi {
    @Headers("X-ListenAPI-Key: 757284b148904f48b7532ba1b672dd2d")
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("type") type: String = "podcast"
    ): SearchResponse

    @Headers("X-ListenAPI-Key: 757284b148904f48b7532ba1b672dd2d")
    @GET("podcasts/{id}")
    suspend fun getEpisodes(
        @Path("id") id: String,
        @Query("sort") sort: String = "recent_first"
    ): EpisodeResponse
}
