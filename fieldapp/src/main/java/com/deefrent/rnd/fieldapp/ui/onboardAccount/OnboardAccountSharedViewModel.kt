package com.deefrent.rnd.fieldapp.ui.onboardAccount

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class OnboardAccountSharedViewModel : ViewModel() {
    val userAccountTypeId = MutableLiveData<Int>()
    val phoneNo = MutableLiveData<String>()
    val personalAccountTypeId = MutableLiveData<Int>()
    val KCBBranchId = MutableLiveData<Int>()
    val idNumber = MutableLiveData<String>()
    val idType = MutableLiveData<String>()
    val surname = MutableLiveData<String>()
    val firstName = MutableLiveData<String>()
    val lastName = MutableLiveData<String>()
    val dob = MutableLiveData<String>()
    val gender = MutableLiveData<String>()
    val frontIdCapture = MutableLiveData<String>()
    val frontIdPath = MutableLiveData<String>()
    val backIdCapture = MutableLiveData<String>()
    val backIdPath = MutableLiveData<String>()
    val passportPhoto = MutableLiveData<String>()
    val passportPhotoPath = MutableLiveData<String>()
    val frontIdCaptureFile = MutableLiveData<File>()
    val frontIdCaptureUri = MutableLiveData<Uri>()
    val backIdCaptureFile = MutableLiveData<File>()
    val backIdCaptureUri = MutableLiveData<Uri>()
    val passportPhotoCaptureFile = MutableLiveData<File>()
    val passportPhotoCaptureUri = MutableLiveData<Uri>()
    val income = MutableLiveData<String>()
    val workLocation = MutableLiveData<String>()
    val lastStep = MutableLiveData<String>()
    val employmentType = MutableLiveData<Int>()
    val accountOpeningPurpose = MutableLiveData<String>()
    val roomDBId = MutableLiveData<Int>()
    val userType = MutableLiveData<String>()

    fun setUserType(newUserType: String) {
        userType.value = newUserType
    }

    fun setRoomDBId(newRoomDBId: Int) {
        roomDBId.value = newRoomDBId
    }

    fun setUserAccountTypeId(newUserAccountTypeId: Int) {
        userAccountTypeId.value = newUserAccountTypeId
        Log.d("observeSharedViewModel", "setUserAccountTypeId: $newUserAccountTypeId")
    }

    fun setPhoneNo(newPhoneNo: String) {
        phoneNo.value = newPhoneNo
    }

    fun setLastStep(newLastStep: String) {
        lastStep.value = newLastStep
    }

    fun setPersonalAccountTypeId(newPersonalAccountTypeId: Int) {
        personalAccountTypeId.value = newPersonalAccountTypeId
    }

    fun setKCBBranchId(newKCBBranchId: Int) {
        KCBBranchId.value = newKCBBranchId
    }

    fun setIdNumber(newIdNumber: String) {
        idNumber.value = newIdNumber
    }

    fun setIdType(newIdType: String) {
        idType.value = newIdType
    }

    fun setSurname(newSurname: String) {
        surname.value = newSurname
    }

    fun setFirstName(newFirstName: String) {
        firstName.value = newFirstName
    }

    fun setLastName(newLastName: String) {
        lastName.value = newLastName
    }

    fun setDob(newDob: String) {
        dob.value = newDob
    }

    fun setGender(newGender: String) {
        gender.value = newGender
    }

    fun setFrontIdCapture(newFrontIdCapture: String) {
        frontIdCapture.value = newFrontIdCapture
    }

    fun setFrontIdPath(newFrontIdPath: String) {
        frontIdPath.value = newFrontIdPath
    }

    fun setBackIdCapture(newBackIdCapture: String) {
        backIdCapture.value = newBackIdCapture
    }

    fun setBackIdPath(newBackIdPath: String) {
        backIdPath.value = newBackIdPath
    }

    fun setPassportPhoto(newPassportPhoto: String) {
        passportPhoto.value = newPassportPhoto
    }

    fun setPassportPhotoPath(newPassportPhotoPath: String) {
        passportPhotoPath.value = newPassportPhotoPath
    }

    fun setIncome(newIncome: String) {
        income.value = newIncome
    }

    fun setWorkLocation(newWorkLocation: String) {
        workLocation.value = newWorkLocation
    }

    fun setEmploymentType(newEmploymentType: Int) {
        employmentType.value = newEmploymentType
    }

    fun setAccountOpeningPurpose(newAccountOpeningPurpose: String) {
        accountOpeningPurpose.value = newAccountOpeningPurpose
    }

    fun setFrontIdCaptureFile(newFrontIdCaptureFile: File) {
        frontIdCaptureFile.value = newFrontIdCaptureFile
    }

    fun setFrontIdCaptureUri(newFrontIdCaptureUri: Uri) {
        frontIdCaptureUri.value = newFrontIdCaptureUri
    }

    fun setBackIdCaptureFile(newBackIdCaptureFile: File) {
        backIdCaptureFile.value = newBackIdCaptureFile
    }

    fun setBackIdCaptureUri(newBackIdCaptureUri: Uri) {
        backIdCaptureUri.value = newBackIdCaptureUri
    }

    fun setPassportPhotoCaptureFile(newPassportPhotoCaptureFile: File) {
        passportPhotoCaptureFile.value = newPassportPhotoCaptureFile
    }

    fun setPassportPhotoCaptureUri(newPassportPhotoCaptureUri: Uri) {
        passportPhotoCaptureUri.value = newPassportPhotoCaptureUri
    }
}