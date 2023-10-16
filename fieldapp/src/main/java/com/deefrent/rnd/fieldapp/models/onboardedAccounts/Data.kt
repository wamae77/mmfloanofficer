package com.deefrent.rnd.fieldapp.models.onboardedAccounts

import androidx.annotation.Keep

@Keep
data class Data(
    val Customers: List<Customer>,
    val Merchants: Merchants
)