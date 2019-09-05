package com.bakkenbaeck.poddy.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Streaming
import retrofit2.http.Url


interface DownloadApi {

    @GET
    @Streaming
    suspend fun download(
        @Url url: String,
        @Header(DOWNLOAD_IDENTIFIER_HEADER) downloadIdentifier: String
    ): Response<ResponseBody>
}
