package com.bakkenbaeck.poddy.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.CoroutineContext

sealed class Result<out T> {
    data class Success<out T>(val value: T): Result<T>()
    data class Error(val code: Int? = null, val throwable: Throwable): Result<Nothing>()
}

suspend fun <T> safeApiCall(context: CoroutineContext = Dispatchers.IO, apiCall: suspend () -> T): Result<T> {
    return withContext(context) {
        try {
            Result.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            handleApiError(throwable)
        }
    }
}

fun handleApiError(throwable: Throwable): Result.Error {
    return when (throwable) {
        is IOException -> Result.Error(throwable = throwable)
        is HttpException -> {
            val code = throwable.code()
            Result.Error(code, throwable)
        }
        else -> Result.Error(null, throwable = throwable)
    }
}
