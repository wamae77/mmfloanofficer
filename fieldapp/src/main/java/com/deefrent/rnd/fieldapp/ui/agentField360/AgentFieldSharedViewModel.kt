package com.deefrent.rnd.fieldapp.ui.agentField360

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AgentFieldSharedViewModel : ViewModel() {
    val accountNumber = MutableLiveData<String>()
    val userType = MutableLiveData<String>()
    fun setAccountNumber(newAccountNumber: String) {
        accountNumber.value = newAccountNumber
    }

    fun setUserType(newUserType: String) {
        userType.value = newUserType
    }
}