package com.bakkenbaeck.poddy.network

import com.bakkenbaeck.poddy.network.model.*
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

const val API_KEY = "757284b148904f48b7532ba1b672dd2d"

interface SearchApi {
    @Headers("X-ListenAPI-Key: $API_KEY")
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("type") type: String = "podcast"
    ): SearchResponse

    @Headers("X-ListenAPI-Key: $API_KEY")
    @GET("podcasts/{id}")
    suspend fun getEpisodes(
        @Path("id") id: String,
        @Query("sort") sort: String = "recent_first",
        @Query("next_episode_pub_date") nextDate: Long? = null
    ): PodcastResponse

    @Headers("X-ListenAPI-Key: $API_KEY")
    @GET("best_podcasts?safe_mode=1&region=no")
    suspend fun getBestPodcasts(): CategoryPodcastReponse

    @Headers("X-ListenAPI-Key: $API_KEY")
    @GET("best_podcasts?safe_mode=1&region=no")
    suspend fun getCategoryById(
        @Query("genre_id") id: String
    ): CategoryPodcastReponse

    @Headers("X-ListenAPI-Key: $API_KEY")
    @GET("genres?top_level_only=1")
    suspend fun getGenres(): GenreResponse

    @Headers("X-ListenAPI-Key: $API_KEY")
    @GET("podcasts/{id}/recommendations?safe_mode=1")
    suspend fun getPodcastRecommendations(
        @Path("id") id: String
    ): PodcastRecommendationResponse
}