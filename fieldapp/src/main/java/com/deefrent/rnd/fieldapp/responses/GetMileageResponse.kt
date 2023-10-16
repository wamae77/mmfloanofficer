package com.deefrent.rnd.fieldapp.responses

import androidx.annotation.Keep
import com.deefrent.rnd.fieldapp.models.mileage.Mileage

@Keep
data class GetMileageResponse(
    val `data`: List<Mileage>,
    val message: String,
    val status: Int
)