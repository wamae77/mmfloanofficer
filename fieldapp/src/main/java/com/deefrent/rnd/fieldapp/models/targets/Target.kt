package com.deefrent.rnd.fieldapp.models.targets

import androidx.annotation.Keep

@Keep
data class Target(
    val achieved: String,
    val achievementPercentage: String,
    val code: String,
    val currency: String,
    val from: String,
    val target: String,
    val to: String,
    val type: String
)