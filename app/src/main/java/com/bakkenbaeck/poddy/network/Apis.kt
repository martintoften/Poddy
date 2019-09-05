package com.bakkenbaeck.poddy.network

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_SEARCH_URL = "https://listen-api.listennotes.com/api/v2/"
private const val BASE_DOWNLOAD_URL = "https://listen-api.listennotes.com/api/v2/"

fun buildSearchApi(): SearchApi {
    val client = getHttpClient()

    val retrofit = Retrofit.Builder()
        .client(client.build())
        .baseUrl(BASE_SEARCH_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    return retrofit.create(SearchApi::class.java)
}

fun buildDownloadApi(interceptor: DownloadProgressInterceptor): DownloadApi {
    val client = getHttpClient()
        .addInterceptor(interceptor)

    val retrofit = Retrofit.Builder()
        .client(client.build())
        .baseUrl(BASE_DOWNLOAD_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    return retrofit.create(DownloadApi::class.java)
}

private fun getHttpClient(): OkHttpClient.Builder {
    val interceptor = getLoggingInterceptor()

    return OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
}

private fun getLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}
