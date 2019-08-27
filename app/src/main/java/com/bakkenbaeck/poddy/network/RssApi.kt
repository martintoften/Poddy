package com.bakkenbaeck.poddy.network

import com.bakkenbaeck.poddy.model.Rss
import retrofit2.http.GET
import retrofit2.http.Url

interface RssApi {
    @GET
    suspend fun getFeed(@Url url: String): Rss
}
