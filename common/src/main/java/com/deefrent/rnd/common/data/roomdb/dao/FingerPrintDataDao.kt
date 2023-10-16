package com.deefrent.rnd.common.data.roomdb.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deefrent.rnd.common.data.fingerprint.FingerPrintData
import kotlinx.coroutines.flow.Flow

@Dao
interface FingerPrintDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(list: FingerPrintData)

    @Query("SELECT * FROM fingerprintdata")
    fun getAllData(): Flow<List<FingerPrintData>>

    @Query("SELECT * FROM fingerprintdata WHERE phoneNumber = :phoneNumber")
    fun getByPhoneNumber(phoneNumber: String): Flow<List<FingerPrintData>>

    @Query("DELETE FROM FingerPrintData")
    fun deleteAllData()

    @Delete
    fun delete(fingerPrintData: FingerPrintData)

    @Query("DELETE FROM fingerprintdata WHERE phoneNumber = :phoneNumber")
    fun deleteByPhoneNumber(phoneNumber: String)

}