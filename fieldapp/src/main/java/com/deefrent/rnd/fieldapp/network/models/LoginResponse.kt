package com.deefrent.rnd.fieldapp.network.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class LoginResponse(
    @SerializedName("data")
    val `data`: LoginData,
    @SerializedName("message")
    val message: String, // Login Success
    @SerializedName("status")
    val status: Int // 1
)

data class LoginData(
    @SerializedName("changePassword")
    val changePassword: Boolean,
    @SerializedName("is_first_login")
    val isFirstLogin: Boolean,
    @SerializedName("last_login")
    val lastLogin: String,
    //
    @SerializedName("fingerprintAuthLink")
    val fingerprintAuthLink: String,
    @SerializedName("fingerprintConsumerKey")
    val fingerprintConsumerKey: String,
    @SerializedName("fingerprintConsumeSecret")
    val fingerprintConsumeSecret: String,
    @SerializedName("fingerprintServiceName")
    val fingerprintServiceName: String,
//
    @SerializedName("portalUrl")
    val portalUrl: String,

    @SerializedName("securityQuestionsSet")
    val securityQuestionsSet: Int,

    @SerializedName("token")
    val token: String,

    @SerializedName("tellerBalance")
    val tellerBalance: TellerBalance,

    @SerializedName("pendingAssessmentCount")
    val pendingAssessmentCount: String,

    @SerializedName("pendingCompletionCount")
    val pendingCompletionCount: String,

    @SerializedName("user")
    val user: User,

    @SerializedName("minGuarantors")
    val minGuarantors: String,

    @SerializedName("maxGuarantors")
    val maxGuarantors: String,

    @SerializedName("minCollaterals")
    val minCollaterals: String,

    @SerializedName("maxCollaterals")
    val maxCollaterals: String,
)

data class User(
    @SerializedName("email")
    val email: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("username")
    val username: String
)

data class TellerBalance(
    @SerializedName("balance")
    val balance: String,
    @SerializedName("loanRepayment")
    val loanRepayment: String,
    @SerializedName("totalDisbursement")
    val totalDisbursement: String
)