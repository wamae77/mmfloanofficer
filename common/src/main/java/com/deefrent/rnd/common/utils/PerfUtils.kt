package com.deefrent.rnd.common.utils

import android.content.Context

object PrefUtils {
    private const val PREFS_NAME = "Sacco Hub"
    const val PREF_JTOKEN = "token"
    fun formatPhoneNumber(countryCode: String, unformattedPhone: String): String {
        var formatPhone = ""

        if (unformattedPhone.startsWith("0")) {
            formatPhone = countryCode + unformattedPhone.substring(1)
        } else if (unformattedPhone.length < 10) {
            formatPhone = countryCode + unformattedPhone
        }
        return formatPhone
    }
    /**load image */

    /*****set/store shared preferences  */
    fun setPreference(con: Context, key: String?, value: String?) {
        val preferences = con.getSharedPreferences(PREFS_NAME, 0)
        val editor = preferences.edit()
        editor.putString(key, value)
        editor.apply()
    }


    /*** get/retrieve shared preferences   */
    fun getPreferences(con: Context, key: String?): String? {
        val sharedPreferences = con.getSharedPreferences(PREFS_NAME, 0)
        return sharedPreferences.getString(key, "0")
    }

    fun clear(con: Context) {
        val sharedPrefs = con.getSharedPreferences(PREFS_NAME, 0)
        val editor = sharedPrefs.edit()
        editor.clear()
        editor.apply()
    }

}