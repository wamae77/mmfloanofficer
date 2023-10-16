package com.deefrent.rnd.fieldapp.dtos

data class UpdateDiaryDTO(
    val description: String,
    val event_date: String,
    val event_type_id: Int,
    val id: Int,
    val is_active: Int,
    val venue: String
)