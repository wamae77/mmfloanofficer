package com.deefrent.rnd.fieldapp.models.funeralcashplan

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CustomerPoliciesData(
    val name: String,
    val status: String,
    val numberOfDependant: String,
    val dueOn: String,
) : Parcelable {
    companion object {
        fun getCustomerPoliciesData() = listOf(
            CustomerPoliciesData(
                name = "Silver package",
                status = "Active",
                numberOfDependant = "4 dependants",
                dueOn = "Due on: Today",
            ),
            CustomerPoliciesData(
                name = "COLD package",
                status = "Active",
                numberOfDependant = "4 dependants",
                dueOn = "Due on: Today",
            )
        )
    }
}