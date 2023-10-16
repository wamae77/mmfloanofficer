package com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer

import android.app.Application
import androidx.lifecycle.*
import com.deefrent.rnd.fieldapp.models.xaraniIdCheck.request.XaraniIdCheckRequest
import com.deefrent.rnd.fieldapp.network.repos.IDCustomerLookUpRepository
import javax.inject.Inject

class IDCustomerLookUpViewModel @Inject constructor(
    private val app: Application,
    private val repository: IDCustomerLookUpRepository
) :
    AndroidViewModel(app) {

    fun xaraniIdCheck(xaraniIdCheckRequest: XaraniIdCheckRequest) =
        repository.xaraniIdCheck(xaraniIdCheckRequest = xaraniIdCheckRequest)
}
