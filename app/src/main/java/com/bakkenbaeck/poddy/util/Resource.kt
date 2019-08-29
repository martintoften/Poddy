package com.bakkenbaeck.poddy.util

sealed class Resource<out T>
data class Success<out T>(val data: T) : Resource<T>()
data class Loading<out T>(val partialData: T? = null) : Resource<T>()
data class Failure<out T>(val throwable: Throwable? = null) : Resource<T>()
