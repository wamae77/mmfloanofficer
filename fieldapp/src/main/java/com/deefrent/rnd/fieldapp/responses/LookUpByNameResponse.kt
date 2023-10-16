package com.deefrent.rnd.fieldapp.responses

import androidx.annotation.Keep
import com.deefrent.rnd.fieldapp.models.customer.CustomerInfo

@Keep
data class LookUpByNameResponse(
    val `data`: List<CustomerInfo>,
    val message: String,
    val status: Int
)