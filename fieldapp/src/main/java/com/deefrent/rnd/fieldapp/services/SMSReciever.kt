package com.deefrent.rnd.fieldapp.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.Task
import java.util.regex.Pattern


/**
 * BroadcastReceiver to wait for SMS messages. This can be registered either
 * in the AndroidManifest or at runtime.  Should filter Intents on
 * SmsRetriever.SMS_RETRIEVED_ACTION.
 */
class SMSReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            //Bundle
            val extras = intent.extras
            val status: Status? = extras!![SmsRetriever.EXTRA_STATUS] as Status?
            if (status != null) {
                when (status.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        // Get SMS message contents
                        val message =
                            extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String?
/*
                        * Extract one-time code from the message and complete verification
                         by sending the code back to your server.*/
                        if (responseFeedback != null) {
                            responseFeedback?.success(
                                extractDigits(
                                    message,
                                    codeLength
                                )
                            )
                        }
                    }
                    CommonStatusCodes.TIMEOUT ->
                        // Waiting for SMS timed out (5 minutes)
                        // Handle the error ...
                        if (responseFeedback != null) {
                            responseFeedback?.error("Timed Out Waiting for verification code")
                        }
                }
            }
        }
    }

    /**
     * ResponseFeedback feedback
     */
    interface ResponseFeedback {
        fun success(message: String?)
        fun error(timeoutMessage: String?)
    }

    companion object {
        private var responseFeedback: ResponseFeedback? = null
        var codeLength = 0
        fun injectListener(
            rf: ResponseFeedback?,
            length: Int
        ) {
            responseFeedback = rf
            codeLength = length
        }

        /**
         * This method extracts the verification code from a message
         * @param in: The message where message ought to be extracted
         * @param codeLength: size of the verification code e.g 0100 is 4
         * @return returns the code
         */
        fun extractDigits(`in`: String?, codeLength: Int): String {
            val p =
                Pattern.compile("(\\d{$codeLength})")
            val m = p.matcher(`in`!!)
            return if (m.find()) {
                m.group(0)!!
            } else ""
        }

        fun startSMSRetriever(ctx: Context?) {
            val client: SmsRetrieverClient = SmsRetriever.getClient(ctx!! /* context */)


// Starts SmsRetriever, which waits for ONE matching SMS message until timeout
// (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
// action SmsRetriever#SMS_RETRIEVED_ACTION.
            val task: Task<Void> = client.startSmsRetriever()
            // Listen for success/failure of the start Task. If in a background thread, this
// can be made blocking using Tasks.await(task, [timeout]);
            task.addOnSuccessListener({ aVoid -> })
            task.addOnFailureListener({ e -> })
        }


    }



}