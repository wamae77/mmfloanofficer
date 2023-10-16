package com.deefrent.rnd.fieldapp.utils

import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.fieldapp.R
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

/**
 * Created by Tom Munyiri on 07/10/2022
 * Email: munyiri.thomas@electics.io
 * Company: Eclectics International Ltd
 */

fun resolveException(e: Exception): String {
    val message = BaseApp.applicationContext().getString(R.string.unable_to_complete_your_request)
    return when (e) {
        is SocketTimeoutException -> {
            "Connection Timed Out"
        }
        is ConnectException, is SocketException -> {
            "Check your internet connection and try again"
        }
        is UnknownHostException, is SSLHandshakeException -> {
            BaseApp.applicationContext().getString(R.string.no_network_connection)
        }
        is HttpException -> {
            when (e.code()) {
                in 500..504 -> BaseApp.applicationContext()
                    .getString(R.string.unable_to_complete_your_request)
                401 -> "Session Expired!"
                400 -> BaseApp.applicationContext()
                    .getString(R.string.unable_to_complete_your_request)
                else -> BaseApp.applicationContext()
                    .getString(R.string.unable_to_complete_your_request)
            }
        }
        else -> message
    }

}