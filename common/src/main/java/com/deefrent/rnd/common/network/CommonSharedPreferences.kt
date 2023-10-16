package com.deefrent.rnd.common.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import javax.inject.Inject


private const val FIELDAPP_PREFERENCE_DATA = "my_preference"
private const val FIELDAPP_PASTROLIST_DATA = "FIELDAPP_PASTROLIST_PREFERENCE"

class CommonSharedPreferences @Inject constructor(context: Context) {

    companion object {
        private const val ISPRINTRECEIPT: String = "SETISPRINTRECIEPT"
        private const val IS_FINGERPRINT_DONE: String = "IS_FINGERPRINT_DONE"
        const val CUSTOMER_INFO: String = "CUSTOMER_INFO"
        const val SELECTED_PACKAGE: String = "SELECTED_PACKAGE"
        const val CUSTOMER_SUBSCRIPTION_PACKAGE: String = "CUSTOMER_SUBSCRIPTION_PACKAGE"
        const val LOANLOOKUPDATA: String = "LOANLOOKUPDATA"
        const val CURRENY_USER_DATA: String = "CURRENY_USER_DATA"
        const val CU_ID_NUMBER = "CU_ID_NUMBER"
        const val CU_FINGER_PRINT_ID = "CU_FINGER_PRINT_ID"
    }

    val applicationContext: Context = context.applicationContext

    private var prefs: SharedPreferences =
        context.getSharedPreferences(
            FIELDAPP_PREFERENCE_DATA,
            Context.MODE_PRIVATE
        )

    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(
            FIELDAPP_PASTROLIST_DATA,
            Context.MODE_PRIVATE
        )

    //    private object PreferenceKey {
//        val key = preferencesKey<String>("authToken")
//        val guestDataTime = preferencesKey<String>("guestDataTime")
//    }


    /**
     * Function to save timestamp  when data is loaded
     * Function to fetch timestamp when data was loaded*/

    fun setIsPrintReceipt(phone: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(ISPRINTRECEIPT, phone)
        editor.apply()
    }

    fun getIsPrintReceipt(): Boolean {
        return prefs.getBoolean(ISPRINTRECEIPT, false)
    }

    fun setIsFingerPrintDone(value: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(IS_FINGERPRINT_DONE, value)
        editor.apply()
    }

    fun getIsFingerPrintDone(): Boolean {
        return prefs.getBoolean(IS_FINGERPRINT_DONE, false)
    }

    /**
     * Function to save timestamp  when data is loaded
     * Function to fetch timestamp when data was loaded*/

    fun saveStringData(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getStringData(key: String): String {
        Log.e(
            "",
            "GET_STRING_DATA${
                sharedPreferences.getString(
                    key,
                    ""
                ).toString()
            }"
        )
        return sharedPreferences.getString(key, "").toString()
    }

    fun clearPastrolistData() {
        sharedPreferences.edit().clear().commit()
    }


}
