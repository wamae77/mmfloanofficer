package com.deefrent.rnd.common.dialogs.base.adapter_detail.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


// model the details in the dialog recyclerview
@Parcelize
data class DialogDetailCommon (
    val label : String,
    val content : String,
) : Parcelable

/*
data class DialogDetailCommon (
    val label : String,
    val content : String,
)
*/
