package com.deefrent.rnd.fieldapp.models.customer

import androidx.annotation.Keep

@Keep
data class Expenses(
    val domesticWorkersWages: String,
    val food: String,
    val funeralPolicy: String,
    val medicalAidOrContributions: String,
    val other: String,
    val rentals: String,
    val schoolFees: String,
    val tithe: String,
    val transport: String
)