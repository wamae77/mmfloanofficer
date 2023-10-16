package com.deefrent.rnd.fieldapp.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OccupationEntity(
    @PrimaryKey
    val id: Int,
    val name: String
) {
    override fun toString(): String {
        return name
    }
}
