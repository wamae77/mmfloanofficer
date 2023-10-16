package com.deefrent.rnd.fieldapp.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VillageEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val districtId: Int,
    val name: String
){
    override fun toString(): String {
        return name
    }
}