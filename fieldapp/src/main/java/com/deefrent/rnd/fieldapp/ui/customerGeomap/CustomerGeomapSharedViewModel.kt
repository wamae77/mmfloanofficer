package com.deefrent.rnd.fieldapp.ui.customerGeomap

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CustomerGeomapSharedViewModel : ViewModel() {
    val accountNumber = MutableLiveData<String>()
    val userType = MutableLiveData<String>()
    fun setAccountNumber(newAccountNumber: String) {
        accountNumber.value = newAccountNumber
        Log.d("Customer Geomap", "setAccountType: $newAccountNumber")
    }
    fun setUserType(newUserType: String) {
        userType.value = newUserType
        Log.d("Customer Geomap", "setUserType: $newUserType")
    }
}