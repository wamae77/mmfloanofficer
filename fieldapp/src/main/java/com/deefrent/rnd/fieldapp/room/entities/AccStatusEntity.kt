package com.deefrent.rnd.fieldapp.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "accommodation_status_table")
data class AccStatusEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String
){
    override fun toString(): String {
        return name
    }
}