package com.deefrent.rnd.fieldapp.models.funeralcashplan.responses


import com.google.gson.annotations.SerializedName

data class CashPlanSubscriptionsPoliciesResponse(
    @SerializedName("data")
    val `data`: List<CashPlanSubscriptionsPoliciesData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

data class CashPlanSubscriptionsPoliciesData(
    @SerializedName("amountPayable")
    val amountPayable: Int,
    @SerializedName("dependants")
    val dependants: List<CashPlanSubscriptionsPoliciesDependants>?,
    @SerializedName("firstPaymentDate")
    val firstPaymentDate: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("isActive")
    val isActive: Int,
    @SerializedName("lastPaymentDate")
    val lastPaymentDate: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("nextPaymentDate")
    val nextPaymentDate: String,
    @SerializedName("numberOfDependants")
    val numberOfDependants: Int,
    @SerializedName("payments")
    val payments: List<CashPlanSubscriptionsPoliciesPayments>?
)

data class CashPlanSubscriptionsPoliciesPayments(

    @SerializedName("date") var date: String? = null,
    @SerializedName("amount") var amount: String? = null,
    @SerializedName("currency") var currency: String? = null,
    @SerializedName("paidBy") var paidBy: String? = null

)


data class CashPlanSubscriptionsPoliciesDependants(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("idNumber") var idNumber: String? = null,
    @SerializedName("dob") var dob: String? = null,
    @SerializedName("relationship") var relationship: String? = null

)