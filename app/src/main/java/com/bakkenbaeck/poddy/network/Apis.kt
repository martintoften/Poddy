package com.bakkenbaeck.poddy.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_SEARCH_URL = "https://listen-api.listennotes.com/api/v2/"

fun buildSearchApi(): SearchApi {
    val client = getHttpClient()

    val retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(BASE_SEARCH_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    return retrofit.create(SearchApi::class.java)
}

private fun getHttpClient(): OkHttpClient {
    val interceptor = getLoggingInterceptor()

    return OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build()
}

private fun getLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}
