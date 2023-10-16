package com.deefrent.rnd.fieldapp.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Tom Munyiri on 26/07/2022
 * Email: munyiri.thomas@electics.io
 * Company: Eclectics International Ltd
 */
@Entity(tableName = "officer_subbranch_table")
data class SubBranchEntity(
    @PrimaryKey
    val id: Int,
    val name: String
) {
    override fun toString(): String {
        return name
    }
}
