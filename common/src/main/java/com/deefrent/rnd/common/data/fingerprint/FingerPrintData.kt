package com.deefrent.rnd.common.data.fingerprint

import androidx.room.Entity
import androidx.room.PrimaryKey


/*import io.realm.RealmObject
import io.realm.annotations.PrimaryKey*/




@Entity
data class FingerPrintData(
    var fingerImage: String?,
    var phoneNumber: String?,
    var handType: String?,
    @PrimaryKey(autoGenerate = false)
    var fingerPosition: String,
    var description: String
)



