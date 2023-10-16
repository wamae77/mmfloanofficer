package com.deefrent.rnd.fieldapp.models.onboardedAccounts

import androidx.annotation.Keep

@Keep
data class Merchants(
    val Agent: List<Agent>,
    val Merchant: List<Merchant>
)