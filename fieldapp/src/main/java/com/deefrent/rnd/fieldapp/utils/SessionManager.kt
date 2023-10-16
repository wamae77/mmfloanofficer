package com.deefrent.rnd.fieldapp.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var sharedPreferences: SharedPreferences? = null

    init {
        sharedPreferences = context.getSharedPreferences("accessToken", Context.MODE_PRIVATE)
    }

    fun getAccessToken(): String? {
        return sharedPreferences?.getString("token", "nothing")
    }
}