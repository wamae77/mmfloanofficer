package com.deefrent.rnd.fieldapp.responses

import androidx.annotation.Keep
import com.deefrent.rnd.fieldapp.models.customer.WalletAccount

@Keep
data class GetWalletAccountsResponse(
    val `data`: List<WalletAccount>,
    val message: String,
    val status: Int
)