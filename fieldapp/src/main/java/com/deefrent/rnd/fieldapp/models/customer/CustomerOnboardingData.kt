package com.deefrent.rnd.fieldapp.models.customer

import androidx.annotation.Keep

@Keep
data class CustomerOnboardingData(
    val customerId: Int,
    val idNumber: String
)