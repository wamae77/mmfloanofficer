package com.deefrent.rnd.jiboostfieldapp

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {
    private const val NAME = "io.eclectics.app"
    private const val MODE = Context.MODE_PRIVATE
    lateinit var preferences: SharedPreferences

    /*****set/store shared preferences  */
    fun setPreference(con: Context, key: String?, value: String?) {
        val preferences = con.getSharedPreferences(NAME, 0)
        val editor = preferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    /*** get/retrieve shared preferences   */
    fun getPreferences(con: Context, key: String?): String? {
        val sharedPreferences = con.getSharedPreferences(NAME, 0)
        return sharedPreferences.getString(key, "0")
    }

    // list of app specific preferences
    private val TOKEN = Pair("accessToken", null)
    private val MAX_COLLATERAL = Pair("maxCollateral", null)
    private val MIN_COLLATERAL = Pair("minCollateral", null)
    private val MAX_GUARANTOR = Pair("maxGuarantor", null)
    private val MIN_GUARANTOR = Pair("minGuarantor", null)
    private val ACTIVITY_INJECTORS = Pair("activityInjectors", null)

    fun init(context: Context) {
        preferences = context.getSharedPreferences(
            NAME,
            MODE
        )
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var token: String?
        get() = preferences.getString(TOKEN.first, TOKEN.second)
        set(value) = preferences.edit {
            it.putString(TOKEN.first, value)
        }

    var maxCollateral: String?
        get() = preferences.getString(MAX_COLLATERAL.first, MAX_COLLATERAL.second)
        set(value) = preferences.edit {
            it.putString(MAX_COLLATERAL.first, value)
        }

    var minCollateral: String?
        get() = preferences.getString(MIN_COLLATERAL.first, MIN_COLLATERAL.second)
        set(value) = preferences.edit {
            it.putString(MIN_COLLATERAL.first, value)
        }

    var maxGuarantor: String?
        get() = preferences.getString(MAX_GUARANTOR.first, MAX_GUARANTOR.second)
        set(value) = preferences.edit {
            it.putString(MAX_GUARANTOR.first, value)
        }

    var minGuarantor: String?
        get() = preferences.getString(MIN_GUARANTOR.first, MIN_GUARANTOR.second)
        set(value) = preferences.edit {
            it.putString(MIN_GUARANTOR.first, value)
        }

    var activityInjectors: String?
        get() = preferences.getString(ACTIVITY_INJECTORS.first, ACTIVITY_INJECTORS.second)
        set(value) = preferences.edit {
            it.putString(ACTIVITY_INJECTORS.first, value)
        }
}