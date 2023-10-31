package com.deefrent.rnd.fieldapp.view.homepage.loans

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.dtos.*
import com.deefrent.rnd.fieldapp.models.loans.RepaymentSchedule
import com.deefrent.rnd.fieldapp.models.loans.Transaction
import com.deefrent.rnd.fieldapp.network.FieldAgentApi
import com.deefrent.rnd.fieldapp.network.models.*
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import kotlin.math.log

class LoanLookUpViewModel() : ViewModel() {
    private var _status = MutableLiveData<Int?>()
    val status: MutableLiveData<Int?>
        get() = _status
    private var _statusId = MutableLiveData<Int?>()
    val statusId: MutableLiveData<Int?>
        get() = _statusId
    private var _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String>
        get() = _statusMessage
    private var _statusCode = MutableLiveData<Int?>()
    val statusCode: MutableLiveData<Int?>
        get() = _statusCode
    private var _statusUpdateCode = MutableLiveData<Int?>()
    val statusUpdateCode: MutableLiveData<Int?>
        get() = _statusUpdateCode

    val loanLookUpData: LiveData<LoanLookupData>
        get() = _loanLookUpData
    private var _payResponseStatus = MutableLiveData<GeneralResponseStatus>()
    val payResponseStatus: LiveData<GeneralResponseStatus>
        get() = _payResponseStatus
    private var _disburseResponseStatus = MutableLiveData<GeneralResponseStatus>()
    val disburseResponseStatus: LiveData<GeneralResponseStatus>
        get() = _disburseResponseStatus
    private var _responseGStatus = MutableLiveData<GeneralResponseStatus>()
    val responseGStatus: LiveData<GeneralResponseStatus>
        get() = _responseGStatus
    private var _responseStatus = MutableLiveData<GeneralResponseStatus>()
    val responseStatus: LiveData<GeneralResponseStatus>
        get() = _responseStatus
    private var _responseUpdateStatus = MutableLiveData<GeneralResponseStatus>()
    val responseUpdateStatus: LiveData<GeneralResponseStatus>
        get() = _responseUpdateStatus
    private var _previewData = MutableLiveData<DisburseLoanData>()
    val previewData: LiveData<DisburseLoanData>
        get() = _previewData

    private var _frequencyData = MutableLiveData<List<FrequencyData>>()
    val frequencyData: LiveData<List<FrequencyData>>
        get() = _frequencyData
    private var _loanPayableData = MutableLiveData<List<LoanPayableData>>()
    val loanPayableData: LiveData<List<LoanPayableData>>
        get() = _loanPayableData
    val amount = MutableLiveData<String>()
    val repaymentPeriodMeasure = MutableLiveData<String>()
    val repaymentPeriod = MutableLiveData<String>()
    val repaymentPeriodCycleMeasure = MutableLiveData<String>()
    val repaymentPeriodCycle = MutableLiveData<String>()
    val applicationDate = MutableLiveData<String>()
    val repaymentDate = MutableLiveData<String>()
    val purpose = MutableLiveData<String>()
    var supplierName = MutableLiveData<String>()
    var cost = MutableLiveData<String>()
    var supplierPhone = MutableLiveData<String>()
    var codition = MutableLiveData<String>()
    var formId = MutableLiveData<String>()
    val charges = MutableLiveData<String>()
    val descri = MutableLiveData<String>()
    var refCode = MutableLiveData<String>()
    var loanOfficerAmount = MutableLiveData<String>()
    var loanOfficerRemarks = MutableLiveData<String>()
    var currency = MutableLiveData<String>()
    private var _applyStatusCode = MutableLiveData<Int?>()
    val applyStatusCode: MutableLiveData<Int?>
        get() = _applyStatusCode
    private var _payStatusCode = MutableLiveData<Int?>()
    val payStatusCode: MutableLiveData<Int?>
        get() = _payStatusCode
    private var _disbStatusCode = MutableLiveData<Int?>()
    val disbStatusCode: MutableLiveData<Int?>
        get() = _disbStatusCode
    private var _cashCommitStatusCode = MutableLiveData<Int?>()
    val cashCommitStatusCode: MutableLiveData<Int?>
        get() = _cashCommitStatusCode
    private var _cashStatusCode = MutableLiveData<Int?>()
    val cashStatusCode: MutableLiveData<Int?>
        get() = _cashStatusCode
    private var _statusCommit = MutableLiveData<Int?>()
    val statusCommit: MutableLiveData<Int?>
        get() = _statusCommit
    private var _statusDCommit = MutableLiveData<Int?>()
    val statusDCommit: MutableLiveData<Int?>
        get() = _statusDCommit
    var _idNumber = MutableLiveData<String>()


