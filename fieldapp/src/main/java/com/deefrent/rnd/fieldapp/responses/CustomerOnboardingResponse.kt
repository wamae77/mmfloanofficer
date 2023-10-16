package com.deefrent.rnd.fieldapp.responses

import androidx.annotation.Keep
import com.deefrent.rnd.fieldapp.models.customer.CustomerOnboardingData

@Keep
data class CustomerOnboardingResponse(
    val `data`: CustomerOnboardingData,
    val message: String,
    val status: Int
)