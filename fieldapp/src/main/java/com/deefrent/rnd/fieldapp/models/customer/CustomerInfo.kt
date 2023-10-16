package com.deefrent.rnd.fieldapp.models.customer

import androidx.annotation.Keep

@Keep
data class CustomerInfo(
    val id: String?,
    val assessmentPercentage: String,
    val assessmentRemarks: String,
    val customerNumber: String,
    val expenses: Expenses?,
    val firstName: String,
    val householdMembers: List<HouseholdMember>,
    val idNumber: String,
    val incomes: Incomes?,
    val isAssessed: Boolean,
    val isFullyRegistered: Boolean,
    val lastName: String,
    val phone: String
)