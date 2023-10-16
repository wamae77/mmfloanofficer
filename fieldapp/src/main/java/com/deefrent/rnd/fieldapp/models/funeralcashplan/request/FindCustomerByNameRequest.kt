package com.deefrent.rnd.fieldapp.models.funeralcashplan.request


import com.google.gson.annotations.SerializedName

data class FindCustomerByNameRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("isCashPlan")
    val isCashPlan: Int? = 1,
    @SerializedName("isLoan")
    val isLoan: Int? = 0
)