package com.deefrent.rnd.fieldapp.network.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
data class LoanPayableResponse(
    @SerializedName("data")
    val `data`: List<LoanPayableData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

@Parcelize
@Keep
data class LoanPayableData(
    @SerializedName("amountApplied")
    val amountApplied: String,
    @SerializedName("amountApproved")
    val amountApproved: String,
    @SerializedName("applicationDate")
    val applicationDate: String,
    @SerializedName("balance")
    val balance: String,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("loanAccountNo")
    val loanAccountNo: String,
    @SerializedName("loanId")
    val loanId: Int,
    @SerializedName("name")
    val name: String
) : Parcelable
