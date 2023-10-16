package com.deefrent.rnd.fieldapp.view.homepage.customerassessment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.dtos.*
import com.deefrent.rnd.fieldapp.models.customer.CustomerInfo
import com.deefrent.rnd.fieldapp.models.loans.RepaymentSchedule
import com.deefrent.rnd.fieldapp.network.FieldAgentApi
import com.deefrent.rnd.fieldapp.network.models.*
import com.deefrent.rnd.fieldapp.network.models.HouseholdMember
import com.deefrent.rnd.fieldapp.responses.LookUpByNameResponse
import com.deefrent.rnd.fieldapp.room.database.FieldAppDatabase
import com.deefrent.rnd.fieldapp.room.repos.DropdownItemRepository
import com.deefrent.rnd.fieldapp.utils.Constants
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import java.io.IOException

class CustomerAssessmentHomeViewModel : ViewModel() {
    private var _status = MutableLiveData<Int?>()
    val status: MutableLiveData<Int?>
        get() = _status
    private var _statusId = MutableLiveData<Int?>()
    val statusId: MutableLiveData<Int?>
        get() = _statusId
    private var _statusE = MutableLiveData<Int?>()
    val statusE: MutableLiveData<Int?>
        get() = _statusE
    private var _statusIncome = MutableLiveData<Int?>()
    val statusIncome: MutableLiveData<Int?>
        get() = _statusIncome
    private var _statusI = MutableLiveData<Int?>()
    val statusI: MutableLiveData<Int?>
        get() = _statusI
    private var _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String>
        get() = _statusMessage
    private var _statusCode = MutableLiveData<Int?>()
    val statusCode: MutableLiveData<Int?>
        get() = _statusCode
    private var _statusDelCode = MutableLiveData<Int?>()
    val statusDelCode: MutableLiveData<Int?>
        get() = _statusDelCode
    private var _statusUpdate = MutableLiveData<Int?>()
    val statusUpdate: MutableLiveData<Int?>
        get() = _statusUpdate
    private var _responseStatus = MutableLiveData<GeneralResponseStatus>()
    val responseStatus: LiveData<GeneralResponseStatus>
        get() = _responseStatus
    private var _responseGStatus = MutableLiveData<GeneralResponseStatus>()
    val responseGStatus: LiveData<GeneralResponseStatus>
        get() = _responseGStatus
    private var _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean>
        get() = _isEmpty
    var _idNumber = MutableLiveData<String>()
    val idNumber: LiveData<String>
        get() = _idNumber
    private var _responseDelStatus = MutableLiveData<GeneralResponseStatus>()
    val responseDelStatus: LiveData<GeneralResponseStatus>
        get() = _responseDelStatus
    var _responseExpensesStatus = MutableLiveData<GeneralResponseStatus>()

    private var _refreshData = MutableLiveData<Boolean>()
    val refreshData: LiveData<Boolean>
        get() = _refreshData

    fun setRefreshData(refresh: Boolean) {
        _refreshData.value = refresh
    }

