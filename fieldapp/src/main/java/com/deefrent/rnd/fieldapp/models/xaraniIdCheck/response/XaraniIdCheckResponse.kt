package com.deefrent.rnd.fieldapp.models.xaraniIdCheck.response

import com.google.gson.annotations.SerializedName

data class XaraniIdCheckResponse(
    @SerializedName("status") var status: Int,
    @SerializedName("message") var message: String,
    @SerializedName("data") var data: XaraniIdCheckData
)

data class XaraniIdCheckData(
    @SerializedName("idNumber") var idNumber: String? = null,
    @SerializedName("genderId") var genderId: String? = null,
    @SerializedName("genderName") var genderName: String? = null,
    @SerializedName("dob") var dob: String? = null,
    @SerializedName("firstName") var firstName: String? = null,
    @SerializedName("lastName") var lastName: String? = null,
    @SerializedName("askUserToDoManualRegistration") var askUserToDoManualRegistration: Boolean
) {
    companion object {
        fun populateDummyCustomerData() = XaraniIdCheckData(
            idNumber = "23345678985AWE",
            genderId = "1",
            genderName = "Male",
            dob = "10-02-1997",
            firstName = "John",
            lastName = "Doe",
            askUserToDoManualRegistration = true
        )
    }
}

