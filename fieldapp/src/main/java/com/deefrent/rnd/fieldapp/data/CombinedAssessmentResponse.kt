package com.deefrent.rnd.fieldapp.data


import com.deefrent.rnd.fieldapp.network.models.HoouseHoldMembers
import com.google.gson.annotations.SerializedName

data class CombinedAssessmentResponse(
    @SerializedName("data")
    val `data`: AssessmentData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)
    data class AssessmentData(
        @SerializedName("expenses")
        val expenses: Expenses,
        @SerializedName("householdMembers")
        val householdMembers: List<HoouseHoldMembers>,
        @SerializedName("incomes")
        val incomes: Incomes
    )
        data class Expenses(
            @SerializedName("domesticWorkersWages")
            val domesticWorkersWages: Any,
            @SerializedName("food")
            val food: String,
            @SerializedName("funeralPolicy")
            val funeralPolicy: Any,
            @SerializedName("medicalAidOrContributions")
            val medicalAidOrContributions: String,
            @SerializedName("other")
            val other: String,
            @SerializedName("rentals")
            val rentals: String,
            @SerializedName("schoolFees")
            val schoolFees: String,
            @SerializedName("tithe")
            val tithe: Any,
            @SerializedName("transport")
            val transport: String
        )

        data class HoouseHoldMembers(
            @SerializedName("fullName")
            val fullName: String,
            @SerializedName("incomeOrFeesPaid")
            val incomeOrFeesPaid: String,
            @SerializedName("natureOfActivity")
            val natureOfActivity: String,
            @SerializedName("occupation")
            val occupation: String,
            @SerializedName("occupationId")
            val occupationId: Int,
            @SerializedName("relationShip")
            val relationShip: String,
            @SerializedName("relationshipId")
            val relationshipId: Int
        )

        data class Incomes(
            @SerializedName("other")
            val other: String,
            @SerializedName("otherBusinesses")
            val otherBusinesses: String,
            @SerializedName("ownSalary")
            val ownSalary: String,
            @SerializedName("remittanceOrDonation")
            val remittanceOrDonation: String,
            @SerializedName("rental")
            val rental: String,
            @SerializedName("roscals")
            val roscals: String
        )
