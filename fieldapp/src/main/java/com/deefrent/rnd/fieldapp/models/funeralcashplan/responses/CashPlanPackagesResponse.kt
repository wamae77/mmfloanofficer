package com.deefrent.rnd.fieldapp.models.funeralcashplan.responses


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class CashPlanPackagesResponse(
    @SerializedName("data")
    val `data`: List<FuneralCashPlanPackagesData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)

@Parcelize
data class FuneralCashPlanPackagesData(
    @SerializedName("adultDependantContribution")
    val adultDependantContribution: String,
    @SerializedName("allowCashback")
    val allowCashback: Int,
    @SerializedName("cashbackAmount")
    val cashbackAmount: String?,
    @SerializedName("contributionPeriod")
    val contributionPeriod: Int,
    @SerializedName("contributionPeriodMeasure")
    val contributionPeriodMeasure: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("logoUrl")
    val logoUrl: String,
    @SerializedName("maxDependants")
    val maxDependants: Int,
    @SerializedName("minorContributionAmount")
    val minorContributionAmount: String,
    @SerializedName("name")
    val name: String
) : Parcelable
