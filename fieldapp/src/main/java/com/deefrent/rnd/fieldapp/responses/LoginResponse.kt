package com.deefrent.rnd.fieldapp.responses

import androidx.annotation.Keep

@Keep
data class LoginResponse(
    val access_token: String,
    val expires_in: Int,
    val jti: String,
    val scope: String,
    val token_type: String
)