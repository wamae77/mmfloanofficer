package com.deefrent.rnd.fieldapp.models.onboardedAccounts

import androidx.annotation.Keep

@Keep
data class GetOnboardedAccountsResponse(
    val `data`: Data,
    val message: String,
    val status: String
)