    val idNumber: LiveData<String>
        get() = _idNumber
    lateinit var loanHistoryItem: LoanHistory

    /**guarantors details*/
    private var _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean>
        get() = _isEmpty
    private val viewModelJob = Job()
    private var _nameLookUpData = MutableLiveData<LoanLookupByNameResponse>()
    val nameLookUpData: LiveData<LoanLookupByNameResponse>
        get() = _nameLookUpData
    var customerList: ArrayList<LoanLookupData> = arrayListOf()
    private val uiScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    var repaymentScheduleList: ArrayList<RepaymentSchedule> = arrayListOf()
    var loanMiniStatementList: ArrayList<Transaction> = arrayListOf()

    init {
        _status.value = null
        getFrequency()
        _statusCode.value = null
        _statusUpdateCode.value = null
        _disbStatusCode.value = null
        _cashCommitStatusCode.value = null
        _cashStatusCode.value = null
        _statusCommit.value = null
        _statusDCommit.value = null
        _payStatusCode.value = null
        _applyStatusCode.value = null
    }

    fun stopObserving() {
        _statusDCommit.value = null
        _cashCommitStatusCode.value = null
        _cashStatusCode.value = null
        _status.value = null
        _disbStatusCode.value = null
        _statusCode.value = null
        _statusUpdateCode.value = null
        _statusCommit.value = null
        _payStatusCode.value = null
        _statusCommit.value = null
        _applyStatusCode.value = null
    }