    /**guarantors details*/
    var _expenseLoadingStatus = MutableLiveData<GeneralResponseStatus>()
    var houseHoldList = MutableLiveData<List<HouseholdMember>>()
    var houseHoldMembers = MutableLiveData<List<HoouseHoldMembers>>()
    var _iDLookUpData = MutableLiveData<CustomerIDData>()
    val iDLookUpData: LiveData<CustomerIDData>
        get() = _iDLookUpData
    var _repaymentScheduleData = MutableLiveData<List<RepaymentSchedule>>()
    val repaymentScheduleData: LiveData<List<RepaymentSchedule>>
        get() = _repaymentScheduleData
    private var _nameLookUpData = MutableLiveData<LookUpByNameResponse>()
    val nameLookUpData: LiveData<LookUpByNameResponse>
        get() = _nameLookUpData
    var customerList: ArrayList<CustomerInfo> = arrayListOf()
    private val viewModelJob = Job()
    val repo: DropdownItemRepository
    private val uiScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        val genderDao =
            FieldAppDatabase.getFieldAppDatabase(BaseApp.applicationContext()).dropdownItemDao()
        repo = DropdownItemRepository(genderDao)
        _status.value = null
        _statusId.value = null
        _isEmpty.value = false
        _statusCode.value = null
        _statusDelCode.value = null
        _statusE.value = null
        _statusI.value = null
        _statusUpdate.value = null
        _refreshData.value = false
        _statusIncome.value = null
    }

    fun stopObserving() {
        _statusUpdate.value = null
        _status.value = null
        _statusId.value = null
        _statusDelCode.value = null
        _statusCode.value = null
        _statusIncome.value = null
        _statusI.value = null
        _refreshData.value = false
        _statusE.value = null
    }

    fun idLookup(idLookUpDTO: CustomerIDLookUpDTO) {
        uiScope.launch {
            //  houseHoldMembers.value= arrayListOf()
            _responseGStatus.value = GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.customerIDLookupAsync(idLookUpDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    _responseGStatus.value = GeneralResponseStatus.DONE
                    _iDLookUpData.value = accResponse.data
                    _idNumber.value = accResponse.data.idNumber
                    Constants.lookupId = accResponse.data.idNumber
                    /*if (accResponse.data.householdMembers.isNotEmpty()){
                        _isEmpty.value=false
                        houseHoldMembers.value = accResponse.data.householdMembers
                    }else{
                        houseHoldMembers.value= arrayListOf()
                        _isEmpty.value=true
                    }*/
                    _statusId.value = accResponse.status
                } else {
                    _statusMessage.value = accResponse.message
                    _responseGStatus.value = GeneralResponseStatus.DONE
                    _statusId.value = 0
                }

            } catch (e: Throwable) {
                _responseGStatus.value = GeneralResponseStatus.DONE
                e.printStackTrace()
                if (e is IOException) {
                    _statusMessage.value = BaseApp.applicationContext()
                        .getString(R.string.no_network_connection)
                    _statusId.value = 0
                }
                _statusId.value = e.hashCode()
            }
        }

    }

    fun customerNameLookup(nameLookupDTO: NameLookupDTO) {
        uiScope.launch {
            //  houseHoldMembers.value= arrayListOf()
            _responseGStatus.value = GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.customerNameLookupAsync(nameLookupDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    customerList.clear()
                    customerList.addAll(accResponse.data)
                    Log.d("TAG", "customerNameLookup: ${customerList.size}")
                    _statusId.value = accResponse.status
                    _responseGStatus.value = GeneralResponseStatus.DONE
                    _nameLookUpData.value = accResponse
                } else {
                    customerList.clear()
                    _statusMessage.value = accResponse.message
                    _responseGStatus.value = GeneralResponseStatus.DONE
                    _statusId.value = 0
                }

            } catch (e: Throwable) {
                customerList.clear()
                _responseGStatus.value = GeneralResponseStatus.DONE
                e.printStackTrace()
                if (e is IOException) {
                    _statusMessage.value = BaseApp.applicationContext()
                        .getString(R.string.no_network_connection)
                    _statusId.value = 0
                }
                _statusId.value = e.hashCode()
            }
        }

    }

    fun addExpenses(expensesDTO: ExpensesDTO) {
        uiScope.launch {
            _responseExpensesStatus.value = GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.addExpenses(expensesDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    _responseExpensesStatus.value = GeneralResponseStatus.DONE
                    val idLookUpDTO = CustomerIDLookUpDTO()
                    idLookUpDTO.idNumber = _idNumber.value!!
                    idLookup(idLookUpDTO)
                    _statusE.value = accResponse.status
                } else {
                    _responseExpensesStatus.value = GeneralResponseStatus.ERROR
                    _statusMessage.value = accResponse.message
                    _statusE.value = 0
                }

            } catch (e: Throwable) {
                _responseExpensesStatus.value = GeneralResponseStatus.ERROR
                e.printStackTrace()
                _statusE.value = e.hashCode()
            }
        }

    }

    fun addMembers(addHouseHoldMember: AddHouseHoldMember) {
        uiScope.launch {
            _responseStatus.value = GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.addMember(addHouseHoldMember)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    val idLookUpDTO = CustomerIDLookUpDTO()
                    idLookUpDTO.idNumber = _idNumber.value!!
                    idLookup(idLookUpDTO)
                    _responseStatus.value = GeneralResponseStatus.DONE
                    _statusCode.value = accResponse.status
                } else {
                    _statusMessage.value = accResponse.message
                    _responseStatus.value = GeneralResponseStatus.ERROR
                    _statusCode.value = 0
                }

            } catch (e: Throwable) {
                _responseStatus.value = GeneralResponseStatus.ERROR
                e.printStackTrace()
                _statusCode.value = e.hashCode()
            }
        }

    }

    fun updateMembers(updateHouseholdMembersDTO: UpdateHouseholdMembersDTO) {
        uiScope.launch {
            val request = FieldAgentApi.retrofitService.updateMember(updateHouseholdMembersDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    val idLookUpDTO = CustomerIDLookUpDTO()
                    idLookUpDTO.idNumber = _idNumber.value!!
                    idLookup(idLookUpDTO)
                    _statusUpdate.value = accResponse.status
                } else {
                    _statusMessage.value = accResponse.message
                    _statusUpdate.value = 0
                }

            } catch (e: Throwable) {
                e.printStackTrace()
                _statusUpdate.value = e.hashCode()
            }
        }

    }

    fun deleteMember(deleteMemberDTO: DeleteMemberDTO) {
        uiScope.launch {
            _responseDelStatus.value = GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.removeMembeAsync(deleteMemberDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    _responseDelStatus.value = GeneralResponseStatus.DONE
                    val idLookUpDTO = CustomerIDLookUpDTO()
                    idLookUpDTO.idNumber = _idNumber.value!!
                    idLookup(idLookUpDTO)
                    _statusDelCode.value = 1
                } else {
                    _responseDelStatus.value = GeneralResponseStatus.ERROR
                    _statusMessage.value = accResponse.message
                    _statusDelCode.value = 0
                }

            } catch (e: Throwable) {
                _responseDelStatus.value = GeneralResponseStatus.ERROR
                _statusDelCode.value = e.hashCode()
            }
        }

    }

    fun addIncome(
        id_number: RequestBody,
        household_income_net_salary: RequestBody,
        household_income_own_salary: RequestBody,
        household_income_total_sales: RequestBody,
        household_income_profit: RequestBody,
        household_income_rental_income: RequestBody,
        household_income_remittance_donations: RequestBody,
        household_income_other: RequestBody
    ) {
        uiScope.launch {
            _expenseLoadingStatus.value = GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.addIncome(
                id_number,
                household_income_net_salary,
                household_income_own_salary,
                household_income_total_sales,
                household_income_profit,
                household_income_rental_income,
                household_income_remittance_donations,
                household_income_other
            )
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    val idLookUpDTO = CustomerIDLookUpDTO()
                    idLookUpDTO.idNumber = _idNumber.value!!
                    idLookup(idLookUpDTO)
                    _expenseLoadingStatus.value = GeneralResponseStatus.DONE
                    _statusI.value = accResponse.status
                } else {
                    _statusMessage.value = accResponse.message
                    _expenseLoadingStatus.value = GeneralResponseStatus.ERROR
                    _statusI.value = 0
                }

            } catch (e: Throwable) {
                _expenseLoadingStatus.value = GeneralResponseStatus.ERROR
                e.printStackTrace()
                _statusI.value = e.hashCode()
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}