package com.deefrent.rnd.fieldapp.responses

import androidx.annotation.Keep
import com.deefrent.rnd.fieldapp.models.customer.CustomerInfo

@Keep
data class LookUpByIDResponse(
    val `data`: CustomerInfo,
    val message: String,
    val status: Int
)
