package com.deefrent.rnd.fieldapp.models.customerGeomap

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class CustomerGeomapDetails(
    val businessName: String,
    val latitude: String,
    val liquidationRate: Double,
    val longitude: String,
    val phoneNo: String
):Parcelable