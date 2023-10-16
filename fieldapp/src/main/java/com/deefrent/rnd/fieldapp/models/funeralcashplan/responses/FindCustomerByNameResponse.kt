package com.deefrent.rnd.fieldapp.models.funeralcashplan.responses


import com.google.gson.annotations.SerializedName

data class FindCustomerByNameResponse(
    @SerializedName("data")
    val `data`: List<FindCustomerData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

