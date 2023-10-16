package com.deefrent.rnd.fieldapp.view.homepage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deefrent.rnd.fieldapp.responses.GetUserDetailsResponse
import javax.inject.Inject

class LoginSessionSharedViewModel @Inject constructor() : ViewModel() {
    var isFromLoginScreen = MutableLiveData<Boolean>()
    val isFromIncompleteDialog = MutableLiveData<Boolean>()
    val getUserDetailsResponse = MutableLiveData<GetUserDetailsResponse>()
    val newPin = MutableLiveData<String>()
    fun setIsFromLoginScreen(newIsFromLoginScreen: Boolean) {
        isFromLoginScreen.value = newIsFromLoginScreen
    }

    fun setPin(pin:String){
        newPin.value=pin
    }

    fun setIsFromIncompleteDialog(newIsFromIncompleteDialog: Boolean) {
        isFromIncompleteDialog.value = newIsFromIncompleteDialog
    }

    fun setGetUserDetailsResponse(newGetUserDetailsResponse: GetUserDetailsResponse) {
        getUserDetailsResponse.value = newGetUserDetailsResponse
    }
}