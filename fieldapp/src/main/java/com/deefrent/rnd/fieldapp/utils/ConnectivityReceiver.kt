package com.deefrent.rnd.fieldapp.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

/**
 * Created by Tom Munyiri on 21/06/2022
 * Email: munyiri.thomas@electics.io
 * Company: Eclectics International Ltd
 */
class ConnectivityReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        connectivityReceiverListener?.onNetworkConnectionChanged(isConnectedOrConnecting(context))
    }

    private fun isConnectedOrConnecting(context: Context?): Boolean {
        val connMgr = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }
}