package com.deefrent.rnd.common.data.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.deefrent.rnd.common.data.fingerprint.FingerPrintData
import com.deefrent.rnd.common.data.roomdb.dao.FingerPrintDataDao

@Database(
    entities = [
        FingerPrintData::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class MoneyMartAppDatabase : RoomDatabase() {

    abstract fun getFingerPrintDataDao(): FingerPrintDataDao

    companion object {
        @Volatile
        private var INSTANCE: MoneyMartAppDatabase? = null

        operator fun invoke(context: Context) = INSTANCE ?: synchronized(Any()) {
            INSTANCE ?: buildDatabase(context).also {
                INSTANCE = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            MoneyMartAppDatabase::class.java,
            "MoneyMartAppDatabase.db"
        ).build()
    }
}