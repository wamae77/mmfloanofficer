package com.deefrent.rnd.fieldapp.network.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
data class BillersResponse(
    @SerializedName("data")
    val `data`: List<Biller>,
    @SerializedName("message")
    val message: String, // Success
    @SerializedName("status")
    val status: Int // 1
)

@Parcelize
data class Biller(
    @SerializedName("billerCode")
    val billerCode: String, // CUT
    @SerializedName("name")
    val name: String, // Chinhoyi University of Technology
    @SerializedName("shortName")
    val shortName: String, // CUT
    @SerializedName("transactionType")
    val transactionType: String, // 004
    val logoUrl: String,
    val categoryId: String,
    val categoryName: String
) : Parcelable
