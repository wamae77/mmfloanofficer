package com.deefrent.rnd.fieldapp.models.mileage

import androidx.annotation.Keep

@Keep
data class Mileage(
    val break_lights_are_ok: Int,
    val final_mileage: String?,
    val from: String,
    val id: Int,
    val is_completed: Int,
    val jack_is_available: Int,
    val oil_is_ok: Int,
    val start_mileage: String,
    val to: String,
    val travel_date: String,
    val triangle_is_available: Int,
    val tyre_pressure_is_ok: Int,
    val user_id: Int,
    val water_is_ok: Int,
    val vehicle_reg_no:String?
)