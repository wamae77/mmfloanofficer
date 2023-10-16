package com.deefrent.rnd.common.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.deefrent.rnd.common.utils.Constants
import com.deefrent.rnd.common.R
import okhttp3.*
import timber.log.Timber
import java.io.IOException

/**
 * This class help to hand no internet errors and decide what happenes
 * when there is no internet connection
 */

class NetworkInterceptor(context: Context) : Interceptor {
    val appContext = context.applicationContext
    override fun intercept(chain: Interceptor.Chain): Response {
        if (isOnline()) {
            val url = chain.request()
                .url
                .newBuilder()
                // .addQueryParameter("apiKey", API_KEY)
                .build()

            /**
             * Add access token if it exists
             * else dont add it when doing request that dont need it
             */
            val requestBuilder = chain.request().newBuilder().url(url)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("version-name", "1.0.0")
                .addHeader("Device-ID", Constants.DEVICE_ID)
                .addHeader("app-id", "1")

            Timber.d(
                "URL IS $url \n " +
                        "REQUEST_HEADER IS ${requestBuilder.build().headers} \n " +
                        "REQUEST_METHOD IS ${requestBuilder.build().method} \n\n"
            )

            return if (Constants.token == "") {
                chain.proceed(requestBuilder.build())
            } else {
                requestBuilder.apply {
                    addHeader("Authorization", " Bearer ${Constants.token}")
                }
                chain.proceed(requestBuilder.build())
            }
        }
        throw NetworkExceptions(appContext.getString(R.string.check_your_internet_connection))
    }

    /**
     * Returns true if the network is available else
     * false if network is not available
     */
    private fun isOnline(): Boolean {
        var result = false
        val connectivityManager =
            appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
        }
        return result
    }
}

class NetworkExceptions(message: String) : IOException(message)
