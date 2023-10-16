package com.deefrent.rnd.fieldapp.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

/**
 * BroadcastReceiver to wait for SMS messages. This can be registered either
 * in the AndroidManifest or at runtime.  Should filter Intents on
 * SmsRetriever.SMS_RETRIEVED_ACTION.
 */
class SmsBroadcastReceiver : BroadcastReceiver() {
    lateinit var smsBroadcastReceiverListener: SmsBroadcastReceiverListener
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == SmsRetriever.SMS_RETRIEVED_ACTION) {
            val extras = intent.extras
            val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status
            when (smsRetrieverStatus.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT).also {
                        smsBroadcastReceiverListener.onSuccess(it)
                    }
                }
                CommonStatusCodes.TIMEOUT -> {
                    smsBroadcastReceiverListener.onFailure()
                }
            }
        }
    }
    interface SmsBroadcastReceiverListener {
        fun onSuccess(intent: Intent?)
        fun onFailure()
    }
}