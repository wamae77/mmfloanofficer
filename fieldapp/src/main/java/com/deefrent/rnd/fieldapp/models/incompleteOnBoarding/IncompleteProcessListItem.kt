package com.deefrent.rnd.fieldapp.models.incompleteOnBoarding

import androidx.annotation.Keep

@Keep
data class IncompleteProcessListItem(
    val userType:String,
    val date:String,
    val roomDBId:Int
)
