package com.deefrent.rnd.fieldapp.view.homepage.funeralcashplan

import androidx.lifecycle.ViewModel
import com.deefrent.rnd.common.network.ResourceNetworkFlow
import com.deefrent.rnd.fieldapp.dtos.VerifyUserDTO
import com.deefrent.rnd.fieldapp.models.funeralcashplan.request.*
import com.deefrent.rnd.fieldapp.models.funeralcashplan.responses.*
import com.deefrent.rnd.fieldapp.network.repos.FuneralCashPlanRepository
import kotlinx.coroutines.flow.Flow
import request.FuneralCashPlanSubscribeRequest
import javax.inject.Inject


class FuneralCashPlanViewModel @Inject constructor(private val repository: FuneralCashPlanRepository) :
    ViewModel() {
    /**
     * requestGetCashPlanPackages
     */
    fun requestGetCashPlanPackages(cashPlanPackagesRequest: CashPlanPackagesRequest): Flow<ResourceNetworkFlow<CashPlanPackagesResponse>> {
        return repository.getCashPlanPackages(cashPlanPackagesRequest = cashPlanPackagesRequest)
    }

    /**
     * findCustomerByName
     */
    fun findCustomerByName(findCustomerByNameRequest: FindCustomerByNameRequest): Flow<ResourceNetworkFlow<FindCustomerByNameResponse>> {
        return repository.findCustomerByName(findCustomerByNameRequest = findCustomerByNameRequest)
    }


    /**
     * Response
     */
    fun findCustomerByIdNumber(findCustomerByIdNumberRequest: FindCustomerByIdNumberRequest): Flow<ResourceNetworkFlow<FindCustomerByIdNumberResponse>> {
        return repository.findCustomerByIdNumber(findCustomerByIdNumberRequest = findCustomerByIdNumberRequest)
    }


    fun getPayableAmount(getPayableAmountRequest: GetPayableAmountRequest): Flow<ResourceNetworkFlow<GetPayableAmountResponse>> {
        return repository.getPayableAmount(getPayableAmountRequest = getPayableAmountRequest)
    }

    /**
     * Response
     */
    fun getSavingAcc(savingAccDTO: SavingAccDTO): Flow<ResourceNetworkFlow<SavingAccountsResponse>> {
        return repository.getSavingAcc(savingAccDTO = savingAccDTO)
    }

    /**
     * Response
     */
    fun cashPlanSubscribe(funeralCashPlanSubscribeRequest: FuneralCashPlanSubscribeRequest): Flow<ResourceNetworkFlow<SubscribeResponse>> {
        return repository.cashPlanSubscribe(funeralCashPlanSubscribeRequest = funeralCashPlanSubscribeRequest)
    }

    /**
     * Response
     */
    fun cashPlanSubscriptions(cashPlanPackagesRequest: CashPlanPackagesRequest): Flow<ResourceNetworkFlow<CashPlanSubscriptionsPoliciesResponse>> {
        return repository.cashPlanSubscriptions(cashPlanPackagesRequest = cashPlanPackagesRequest)
    }

    /**
     * Response
     */
    fun cashPlanSubscriptionPay(cashPlanSubscriptionPayRequest: CashPlanSubscriptionPayRequest): Flow<ResourceNetworkFlow<CommonResponse>> {
        return repository.cashPlanSubscriptionPay(cashPlanSubscriptionPayRequest = cashPlanSubscriptionPayRequest)
    }
    /**
     * Response
     */
    fun verifyUser(verifyUserDTO: VerifyUserDTO): Flow<ResourceNetworkFlow<CommonResponse>> {
        return repository.verifyUser(verifyUserDTO = verifyUserDTO)
    }


}

