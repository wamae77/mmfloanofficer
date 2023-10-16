package com.deefrent.rnd.fieldapp.models.funeralcashplan.request


import com.google.gson.annotations.SerializedName

data class CashPlanSubscriptionPayRequest(
    @SerializedName("amount")
    val amount: Int?,
    @SerializedName("subscriptionId")
    val subscriptionId: Int?,
    @SerializedName("walletAccountId")
    val walletAccountId: Int?,
    @SerializedName("customerIdNumber")
    val customerIdNumber: String
)