package com.deefrent.rnd.fieldapp.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rship_type_table")
data class RshipTypeEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String
){
    override fun toString(): String {
        return name
    }
}