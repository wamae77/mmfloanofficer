package com.deefrent.rnd.fieldapp.ui.assetManagement

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class AssetManagementSharedViewModel : ViewModel() {
    val accountNumber = MutableLiveData<String>()
    val assetId = MutableLiveData<Int>()
    val userAccountTypeId = MutableLiveData<Int>()
    val userType = MutableLiveData<String>()
    val assetPhoto1Path=MutableLiveData<String>()
    val assetPhoto1File=MutableLiveData<File>()
    fun setAccountNumber(newAccountNumber: String) {
        accountNumber.value = newAccountNumber
    }

    fun setUserType(newUserType: String) {
        userType.value = newUserType
    }

    fun setAssetId(newAssetId: Int) {
        assetId.value = newAssetId
    }

    fun setUserAccountTypeId(newUserAccountTypeId:Int){
        userAccountTypeId.value=newUserAccountTypeId
    }

    fun setAssetPhoto1Path(newAssetPhoto1Path: String) {
        assetPhoto1Path.value = newAssetPhoto1Path
    }

    fun setAssetPhoto1File(newAssetPhoto1File: File) {
        assetPhoto1File.value = newAssetPhoto1File
    }
}