package com.deefrent.rnd.common.network

import okhttp3.ResponseBody

sealed class ResourceNetwork<out T> {
    data class Success<out T>(val value: T) : ResourceNetwork<T>()
    data class Failure(
        val isNetworkError: Boolean,
        val errorCode: Int?,
        val errorBody: ResponseBody?,
        val errorString: String?
    ) : ResourceNetwork<Nothing>()

    object Loading : ResourceNetwork<Nothing>()
}


sealed class ResourceNetworkFlow<T>(
    val data: T? = null,
    val error: Throwable? = null
) {
    class Success<T>(data: T) : ResourceNetworkFlow<T>(data)
    class Loading<T>(data: T? = null) : ResourceNetworkFlow<T>(data)
    class Error<T>(throwable: Throwable, data: T? = null) : ResourceNetworkFlow<T>(data, throwable)
}


sealed class ResourceNetworkStateFlow<out T> {
    data class Success<out T>(val value: T) : ResourceNetworkStateFlow<T>()
    class Failure(val throwable: Throwable) : ResourceNetworkStateFlow<Nothing>()
    object Loading : ResourceNetworkStateFlow<Nothing>()
    object Empty : ResourceNetworkStateFlow<Nothing>()
}

sealed class ApiState<out T> {
    class Failure(val throwable: Throwable) : ApiState<Nothing>()
    class Success<out T>(val data: T) : ApiState<T>()
    object Loading : ApiState<Nothing>()
    object Empty : ApiState<Nothing>()
}