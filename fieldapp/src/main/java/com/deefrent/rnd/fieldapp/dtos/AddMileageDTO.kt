package com.deefrent.rnd.fieldapp.dtos

data class AddMileageDTO(
    val break_lights_are_ok: Int,
    val from: String,
    val jack_is_available: Int,
    val oil_is_ok: Int,
    val start_mileage: String,
    val to: String,
    val travel_date: String,
    val triangle_is_available: Int,
    val tyre_pressure_is_ok: Int,
    val water_is_ok: Int,
    val vehicle_reg_no: String
)