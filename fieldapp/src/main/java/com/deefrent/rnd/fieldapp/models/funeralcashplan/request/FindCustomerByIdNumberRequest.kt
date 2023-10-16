package com.deefrent.rnd.fieldapp.models.funeralcashplan.request


import com.google.gson.annotations.SerializedName

data class FindCustomerByIdNumberRequest(
    @SerializedName("idNumber")
    val idNumber: String,
    @SerializedName("isCashPlan")
    val isCashPlan: Int?=1,
    @SerializedName("isLoan")
    val isLoan: Int? = 0
)