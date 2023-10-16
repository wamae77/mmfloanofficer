package com.deefrent.rnd.fieldapp.ui.existingAccount

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ExistingAccountSharedViewModel : ViewModel() {
    val accountName = MutableLiveData<String>()
    val customerID = MutableLiveData<String>()
    val accountNumber = MutableLiveData<String>()
    var userType = MutableLiveData<String>()
    fun setAccountName(newAccountName: String) {
        accountName.value = newAccountName
    }

    fun setAccountNumber(newAccountNumber: String) {
        accountNumber.value = newAccountNumber
    }

    fun setCustomerID(newCustomerID: String) {
        customerID.value = newCustomerID
    }

    fun setUserType(newUserType: String) {
        userType.value = newUserType
    }
}