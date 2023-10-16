package com.deefrent.rnd.fieldapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deefrent.rnd.fieldapp.models.onboardedAccounts.GetOnboardedAccountsResponse
import com.deefrent.rnd.fieldapp.repositories.OnboardedAccountsRepo

class OnboardedAccountsViewModel : ViewModel() {
    val onboardedAccountsResponse = MutableLiveData<GetOnboardedAccountsResponse>()
    val startDate = MutableLiveData<String>()
    val endDate = MutableLiveData<String>()
    private var onboardedAccountsRepo: OnboardedAccountsRepo = OnboardedAccountsRepo()
    fun getOnboardedAccounts(
        startdate: String,
        enddate: String,
        size: String,
        page: String
    ): LiveData<GetOnboardedAccountsResponse?> {
        return onboardedAccountsRepo.getOnboardedAccounts(startdate, enddate, size, page)
    }

    fun setOnboardedAccountsResponse(onboardedAccountsResponse1: GetOnboardedAccountsResponse) {
        onboardedAccountsResponse.value = onboardedAccountsResponse1
    }

    fun setDateValues(startdate: String, enddate: String) {
        startDate.value = startdate
        endDate.value = enddate
    }
}