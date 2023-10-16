package com.deefrent.rnd.fieldapp.network.repos

import com.deefrent.rnd.common.repo.BaseRepository
import com.deefrent.rnd.fieldapp.dtos.VerifyUserDTO
import com.deefrent.rnd.fieldapp.models.funeralcashplan.request.*
import com.deefrent.rnd.fieldapp.network.FieldApiService
import request.FuneralCashPlanSubscribeRequest
import javax.inject.Inject

class FuneralCashPlanRepository @Inject constructor(private val apiService: FieldApiService) :
    BaseRepository {

    fun getCashPlanPackages(
        cashPlanPackagesRequest: CashPlanPackagesRequest
    ) = apiRequestByResourceFlow {
        apiService.getCashPlanPackages(cashPlanPackagesRequest = cashPlanPackagesRequest)
    }


    fun findCustomerByName(
        findCustomerByNameRequest: FindCustomerByNameRequest
    ) = apiRequestByResourceFlow {
        apiService.findCustomerByName(findCustomerByNameRequest = findCustomerByNameRequest)
    }

    fun findCustomerByIdNumber(
        findCustomerByIdNumberRequest: FindCustomerByIdNumberRequest
    ) = apiRequestByResourceFlow {
        apiService.findCustomerByIdNumber(findCustomerByIdNumberRequest = findCustomerByIdNumberRequest)
    }

    fun getPayableAmount(
        getPayableAmountRequest: GetPayableAmountRequest
    ) = apiRequestByResourceFlow {
        apiService.getPayableAmount(getPayableAmountRequest = getPayableAmountRequest)
    }

    fun getSavingAcc(
        savingAccDTO: SavingAccDTO
    ) = apiRequestByResourceFlow {
        apiService.getSavingAcc(savingAccDTO = savingAccDTO)
    }

    fun cashPlanSubscribe(
        funeralCashPlanSubscribeRequest: FuneralCashPlanSubscribeRequest
    ) = apiRequestByResourceFlow {
        apiService.cashPlanSubscribe(funeralCashPlanSubscribeRequest = funeralCashPlanSubscribeRequest)
    }

    fun cashPlanSubscriptions(
        cashPlanPackagesRequest: CashPlanPackagesRequest
    ) = apiRequestByResourceFlow {
        apiService.cashPlanSubscriptions(cashPlanPackagesRequest = cashPlanPackagesRequest)
    }

    fun cashPlanSubscriptionPay(
        cashPlanSubscriptionPayRequest: CashPlanSubscriptionPayRequest
    ) = apiRequestByResourceFlow {
        apiService.cashPlanSubscriptionPay(cashPlanSubscriptionPayRequest = cashPlanSubscriptionPayRequest)
    }

    fun verifyUser(
        verifyUserDTO: VerifyUserDTO
    ) = apiRequestByResourceFlow {
        apiService.verifyUserIsMe(verifyUserDTO = verifyUserDTO)
    }
}