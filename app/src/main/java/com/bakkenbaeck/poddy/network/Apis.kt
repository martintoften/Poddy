package com.bakkenbaeck.poddy.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_RSS_URL = "https://itunes.apple.com/"
private const val BASE_SEARCH_URL = "https://itunes.apple.com/"

fun buildRssApi(): RssApi {
    val client = getHttpClient()

    val xmlFactory = SimpleXmlConverterFactory.createNonStrict(Persister(AnnotationStrategy()))

    val retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(BASE_RSS_URL)
        .addConverterFactory(xmlFactory)
        .build()

    return retrofit.create(RssApi::class.java)
}

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
