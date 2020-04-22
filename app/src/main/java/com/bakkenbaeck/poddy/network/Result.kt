package com.bakkenbaeck.poddy.network

import retrofit2.HttpException
import java.io.IOException

sealed class Result<out T> {
    data class Success<out T>(val value: T): Result<T>()
    data class Error(val code: Int? = null, val throwable: Throwable): Result<Nothing>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
    return try {
        Result.Success(apiCall.invoke())
    } catch (throwable: Throwable) {
        when (throwable) {
            is IOException -> Result.Error(throwable = throwable)
            is HttpException -> {
                val code = throwable.code()
                Result.Error(code, throwable)
            }
            else -> {
                Result.Error(null, throwable = throwable)
            }
        }
    }
}
