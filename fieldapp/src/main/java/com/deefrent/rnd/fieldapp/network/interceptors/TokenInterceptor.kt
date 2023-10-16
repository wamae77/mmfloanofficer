package com.deefrent.rnd.fieldapp.network.interceptors

import com.deefrent.rnd.jiboostfieldapp.AppPreferences
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class TokenInterceptor :Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        //val sessionManager = SessionManager(context)
        //rewrite the request to add bearer token
        val newRequest: Request = chain.request().newBuilder()
            .header("Authorization", "Bearer " + AppPreferences.token)
            .build()

        return chain.proceed(newRequest)
    }
}