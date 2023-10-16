package com.deefrent.rnd.fieldapp.room.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deefrent.rnd.fieldapp.network.models.Gender

@Dao
interface GenderDao {
    @Query("SELECT * from gender_entity_table")
    fun getAllGender():LiveData<List<Gender>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertGender(genderItems: List<Gender>)
    @Query("DELETE from gender_entity_table")
    fun deleteGender():Int
    @Query("SELECT * from gender_entity_table")
    fun retrieveGender():List<Gender>

}