    fun getRepaymentSchedule(repaymentScheduleDTO: RepaymentScheduleDTO) {
        uiScope.launch {
            //  houseHoldMembers.value= arrayListOf()
            _responseGStatus.value = GeneralResponseStatus.LOADING
            val request =
                FieldAgentApi.retrofitService.getRepaymentScheduleAsync(repaymentScheduleDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    repaymentScheduleList =
                        accResponse.data as ArrayList<RepaymentSchedule> /* = java.util.ArrayList<com.ekenya.rnd.fieldapp.models.loans.RepaymentSchedule> */
                    Log.d("TAG", "customerNameLookup: ${repaymentScheduleList.size}")
                    _statusId.value = accResponse.status
                    _responseGStatus.value = GeneralResponseStatus.DONE
                } else {
                    repaymentScheduleList = arrayListOf()
                    _statusMessage.value = accResponse.message
                    _responseGStatus.value = GeneralResponseStatus.DONE
                    _statusId.value = 0
                }

            } catch (e: Throwable) {
                repaymentScheduleList = arrayListOf()
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

    fun getLoanMiniStatement(repaymentScheduleDTO: RepaymentScheduleDTO) {
        uiScope.launch {
            //  houseHoldMembers.value= arrayListOf()
            _responseGStatus.value = GeneralResponseStatus.LOADING
            val request =
                FieldAgentApi.retrofitService.getLoanMiniStatementAsync(repaymentScheduleDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    loanMiniStatementList =
                        accResponse.data.transactions as ArrayList<Transaction> /* = java.util.ArrayList<com.ekenya.rnd.fieldapp.models.loans.Transaction> */
                    Log.d("TAG", "customerNameLookup: ${loanMiniStatementList.size}")
                    _statusId.value = accResponse.status
                    _responseGStatus.value = GeneralResponseStatus.DONE
                } else {
                    loanMiniStatementList = arrayListOf()
                    _statusMessage.value = accResponse.message
                    _responseGStatus.value = GeneralResponseStatus.DONE
                    _statusId.value = 0
                }

            } catch (e: Throwable) {
                loanMiniStatementList = arrayListOf()
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

    var _loanLookUpData = MutableLiveData<LoanLookupData>()
    fun loanLookUp(loanLookUpDTO: LoanLookUpDTO) {
        uiScope.launch {
            //  houseHoldMembers.value= arrayListOf()
            _responseStatus.value = GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.loanLookupAsync(loanLookUpDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    _idNumber.value = accResponse.data.idNumber
                    _responseStatus.value = GeneralResponseStatus.DONE
                    _loanLookUpData.value = accResponse.data
                    _status.value = accResponse.status
                } else {
                    _statusMessage.value = accResponse.message
                    _responseStatus.value = GeneralResponseStatus.DONE
                    _status.value = 0
                }

            } catch (e: Throwable) {
                _responseStatus.value = GeneralResponseStatus.DONE
                e.printStackTrace()
                if (e is IOException) {
                    _statusMessage.value = BaseApp.applicationContext()
                        .getString(R.string.no_network_connection)
                    _status.value = 0
                }
                _status.value = e.hashCode()
            }
        }

    }

    fun loanNameLookup(nameLookupDTO: NameLookupDTO) {
        uiScope.launch {
            //  houseHoldMembers.value= arrayListOf()
            _responseStatus.value = GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.loanLookupByNameAsync(nameLookupDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    customerList.clear()
                    customerList.addAll(accResponse.data)
                    Log.d("TAG", "customerNameLookup: ${customerList.size}")
                    _status.value = accResponse.status
                    _responseStatus.value = GeneralResponseStatus.DONE
                    _nameLookUpData.value = accResponse
                } else {
                    customerList.clear()
                    _statusMessage.value = accResponse.message
                    _responseStatus.value = GeneralResponseStatus.DONE
                    _status.value = 0
                }

            } catch (e: Throwable) {
                customerList.clear()
                _responseStatus.value = GeneralResponseStatus.DONE
                e.printStackTrace()
                if (e is IOException) {
                    _statusMessage.value = BaseApp.applicationContext()
                        .getString(R.string.no_network_connection)
                    _status.value = 0
                }
                _status.value = e.hashCode()
            }
        }

    }

    private fun getFrequency() {
        uiScope.launch {
            val request = FieldAgentApi.retrofitService.getFrequency()
            try {
                val accResponse = request.await()
                _frequencyData.value = accResponse.data
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

    }

    fun applyLoan(loanRequestDTO: LoanRequestDTO) {
        uiScope.launch {
            _payResponseStatus.value = GeneralResponseStatus.LOADING
            val applyLoanDetails = FieldAgentApi.retrofitService.loanRequestAsync(loanRequestDTO)
            try {
                val applyloanResponse = applyLoanDetails.await()
                if (applyloanResponse.status == 1) {
                    _payResponseStatus.value = GeneralResponseStatus.DONE
                    formId.value = applyloanResponse.data.formId.toString()
                    charges.value = applyloanResponse.data.charges.toString()
                    _applyStatusCode.value = applyloanResponse.status
                } else if (applyloanResponse.status == 0) {
                    _payResponseStatus.value = GeneralResponseStatus.LOADING
                    _statusMessage.value = applyloanResponse.message
                    _applyStatusCode.value = applyloanResponse.status
                }
            } catch (e: HttpException) {
                _payResponseStatus.value = GeneralResponseStatus.ERROR
                _applyStatusCode.value = e.code()
            }
        }
    }

    fun loanApplyCommit() {
        uiScope.launch {
            val formIDDTO = FormIDDTO()
            formIDDTO.formId = formId.value!!
            val loanRequest = FieldAgentApi.retrofitService.loanCommitAsync(formIDDTO)
            try {
                val loanResponse = loanRequest.await()
                if (loanResponse.status == 1) {
                    val loanLookUpDTO = LoanLookUpDTO()
                    loanLookUpDTO.idNumber = _idNumber.value!!
                    loanLookUpDTO.isLoan = 1
                    loanLookUp(loanLookUpDTO)
                    refCode.value = loanResponse.data.transactionCode
                    _statusCommit.value = 1
                } else {
                    _statusMessage.value = loanResponse.message
                    _statusCommit.value = 0
                }

            } catch (e: HttpException) {
                _statusCommit.value = e.code()
            }

        }
    }

    fun disburseLoan(disburseLoanPreviewDTO: DisburseLoanPreviewDTO) {
        uiScope.launch {
            _disburseResponseStatus.value = GeneralResponseStatus.LOADING
            val disbLoanDetails =
                FieldAgentApi.retrofitService.disburseLoanAsync(disburseLoanPreviewDTO)
            try {
                val disbloanResponse = disbLoanDetails.await()
                if (disbloanResponse.status == 1) {
                    _disburseResponseStatus.value = GeneralResponseStatus.DONE
                    _previewData.value = disbloanResponse.data
                    _disbStatusCode.value = disbloanResponse.status
                } else if (disbloanResponse.status == 0) {
                    _disburseResponseStatus.value = GeneralResponseStatus.ERROR
                    _statusMessage.value = disbloanResponse.message
                    _disbStatusCode.value = disbloanResponse.status
                }
            } catch (e: HttpException) {
                _disburseResponseStatus.value = GeneralResponseStatus.ERROR
                _disbStatusCode.value = e.code()
            }
        }
    }

    var loanDisburseCommitData = MutableLiveData<GeneralCommitData>()
    fun loanDisburseCommit(disburseLoanDTO: DisburseLoanDTO) {
        uiScope.launch {
            _responseGStatus.value = GeneralResponseStatus.LOADING
            val loanRequest = FieldAgentApi.retrofitService.disburseLoanCommitAsync(disburseLoanDTO)
            try {
                val loanResponse = loanRequest.await()
                if (loanResponse.status == 1) {
                    _responseGStatus.value = GeneralResponseStatus.DONE
                    val loanLookUpDTO = LoanLookUpDTO()
                    loanLookUpDTO.idNumber = _idNumber.value!!
                    loanLookUpDTO.isLoan = 1
                    loanLookUp(loanLookUpDTO)
                    loanDisburseCommitData.value = loanResponse.data
                    refCode.value = loanResponse.data.transactionCode
                    currency.value = loanResponse.data.defaultCurrency
                    _statusDCommit.value = 1
                } else {
                    _responseGStatus.value = GeneralResponseStatus.ERROR
                    _statusMessage.value = loanResponse.message
                    _statusDCommit.value = 0
                }

            } catch (e: HttpException) {
                _responseGStatus.value = GeneralResponseStatus.ERROR
                _statusDCommit.value = e.code()
            }

        }
    }

    fun cashOutLoan(disburseLoanPreviewDTO: DisburseLoanPreviewDTO) {
        uiScope.launch {
            _disburseResponseStatus.value = GeneralResponseStatus.LOADING
            val disbLoanDetails =
                FieldAgentApi.retrofitService.cashOutLoansAsync(disburseLoanPreviewDTO)
            try {
                val disbloanResponse = disbLoanDetails.await()
                if (disbloanResponse.status == 1) {
                    _disburseResponseStatus.value = GeneralResponseStatus.DONE
                    _previewData.value = disbloanResponse.data
                    _cashStatusCode.value = disbloanResponse.status
                } else if (disbloanResponse.status == 0) {
                    _disburseResponseStatus.value = GeneralResponseStatus.ERROR
                    _statusMessage.value = disbloanResponse.message
                    _cashStatusCode.value = disbloanResponse.status
                }
            } catch (e: HttpException) {
                _disburseResponseStatus.value = GeneralResponseStatus.ERROR
                _cashStatusCode.value = e.code()
            }
        }
    }

    fun cashOutLoanCommit(cashOutDTO: CashOutDTO) {
        uiScope.launch {
            _responseGStatus.value = GeneralResponseStatus.LOADING
            val loanRequest = FieldAgentApi.retrofitService.cashOutLoansCommitAsync(cashOutDTO)
            try {
                val loanResponse = loanRequest.await()
                if (loanResponse.status == 1) {
                    _responseGStatus.value = GeneralResponseStatus.DONE
                    val loanLookUpDTO = LoanLookUpDTO()
                    loanLookUpDTO.idNumber = _idNumber.value!!
                    loanLookUpDTO.isLoan = 1
                    loanLookUp(loanLookUpDTO)
                    refCode.value = loanResponse.data.transactionCode
                    currency.value = loanResponse.data.defaultCurrency
                    _cashCommitStatusCode.value = 1
                } else {
                    _responseGStatus.value = GeneralResponseStatus.ERROR
                    _statusMessage.value = loanResponse.message
                    _cashCommitStatusCode.value = 0
                }

            } catch (e: HttpException) {
                _responseGStatus.value = GeneralResponseStatus.ERROR
                _cashCommitStatusCode.value = e.code()
            }

        }
    }

    fun payLoanPreview(payLoanDTO: PayLoanDTO) {
        uiScope.launch {
            _payResponseStatus.value = GeneralResponseStatus.LOADING
            val payLoanDetails = FieldAgentApi.retrofitService.loanRepayPreviewAsync(payLoanDTO)
            try {
                val payloanResponse = payLoanDetails.await()
                if (payloanResponse.status == 1) {
                    _payResponseStatus.value = GeneralResponseStatus.DONE
                    formId.value = payloanResponse.data.formId.toString()
                    charges.value = payloanResponse.data.charges.toString()
                    _payStatusCode.value = payloanResponse.status
                } else if (payloanResponse.status == 0) {
                    _payResponseStatus.value = GeneralResponseStatus.ERROR
                    _statusMessage.value = payloanResponse.message
                    _payStatusCode.value = payloanResponse.status
                }
            } catch (e: HttpException) {
                _payResponseStatus.value = GeneralResponseStatus.ERROR
                _payStatusCode.value = e.code()
            }
        }
    }

    var payLoanCommitData = MutableLiveData<GeneralCommitData>()
    fun payLoanCommit() {
        uiScope.launch {
            val formIDDTO = FormIDDTO()
            formIDDTO.formId = formId.value!!
            val loanRequest = FieldAgentApi.retrofitService.loanRepayCommitAsync(formIDDTO)
            try {
                val loanResponse = loanRequest.await()
                if (loanResponse.status == 1) {
                    val loanLookUpDTO = LoanLookUpDTO()
                    loanLookUpDTO.idNumber = _idNumber.value!!
                    loanLookUpDTO.isLoan = 1
                    payLoanCommitData.value = loanResponse.data
                    loanLookUp(loanLookUpDTO)
                    currency.value = loanResponse.data.defaultCurrency
                    refCode.value = loanResponse.data.transactionCode
                    _statusCommit.value = 1
                } else {
                    _statusMessage.value = loanResponse.message
                    _statusCommit.value = 0
                }

            } catch (e: HttpException) {
                _statusCommit.value = e.code()
            }

        }
    }

    fun updateLoan(updateLoanDTO: UpdateLoanDTO) {
        uiScope.launch {
            _responseUpdateStatus.value = GeneralResponseStatus.LOADING
            val loanRequest = FieldAgentApi.retrofitService.updateLoanAsync(updateLoanDTO)
            try {
                val loanResponse = loanRequest.await()
                if (loanResponse.status == 1) {
                    _responseUpdateStatus.value = GeneralResponseStatus.DONE
                    val loanLookUpDTO = LoanLookUpDTO()
                    loanLookUpDTO.idNumber = _idNumber.value!!
                    loanLookUpDTO.isLoan = 1
                    loanLookUp(loanLookUpDTO)
                    _statusUpdateCode.value = 1
                } else {
                    _responseUpdateStatus.value = GeneralResponseStatus.ERROR
                    _statusMessage.value = loanResponse.message
                    _statusUpdateCode.value = 0
                }

            } catch (e: HttpException) {
                _responseUpdateStatus.value = GeneralResponseStatus.ERROR
                _statusUpdateCode.value = e.code()
            }

        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}