package com.deefrent.rnd.fieldapp.ui.onboardAccount

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class OnboardMerchantSharedViewModel : ViewModel() {
    val roomDBId = MutableLiveData<Int>()
    val dob = MutableLiveData<String>()
    val merchantIDNumber = MutableLiveData<String>()
    val merchantSurname = MutableLiveData<String>()
    val merchantFirstName = MutableLiveData<String>()
    val merchantLastName = MutableLiveData<String>()
    val merchantGender = MutableLiveData<String>()
    val userType = MutableLiveData<String>()
    val userAccountTypeId = MutableLiveData<Int>()
    val merchAgentAccountTypeId = MutableLiveData<Int>()
    val businessName = MutableLiveData<String>()
    val mobileNumber = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val businessTypeId = MutableLiveData<Int>()
    val businessNature = MutableLiveData<String>()
    val liquidationTypeId = MutableLiveData<Int>()
    val liquidationRate = MutableLiveData<Int>()
    val bankCode = MutableLiveData<String>()
    val branchCode = MutableLiveData<String>()
    val accountName = MutableLiveData<String>()
    val accountNumber = MutableLiveData<String>()
    val countyCode = MutableLiveData<String>()
    val townName = MutableLiveData<String>()
    val roomNo = MutableLiveData<String>()
    val buldingName = MutableLiveData<String>()
    val streetName = MutableLiveData<String>()
    val termsAndConditionDoc = MutableLiveData<String>()
    val termsAndConditionDocPath = MutableLiveData<String>()
    val termsAndConditionDocFile = MutableLiveData<File>()
    val termsAndConditionDocUri = MutableLiveData<Uri>()
    val customerPhotoPath = MutableLiveData<String>()
    val customerPhotoFile = MutableLiveData<File>()
    val customerPhotoUri = MutableLiveData<Uri>()
    val signatureDocPath = MutableLiveData<String>()
    val signatureDocFile = MutableLiveData<File>()
    val signatureDocUri = MutableLiveData<Uri>()
    val businessPermitDoc = MutableLiveData<String>()
    val businessPermitDocPath = MutableLiveData<String>()
    val businessPermitDocFile = MutableLiveData<File>()
    val businessPermitDocUri = MutableLiveData<Uri>()
    val companyRegistrationDoc = MutableLiveData<String>()
    val companyRegistrationPath = MutableLiveData<String>()
    val companyRegistrationDocFile = MutableLiveData<File>()
    val companyRegistrationDocUri = MutableLiveData<Uri>()
    val frontIdCapture = MutableLiveData<String>()
    val frontIdPath = MutableLiveData<String>()
    val backIdCapture = MutableLiveData<String>()
    val backIdPath = MutableLiveData<String>()
    val frontIdCaptureFile = MutableLiveData<File>()
    val frontIdCaptureUri = MutableLiveData<Uri>()
    val backIdCaptureFile = MutableLiveData<File>()
    val backIdCaptureUri = MutableLiveData<Uri>()
    val kraPINFile = MutableLiveData<File>()
    val kraPINUri = MutableLiveData<Uri>()
    val kraPINPath = MutableLiveData<String>()
    val businessLicenseFile = MutableLiveData<File>()
    val businessLicenseUri = MutableLiveData<Uri>()
    val businessLicensePath = MutableLiveData<String>()
    val goodConductFile = MutableLiveData<File>()
    val goodConductUri = MutableLiveData<Uri>()
    val goodConductPath = MutableLiveData<String>()
    val fieldApplicationFormFile = MutableLiveData<File>()
    val fieldApplicationFormUri = MutableLiveData<Uri>()
    val fieldApplicationFormPath = MutableLiveData<String>()
    val shopPhotoFile = MutableLiveData<File>()
    val shopPhotoPath = MutableLiveData<String>()
    val lastStep = MutableLiveData<String>()
    val idType = MutableLiveData<String>()

    fun setLastStep(newLastStep: String) {
        lastStep.value = newLastStep
    }

    fun setIdType(newIdType: String) {
        idType.value = newIdType
    }

    fun setRoomDBId(newRoomDBId: Int) {
        roomDBId.value = newRoomDBId
    }

    fun setUserType(newUserType: String) {
        userType.value = newUserType
    }

    fun setUserAccountTypeId(newUserAccountTypeId: Int) {
        userAccountTypeId.value = newUserAccountTypeId
        Log.d("observeSharedViewModel", "setUserAccountTypeId: $newUserAccountTypeId")
    }

    fun setMerchAgentAccountTypeId(newMerchAgentAccountTypeId: Int) {
        merchAgentAccountTypeId.value = newMerchAgentAccountTypeId
    }

    fun setBusinessName(newBusinessName: String) {
        businessName.value = newBusinessName
    }

    fun setMobileNumber(newMobileNumber: String) {
        mobileNumber.value = newMobileNumber
    }

    fun setEmail(newEmail: String) {
        email.value = newEmail
    }

    fun setBusinessTypeId(newBusinessTypeId: Int) {
        businessTypeId.value = newBusinessTypeId
    }

    fun setBusinessNature(newBusinessNature: String) {
        businessNature.value = newBusinessNature
    }

    fun setLiquidationTypeId(newLiquidationTypeId: Int) {
        liquidationTypeId.value = newLiquidationTypeId
    }

    fun setLiquidationRate(newLiquidationRate: Int) {
        liquidationRate.value = newLiquidationRate
    }

    fun setBankCode(newBankCode: String) {
        bankCode.value = newBankCode
    }

    fun setBranchCode(newBranchCode: String) {
        branchCode.value = newBranchCode
    }

    fun setAccountName(newAccountName: String) {
        accountName.value = newAccountName
    }

    fun setAccountNumber(newAccountNumber: String) {
        accountNumber.value = newAccountNumber
    }

    fun setCountyCode(newCountyCode: String) {
        countyCode.value = newCountyCode
    }

    fun setTownName(newTownName: String) {
        townName.value = newTownName
    }

    fun setStreetName(newStreetName: String) {
        streetName.value = newStreetName
    }

    fun setBuildingName(newBuildingName: String) {
        buldingName.value = newBuildingName
    }

    fun setRoomNumber(newRoomNumber: String) {
        roomNo.value = newRoomNumber
    }

    fun setTermsAndConditionDoc(newTermsAndConditionDoc: String) {
        termsAndConditionDoc.value = newTermsAndConditionDoc
    }

    fun setTermsAndConditionDocFile(newTermsAndConditionDocFile: File) {
        termsAndConditionDocFile.value = newTermsAndConditionDocFile
    }

    fun setTermsAndConditionDocUri(newTermsAndConditionDocUri: Uri) {
        termsAndConditionDocUri.value = newTermsAndConditionDocUri
    }

    fun setCustomerPhotoPath(newCustomerPhotoPath: String) {
        customerPhotoPath.value = newCustomerPhotoPath
    }

    fun setCustomerPhotoFile(newCustomerPhotoFile: File) {
        customerPhotoFile.value = newCustomerPhotoFile
    }

    fun setCustomerPhotoUri(newCustomerPhotoUri: Uri) {
        customerPhotoUri.value = newCustomerPhotoUri
    }

    fun setSignatureDocPath(newSignatureDocPath: String) {
        signatureDocPath.value = newSignatureDocPath
    }

    fun setSignatureDocFile(newSignatureDocFile: File) {
        signatureDocFile.value = newSignatureDocFile
    }

    fun setSignatureDocUri(newSignatureDocUri: Uri) {
        signatureDocUri.value = newSignatureDocUri
    }

    fun setBusinessPermitDoc(newBusinessPermitDoc: String) {
        businessPermitDoc.value = newBusinessPermitDoc
    }

    fun setBusinessPermitDocFile(newBusinessPermitDocFile: File) {
        businessPermitDocFile.value = newBusinessPermitDocFile
    }

    fun setBusinessPermitDocUri(newBusinessPermitDocUri: Uri) {
        businessPermitDocUri.value = newBusinessPermitDocUri
    }

    fun setCompanyRegistrationDoc(newCompanyRegistrationDoc: String) {
        companyRegistrationDoc.value = newCompanyRegistrationDoc
    }

    fun setCompanyRegistrationDocFile(newCompanyRegistrationDocFile: File) {
        companyRegistrationDocFile.value = newCompanyRegistrationDocFile
    }

    fun setCompanyRegistrationDocUri(newCompanyRegistrationDocUri: Uri) {
        companyRegistrationDocUri.value = newCompanyRegistrationDocUri
    }

    fun setDob(newDob: String) {
        dob.value = newDob
    }

    fun setMerchantIDNumber(newMerchantIDNumber: String) {
        merchantIDNumber.value = newMerchantIDNumber
    }

    fun setMerchantSurname(newMerchantSurname: String) {
        merchantSurname.value = newMerchantSurname
    }

    fun setMerchantFirstName(newMerchantFirstName: String) {
        merchantFirstName.value = newMerchantFirstName
    }

    fun setMerchantLastName(newMerchantLastName: String) {
        merchantLastName.value = newMerchantLastName
    }

    fun setMerchantGender(newMerchantGender: String) {
        merchantGender.value = newMerchantGender
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

    fun setKRAPinFile(newKRAPinFile: File) {
        kraPINFile.value = newKRAPinFile
    }

    fun setKRAPinUri(newKRAPinUri: Uri) {
        kraPINUri.value = newKRAPinUri
    }

    fun setGoodConductFile(newGoodConductFile: File) {
        goodConductFile.value = newGoodConductFile
    }

    fun setGoodConductUri(newGoodConductUri: Uri) {
        goodConductUri.value = newGoodConductUri
    }

    fun setGoodConductPath(newGoodConductPath: String) {
        goodConductPath.value = newGoodConductPath
    }

    fun setFieldApplicationFormFile(newFieldApplicationFormFile: File) {
        fieldApplicationFormFile.value = newFieldApplicationFormFile
    }

    fun setFieldApplicationFormUri(newFieldApplicationFormUri: Uri) {
        fieldApplicationFormUri.value = newFieldApplicationFormUri
    }

    fun setFieldApplicationFormPath(newFieldApplicationFormPath: String) {
        fieldApplicationFormPath.value = newFieldApplicationFormPath
    }

    fun setBusinessLicenseFile(newBusinessLicenseFile: File) {
        businessLicenseFile.value = newBusinessLicenseFile
    }

    fun setBusinessLicenseUri(newBusinessLicenseUri: Uri) {
        businessLicenseUri.value = newBusinessLicenseUri
    }

    fun setTermsAndConditionsDocPath(newTermsAndConditionsDocPath: String) {
        termsAndConditionDocPath.value = newTermsAndConditionsDocPath
    }

    fun setKRAPinPath(newKRAPinPath: String) {
        kraPINPath.value = newKRAPinPath
    }

    fun setBusinessLicensePath(newBusinessLicensePath: String) {
        businessLicensePath.value = newBusinessLicensePath
    }

    fun setBusinessPermitPath(newBusinessPermitPath: String) {
        businessPermitDocPath.value = newBusinessPermitPath
    }

    fun setCompanyRegistrationDocPath(newCompanyRegistrationDocPath: String) {
        companyRegistrationPath.value = newCompanyRegistrationDocPath
    }

    fun setShopPhotoPath(newShopPhotoPath: String) {
        shopPhotoPath.value = newShopPhotoPath
    }

    fun setShopPhotoFile(newShopPhotoFile: File) {
        shopPhotoFile.value = newShopPhotoFile
    }
}