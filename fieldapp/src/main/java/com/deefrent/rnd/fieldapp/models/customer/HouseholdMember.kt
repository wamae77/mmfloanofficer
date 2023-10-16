package com.deefrent.rnd.fieldapp.models.customer

import androidx.annotation.Keep

@Keep
data class HouseholdMember(
    val fullName: String,
    val incomeOrFeesPaid: String,
    val memberId: Int,
    val natureOfActivity: String,
    val occupation: String,
    val occupationId: Int,
    val relationShip: String,
    val relationshipId: Int
)