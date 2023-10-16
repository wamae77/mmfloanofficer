package com.deefrent.rnd.fieldapp.dtos


import com.google.gson.annotations.SerializedName

data class AddHouseHoldMember(
    @SerializedName("id_number")
    val idNumber: String,
    @SerializedName("members")
    val members: List<Member>
) {
    data class Member(
        @SerializedName("relationship_id")
        val relationshipId: String,
        @SerializedName("full_name")
        val fullName: String,
        @SerializedName("current_occupation_id")
        val currentOccupationId: String,
        @SerializedName("nature_of_activity")
        val natureOfActivity: String,
        @SerializedName("income_or_fees_paid")
        val incomeOrFeesPaid: String,


    )
}
