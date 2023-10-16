package com.deefrent.rnd.fieldapp.models.funeralcashplan.responses


import com.google.gson.annotations.SerializedName

data class GetPayableAmountResponse(
    @SerializedName("data")
    val payableAmount: PayableAmount,
    val message: String,
    val status: Int
)

data class PayableAmount(
    val amount: String
)