package com.deefrent.rnd.fieldapp.repositories

import com.deefrent.rnd.fieldapp.dtos.AccountLookUpDTO
import com.deefrent.rnd.fieldapp.dtos.LoginDTO
import com.deefrent.rnd.fieldapp.dtos.ResetPassDTO
import com.deefrent.rnd.fieldapp.dtos.VerifyOtpDTO
import com.deefrent.rnd.fieldapp.network.FieldAgentApi

class LookupRepo {
    fun accountLookup(accountLookUpDTO: AccountLookUpDTO) =
        FieldAgentApi.retrofitService.accountLookUpAsync(accountLookUpDTO)

    fun verifyOtp(verifyOtpDTO: VerifyOtpDTO) =
        FieldAgentApi.retrofitService.verifyOTPAsync(verifyOtpDTO)

    fun resendOtp(resetPassDTO: ResetPassDTO) =
        FieldAgentApi.retrofitService.resendOtpAsync(resetPassDTO)

    fun loginUser(loginDTO: LoginDTO) = FieldAgentApi.retrofitService.loginAsync(loginDTO)
    fun logoutUser() = FieldAgentApi.retrofitService.logoutAsync()
}