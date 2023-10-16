package com.deefrent.rnd.fieldapp.ui.customerComplains

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class CustomerComplainsSharedViewModel : ViewModel() {
    val accountNumber = MutableLiveData<String>()
    val complainAttachmentPath = MutableLiveData<String>()
    val complainAttachmentFile = MutableLiveData<File>()
    val complainTypeId = MutableLiveData<Int>()
    val userType = MutableLiveData<String>()
    val userAccountTypeId = MutableLiveData<Int>()
    fun setAccountNumber(newAccountNumber: String) {
        accountNumber.value = newAccountNumber
    }

    fun setUserType(newUserType: String) {
        userType.value = newUserType
    }

    fun setComplainTypeId(newComplainTypeId: Int) {
        complainTypeId.value = newComplainTypeId
    }

    fun setUserAccountTypeId(newUserAccountTypeId: Int) {
        userAccountTypeId.value = newUserAccountTypeId
    }

    fun setComplainAttachmentPath(newComplainAttachmentPath: String) {
        complainAttachmentPath.value = newComplainAttachmentPath
    }

    fun setComplainAttachmentFile(newComplainAttachmentFile: File) {
        complainAttachmentFile.value = newComplainAttachmentFile
    }
}