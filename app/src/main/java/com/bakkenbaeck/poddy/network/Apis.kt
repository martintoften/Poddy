package com.bakkenbaeck.poddy.network

import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_SEARCH_URL = "https://listen-api.listennotes.com/api/v2/"
private const val BASE_DOWNLOAD_URL = "https://listen-api.listennotes.com/api/v2/"

fun buildSearchApi(): SearchApi {
    val client = getHttpClient(listOf(getLoggingInterceptor()))

    val retrofit = Retrofit.Builder()
        .client(client.build())
        .baseUrl(BASE_SEARCH_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    return retrofit.create(SearchApi::class.java)
}

fun buildDownloadApi(progressInterceptor: DownloadProgressInterceptor): DownloadApi {
    val client = getHttpClient(listOf(progressInterceptor))

    val retrofit = Retrofit.Builder()
        .client(client.build())
        .baseUrl(BASE_DOWNLOAD_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    return retrofit.create(DownloadApi::class.java)
}

private fun getHttpClient(interceptors: List<Interceptor> = emptyList()): OkHttpClient.Builder {
    val builder = OkHttpClient.Builder()
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)

    interceptors.forEach { builder.addInterceptor(it) }

    return builder
}

private fun getLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}
