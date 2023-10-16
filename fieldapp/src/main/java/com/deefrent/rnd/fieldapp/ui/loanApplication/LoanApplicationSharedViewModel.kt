package com.deefrent.rnd.fieldapp.ui.loanApplication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoanApplicationSharedViewModel : ViewModel() {
    val calculateLoanTabSelected = MutableLiveData<Boolean>()
    val accountName = MutableLiveData<String>()
    val customerID = MutableLiveData<String>()
    val accountNumber = MutableLiveData<String>()
    val userType = MutableLiveData<String>()
    fun selectCalculateLoanTab(selected: Boolean) {
        calculateLoanTabSelected.value = selected
    }
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