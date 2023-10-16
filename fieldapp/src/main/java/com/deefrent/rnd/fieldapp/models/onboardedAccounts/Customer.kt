package com.deefrent.rnd.fieldapp.models.onboardedAccounts

import androidx.annotation.Keep

@Keep
data class Customer(
    val accountNo: String,
    val createdDate: String,
    val firstName: String,
    val lastName: String,
    val latitude: String,
    val longitude: String
)