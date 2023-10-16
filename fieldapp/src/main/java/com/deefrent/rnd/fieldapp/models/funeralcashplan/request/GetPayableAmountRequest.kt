package com.deefrent.rnd.fieldapp.models.funeralcashplan.request

import com.google.gson.annotations.SerializedName
import request.Dependant


data class GetPayableAmountRequest(
    @SerializedName("packageId") var packageId: Int? = null,
    @SerializedName("dependants") var dependants: ArrayList<Dependant> = arrayListOf(),
    @SerializedName("customerIdNumber") val customerIdNumber: String
    //  @SerializedName("paymentAmount") var paymentAmount: Int? = null,
    // @SerializedName("walletAccountId") var walletAccountId: Int? = null
)