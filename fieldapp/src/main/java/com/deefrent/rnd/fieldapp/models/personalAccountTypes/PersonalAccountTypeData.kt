package com.deefrent.rnd.fieldapp.models.personalAccountTypes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PersonalAccountTypeData(
    @SerializedName("personalaccounttype")
    @Expose
    val personalAccountType: List<PersonalAccountType>
)