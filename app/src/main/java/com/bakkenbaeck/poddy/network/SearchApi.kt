package com.bakkenbaeck.poddy.network

import com.bakkenbaeck.poddy.network.model.CategoryPodcastReponse
import com.bakkenbaeck.poddy.network.model.GenreResponse
import com.bakkenbaeck.poddy.network.model.PodcastResponse
import com.bakkenbaeck.poddy.network.model.SearchResponse
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
        @Query("sort") sort: String = "recent_first",
        @Query("next_episode_pub_date") nextDate: Long? = null
    ): PodcastResponse

    @Headers("X-ListenAPI-Key: 757284b148904f48b7532ba1b672dd2d")
    @GET("best_podcasts?safe_mode=1&region=no")
    suspend fun getBestPodcasts(): CategoryPodcastReponse

    @Headers("X-ListenAPI-Key: 757284b148904f48b7532ba1b672dd2d")
    @GET("best_podcasts?safe_mode=1&region=no")
    suspend fun getCategoryById(
        @Query("genre_id") id: String
    ): CategoryPodcastReponse

    @Headers("X-ListenAPI-Key: 757284b148904f48b7532ba1b672dd2d")
    @GET("genres?top_level_only=1")
    suspend fun getGenres(): GenreResponse
}