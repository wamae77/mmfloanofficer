package com.deefrent.rnd.common.repo

import android.util.Log
import com.deefrent.rnd.common.network.ResourceNetworkFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.io.IOException

/**
 * Wrapping network routing using
 * This help to avoid handling failure on the UI
 * Also handle how responses will be handle after success or failure
 */
interface BaseRepository {
    /**
     * Wrapping network routing using
     */
    fun <T> apiRequestByResourceFlow(api: suspend () -> T) = flow {
        try {
            emit(ResourceNetworkFlow.Loading())
            val dataResponse = api.invoke()
            Log.e("ResourceNetworkFlow", "${dataResponse}")
            Timber.d("ResourceNetworkFlow ${dataResponse}")
            emit(ResourceNetworkFlow.Success(dataResponse))
        } catch (exception: Exception) {
            Timber.d("ResourceNetworkFlow ${exception}")
            emit(ResourceNetworkFlow.Error(exception))
        }
    }.flowOn(Dispatchers.IO)

}


class ApiExceptions(message: String) : IOException(message)
