package com.deefrent.rnd.fieldapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deefrent.rnd.fieldapp.bodies.existingAccounts.SearchExistingAccountBody
import com.deefrent.rnd.fieldapp.models.customerDetails.CustomerDetailsResponse
import com.deefrent.rnd.fieldapp.models.customerGeomap.CustomerGeomapResponse
import com.deefrent.rnd.fieldapp.models.merchantAgentDetails.MerchantAgentDetailsResponse
import com.deefrent.rnd.fieldapp.repositories.ExistingAccountRepo

class ExistingAccountViewModel : ViewModel() {
    private var existingAccountRepo: ExistingAccountRepo = ExistingAccountRepo()
    val merchantAgentDetailsResponse = MutableLiveData<MerchantAgentDetailsResponse>()
    val customerDetailsResponse = MutableLiveData<CustomerDetailsResponse>()
    val accountNumber = MutableLiveData<String>()
    var userType = MutableLiveData<String>()
    fun getCustomerDetails(searchExistingAccountBody: SearchExistingAccountBody): LiveData<CustomerDetailsResponse> {
        return existingAccountRepo.getCustomerDetails(searchExistingAccountBody)
    }

    fun getMerchantAgentDetails(accountNo: String): LiveData<MerchantAgentDetailsResponse> {
        return existingAccountRepo.getMerchantAgentDetails(accountNo)
    }

    fun setExistingMerchantAgentDetailsResponse(merchantAgentResponse: MerchantAgentDetailsResponse) {
        merchantAgentDetailsResponse.value = merchantAgentResponse
    }

    fun setExistingCustomerDetailsResponse(customerDetailsResponse1: CustomerDetailsResponse) {
        customerDetailsResponse.value = customerDetailsResponse1
    }

    fun setAccountNumber(newAccountNumber: String) {
        accountNumber.value = newAccountNumber
    }

    fun setUserType(newUserType: String) {
        userType.value = newUserType
    }

    fun getCustomerGeomapDetails(accountNo: String): LiveData<CustomerGeomapResponse> {
        return existingAccountRepo.getCustomerGeomapDetails(accountNo)
    }
}