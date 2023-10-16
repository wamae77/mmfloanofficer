package com.deefrent.rnd.fieldapp.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "identity_type_table")
data class IdentityTypeEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String
){
    override fun toString(): String {
        return name
    }
}