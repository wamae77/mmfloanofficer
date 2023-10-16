package com.deefrent.rnd.fieldapp.view.homepage.billers

import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.dtos.lookUp.IDLookUpDTO
import com.deefrent.rnd.fieldapp.dtos.NameLookupDTO
import com.deefrent.rnd.fieldapp.dtos.billers.BillPaymentDTO
import com.deefrent.rnd.fieldapp.dtos.billers.BillPaymentPreviewDTO
import com.deefrent.rnd.fieldapp.dtos.customer.GetWalletAccountsDTO
import com.deefrent.rnd.fieldapp.models.customer.CustomerInfo
import com.deefrent.rnd.fieldapp.models.customer.WalletAccount
import com.deefrent.rnd.fieldapp.network.FieldAgentApi
import com.deefrent.rnd.fieldapp.network.models.Biller
import com.deefrent.rnd.fieldapp.responses.*
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class BillersViewModel : ViewModel() {
    private var _statusCode = MutableLiveData<Int?>()
    val statusCode: MutableLiveData<Int?>
        get() = _statusCode

    private var _statusCodePreview = MutableLiveData<Int?>()
    val statusCodePreview: MutableLiveData<Int?>
        get() = _statusCodePreview

    private var _status = MutableLiveData<Int?>()
    val status: MutableLiveData<Int?>
        get() = _status
    private var _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String>
        get() = _statusMessage
    private var _billList = MutableLiveData<List<Biller>>()
    val billList: LiveData<List<Biller>?>
        get() = _billList
    private var _walletAccountsList = MutableLiveData<List<WalletAccount>>()
    val walletAccountsList: LiveData<List<WalletAccount>>
        get() = _walletAccountsList
    var _billPaymentPreviewResponse = MutableLiveData<GetBillPaymentPreviewResponse>()
    var _billPaymentResponse = MutableLiveData<PostBillPaymentResponse>()
    val billPreviewResponse: MutableLiveData<GetBillPaymentPreviewResponse>
        get() = _billPaymentPreviewResponse
    private var _responseStatus = MutableLiveData<GeneralResponseStatus>()
    val responseStatus: LiveData<GeneralResponseStatus>
        get() = _responseStatus
    var _idNumber = MutableLiveData<String>()
    var phoneNumber = MutableLiveData<String>()
    val idNumber: LiveData<String>
        get() = _idNumber
    var _nameLookUpData = MutableLiveData<LookUpByNameResponse>()
    val nameLookUpData: LiveData<LookUpByNameResponse>
        get() = _nameLookUpData
    var customerList: ArrayList<CustomerInfo> = arrayListOf()
    var _idLookUpData = MutableLiveData<LookUpByIDResponse>()
    val idLookUpData: LiveData<LookUpByIDResponse>
        get() = _idLookUpData
    var selectedCustomer = MutableLiveData<CustomerInfo>()
    var postBillPaymentDTO = MutableLiveData<BillPaymentDTO>()
    var postBillPaymentData = MutableLiveData<PostBillPaymentData>()
    lateinit var paymentCurrency: String
    lateinit var customerName: String
    lateinit var walletName: String
    lateinit var billerName: String
    var recipientName= MutableLiveData<String>()

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    init {
        _statusCode.value = null
        _statusCodePreview.value = null
        _status.value = null
    }

    fun stopObserving() {
        _statusCode.value = null
        _status.value = null
        _statusCodePreview.value = null
    }

    fun getBillPaymentPreview(billPaymentPreviewDTO: BillPaymentPreviewDTO) {
        uiScope.launch {
            _responseStatus.value = GeneralResponseStatus.LOADING
            val billPreviewDetails =
                FieldAgentApi.retrofitService.getBillPaymentPreviewAsync(billPaymentPreviewDTO)
            try {
                val billPaymentPreviewResponse = billPreviewDetails.await()
                Log.d("TAG", "getBillPaymentPreview1: ${billPaymentPreviewResponse}")
                if (billPaymentPreviewResponse.status == 1) {
                    _billPaymentPreviewResponse.value = billPaymentPreviewResponse
                    recipientName.value = billPaymentPreviewResponse.data.recipientName
                    _responseStatus.value = GeneralResponseStatus.DONE
                    Log.d("TAG", "getBillPaymentPreview1: ${_billPaymentPreviewResponse.value}")
                    _statusCode.value = 1
                } else if (billPaymentPreviewResponse.status == 0) {
                    _billPaymentPreviewResponse.value = billPaymentPreviewResponse
                    _responseStatus.value = GeneralResponseStatus.DONE
                    Log.d("TAG", "getBillPaymentPreview1: ${_billPaymentPreviewResponse.value}")
                    _statusCode.value = 0
                }
            } catch (e: Exception) {
                _responseStatus.value = GeneralResponseStatus.ERROR
                _statusCode.value = e.hashCode()
                Log.d("TAG", "getBillPaymentPreview: error ${e.localizedMessage}")
            }
        }
    }

    fun postBillPayment(billPaymentDTO: BillPaymentDTO) {
        postBillPaymentDTO.value = billPaymentDTO
        uiScope.launch {
            _responseStatus.value = GeneralResponseStatus.LOADING
            val billPaymentDetails =
                FieldAgentApi.retrofitService.postBillPaymentAsync(billPaymentDTO)
            try {
                val billPaymentResponse = billPaymentDetails.await()
                Log.d("TAG", "getBillPaymentPreview1: ${billPaymentResponse}")
                if (billPaymentResponse.status == 1) {
                    _billPaymentResponse.value = billPaymentResponse
                    _responseStatus.value = GeneralResponseStatus.DONE
                    Log.d("TAG", "getBillPaymentPreview1: ${_billPaymentResponse.value}")
                    _statusCode.value = 1
                } else if (billPaymentResponse.status == 0) {
                    _billPaymentResponse.value = billPaymentResponse
                    _responseStatus.value = GeneralResponseStatus.DONE
                    Log.d("TAG", "getBillPaymentPreview1: ${_billPaymentResponse.value}")
                    _statusCode.value = 0
                }
            } catch (e: Exception) {
                _responseStatus.value = GeneralResponseStatus.ERROR
                _statusCode.value = e.hashCode()
                Log.d("TAG", "getBillPaymentPreview: error ${e.localizedMessage}")
            }
        }
    }

    fun getAllBillers() {
        uiScope.launch {
            _responseStatus.value = GeneralResponseStatus.LOADING
            val billerDetails = FieldAgentApi.retrofitService.getBillersAsync()
            try {
                val billersResponse = billerDetails.await()
                _responseStatus.value = GeneralResponseStatus.DONE
                if (billersResponse.status == 1) {
                    _billList.postValue(billersResponse.data)
                    _statusCode.value = 1
                } else {
                    _responseStatus.value = GeneralResponseStatus.DONE
                    _statusCode.value = 0
                }
            } catch (e: Exception) {
                _responseStatus.value = GeneralResponseStatus.ERROR
                _statusCode.value = e.hashCode()
            }
        }
    }

    fun getCustomerWalletAccounts(getWalletAccountsDTO: GetWalletAccountsDTO) {
        uiScope.launch {
            _responseStatus.value = GeneralResponseStatus.LOADING
            val walletDetails =
                FieldAgentApi.retrofitService.getCustomerWalletAccountsAsync(getWalletAccountsDTO)
            try {
                val walletResponse = walletDetails.await()
                _responseStatus.value = GeneralResponseStatus.DONE
                if (walletResponse.status == 1) {
                    _walletAccountsList.postValue(walletResponse.data)
                    _statusCode.value = 1
                } else {
                    _responseStatus.value = GeneralResponseStatus.DONE
                    _statusCode.value = 0
                }
            } catch (e: Exception) {
                _responseStatus.value = GeneralResponseStatus.ERROR
                _statusCode.value = e.hashCode()
            }
        }
    }

    fun customerIDLookUp(idLookUpDTO: IDLookUpDTO) {
        uiScope.launch {
            //  houseHoldMembers.value= arrayListOf()
            _responseStatus.value = GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.idLookupAsync(idLookUpDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    _idNumber.value = accResponse.data.idNumber
                    selectedCustomer.value = accResponse.data
                    //phoneNumber.value=idLookUpDTO
                    _responseStatus.value = GeneralResponseStatus.DONE
                    _idLookUpData.value = accResponse
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

    fun customerNameLookup(nameLookupDTO: NameLookupDTO) {
        uiScope.launch {
            //  houseHoldMembers.value= arrayListOf()
            _responseStatus.value = GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.nameLookupAsync(nameLookupDTO)
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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
