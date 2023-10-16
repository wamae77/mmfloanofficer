package com.deefrent.rnd.fieldapp.models.funeralcashplan.request


import com.google.gson.annotations.SerializedName

data class CashPlanPackagesRequest(
    @SerializedName("customerIdNumber")
    val customerIdNumber: String
)