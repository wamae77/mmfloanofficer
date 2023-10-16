package com.deefrent.rnd.fieldapp.responses

import androidx.annotation.Keep
import com.deefrent.rnd.fieldapp.models.targets.Target

@Keep
data class GetSalesTargetResponse(
    val `data`: List<Target>,
    val message: String,
    val status: Int
)