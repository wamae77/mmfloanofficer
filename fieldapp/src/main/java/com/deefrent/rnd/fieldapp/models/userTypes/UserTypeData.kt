package com.deefrent.rnd.fieldapp.models.userTypes

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class UserTypeData(
    @SerializedName("useraccount")
    @Expose
    val userType: List<UserType>
)