package com.deefrent.rnd.fieldapp.network.repos

import com.deefrent.rnd.common.repo.BaseRepository
import com.deefrent.rnd.fieldapp.models.xaraniIdCheck.request.XaraniIdCheckRequest
import com.deefrent.rnd.fieldapp.network.FieldApiService
import javax.inject.Inject

class IDCustomerLookUpRepository @Inject constructor(private val apiService: FieldApiService) :
    BaseRepository {

    fun xaraniIdCheck(
        xaraniIdCheckRequest: XaraniIdCheckRequest
    ) = apiRequestByResourceFlow {
        apiService.xaraniIdCheck(xaraniIdCheckRequest = xaraniIdCheckRequest)
    }


}