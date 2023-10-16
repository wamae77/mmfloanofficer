package com.deefrent.rnd.fieldapp.models.funeralcashplan.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Tom Munyiri on 19/07/2022
 * Email: munyiri.thomas@electics.io
 * Company: Eclectics International Ltd
 */

data class SubscribeResponse(
    @SerializedName("data")
    val `data`: SubscribeResponseData? = null,
    @SerializedName("message")
    val message: String? = null, // Success
    @SerializedName("status")
    val status: Int? = null // 1
)

data class SubscribeResponseData(
    @SerializedName("transactionCode")
    val transactionCode: String? = null, // WMAB1IU6
    val amount: String? = null,
    val currency: String? = null,
    val dependants: String? = null,
    val nextPaymentDate: String? = null,
    val walletBalance: String? = null,
)