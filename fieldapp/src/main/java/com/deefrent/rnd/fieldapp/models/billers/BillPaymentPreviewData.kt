package com.deefrent.rnd.fieldapp.models.billers

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class BillPaymentPreviewData(
    val amount: String,
    val charges: String,
    val conversionCurrency: String,
    val convertedAmount: String,
    val exerciseDuty: String,
    val paymentCurrency: String,
    val rate: String,
    @SerializedName("recipientName")
    val recipientName: String
)