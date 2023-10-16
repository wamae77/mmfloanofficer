package com.deefrent.rnd.fieldapp.models.onboardedAccounts

import androidx.annotation.Keep

@Keep
data class Agent(
    val accountNo: String,
    val businessName: String,
    val firstName: String,
    val latitude: String,
    val longitude: String,
    val merchantType: String,
    val natureOfBusiness: String,
    val registeredDate: String
)