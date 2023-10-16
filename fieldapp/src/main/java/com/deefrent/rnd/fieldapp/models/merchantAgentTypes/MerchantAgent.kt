package com.deefrent.rnd.fieldapp.models.merchantAgentTypes

import androidx.annotation.Keep
import com.deefrent.rnd.fieldapp.models.userTypes.UserType

@Keep
data class MerchantAgent(
    val createdBy: Int,
    val createdOn: String,
    val id: Int,
    val name: String,
    val status: String,
    val updatedBy: Int,
    val updatedOn: String,
    val userAccountType: UserType
){
    override fun toString(): String {
        return name
    }
}