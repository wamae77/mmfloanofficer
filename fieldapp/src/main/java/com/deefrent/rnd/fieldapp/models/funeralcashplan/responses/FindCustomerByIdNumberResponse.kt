package com.deefrent.rnd.fieldapp.models.funeralcashplan.responses


import com.google.gson.annotations.SerializedName

data class FindCustomerByIdNumberResponse(
    @SerializedName("data")
    val `data`: FindCustomerData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

data class FindCustomerData(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("firstName") var firstName: String? = null,
    @SerializedName("customerNumber") var customerNumber: String? = null,
    @SerializedName("idNumber") var idNumber: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("dob") var dob: String? = null,
    @SerializedName("lastName") var lastName: String? = null,
    @SerializedName("creditRating") var creditRating: String? = null,
    @SerializedName("isAssessed") var isAssessed: Boolean? = null,
    @SerializedName("isFullyRegistered") var isFullyRegistered: Boolean? = null,
    @SerializedName("policies") var policies: String? = null
)
