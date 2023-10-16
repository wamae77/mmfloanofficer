package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.updatecdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.dtos.*
import com.deefrent.rnd.fieldapp.network.FieldAgentApi
import com.deefrent.rnd.fieldapp.network.models.*
import com.deefrent.rnd.fieldapp.network.models.OtherBorrowing
import com.deefrent.rnd.fieldapp.room.database.FieldAppDatabase
import com.deefrent.rnd.fieldapp.room.repos.DropdownItemRepository
import com.deefrent.rnd.fieldapp.utils.Constants
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.IOException

class CustomerInfoViewModel : ViewModel() {
    private var _status = MutableLiveData<Int?>()
    val status: MutableLiveData<Int?>
        get() = _status
    private var _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String>
        get() = _statusMessage
    private var _statusCode = MutableLiveData<Int?>()
    val statusCode: MutableLiveData<Int?>
        get() = _statusCode
    private var _statusACode = MutableLiveData<Int?>()
    val statusACode: MutableLiveData<Int?>
        get() = _statusACode
    private var _statusBDCode = MutableLiveData<Int?>()
    val statusBDCode: MutableLiveData<Int?>
        get() = _statusBDCode
    private var _statusBACode = MutableLiveData<Int?>()
    val statusBACode: MutableLiveData<Int?>
        get() = _statusBACode
    private var _statusGCode = MutableLiveData<Int?>()
    val statusGCode: MutableLiveData<Int?>
        get() = _statusGCode
    private var _statusRGCode = MutableLiveData<Int?>()
    val statusRGCode: MutableLiveData<Int?>
        get() = _statusRGCode
    private var _statusAGCode = MutableLiveData<Int?>()
    val statusAGCode: MutableLiveData<Int?>
        get() = _statusAGCode
    private var _statusRCode = MutableLiveData<Int?>()
    val statusRCode: MutableLiveData<Int?>
        get() = _statusRCode
    private var _statusCoCode = MutableLiveData<Int?>()
    val statusCoCode: MutableLiveData<Int?>
        get() = _statusCoCode
    private var _statusBCode = MutableLiveData<Int?>()
    val statuBsCode: MutableLiveData<Int?>
        get() = _statusBCode
    private var _statusNokCode = MutableLiveData<Int?>()
    val statusNokCode: MutableLiveData<Int?>
        get() = _statusNokCode
    private var _responseStatus = MutableLiveData<GeneralResponseStatus>()
    val responseStatus: LiveData<GeneralResponseStatus>
        get() = _responseStatus
    private var _responseRemGStatus = MutableLiveData<GeneralResponseStatus>()
    val responseRemGStatus: LiveData<GeneralResponseStatus>
        get() = _responseRemGStatus
    private var _responseUpGStatus = MutableLiveData<GeneralResponseStatus>()
    val responseUpGStatus: LiveData<GeneralResponseStatus>
        get() = _responseUpGStatus
    private var _responseUpGDocStatus = MutableLiveData<GeneralResponseStatus>()
    val responseUpGDocStatus: LiveData<GeneralResponseStatus>
        get() = _responseUpGDocStatus
    private var _responseAddGStatus = MutableLiveData<GeneralResponseStatus>()
    val responseAddGStatus: LiveData<GeneralResponseStatus>
        get() = _responseAddGStatus
    private var _responseAddCoStatus = MutableLiveData<GeneralResponseStatus>()
    val responseAddCoStatus: LiveData<GeneralResponseStatus>
        get() = _responseAddCoStatus
    private var _responseDelCoStatus = MutableLiveData<GeneralResponseStatus>()
    val responseDelCoStatus: LiveData<GeneralResponseStatus>
        get() = _responseDelCoStatus
    private var _responseUpCoStatus = MutableLiveData<GeneralResponseStatus>()
    val responseUpCoStatus: LiveData<GeneralResponseStatus>
        get() = _responseUpCoStatus
    private var _responseUpCoDocStatus = MutableLiveData<GeneralResponseStatus>()
    val responseUpCoDocStatus: LiveData<GeneralResponseStatus>
        get() = _responseUpCoDocStatus
    private var _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean>
        get() = _isEmpty

    private var _responseGStatus = MutableLiveData<GeneralResponseStatus>()
    val responseGStatus: LiveData<GeneralResponseStatus>
        get() = _responseGStatus
    private var _statusDocCode = MutableLiveData<Int?>()
    val statusDocCode: MutableLiveData<Int?>
        get() = _statusDocCode

    /**guarantors details*/
    private var _detailsData = MutableLiveData<FullCustomerDetailsData>()
    val detailsData: LiveData<FullCustomerDetailsData>
        get() = _detailsData
    private var _borrowingData = MutableLiveData<List<OtherBorrowing>>()
    val borrowingData: LiveData<List<OtherBorrowing>>
        get() = _borrowingData
    private val viewModelJob = Job()
    val repo: DropdownItemRepository
    private val uiScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        val genderDao =
            FieldAppDatabase.getFieldAppDatabase(BaseApp.applicationContext()).dropdownItemDao()
        repo = DropdownItemRepository(genderDao)
        getCustomerFullDetails()
        _status.value = null
        _isEmpty.value = false
        _statusCode.value = null
        _statusRGCode.value = null
        _statusAGCode.value = null
        _statusACode.value = null
        _statusBACode.value = null
        _statusBDCode.value = null
        _statusGCode.value = null
        _statusRCode.value = null
        _statusBCode.value = null
        _statusCoCode.value = null
        _statusNokCode.value = null
        _statusDocCode.value = null
    }

    fun stopObserving() {
        _statusRGCode.value = null
        _status.value = null
        _statusAGCode.value = null
        _statusCode.value = null
        _statusACode.value = null
        _statusBACode.value = null
        _statusBDCode.value = null
        _statusGCode.value = null
        _statusRCode.value = null
        _statusBCode.value = null
        _statusCoCode.value = null
        _statusNokCode.value = null
        _statusDocCode.value = null
    }

    fun getCustomerFullDetails() {
        uiScope.launch {
            _borrowingData.value = arrayListOf()
            _responseStatus.value = GeneralResponseStatus.LOADING
            val idLookUpDTO = CustomerIDLookUpDTO()
            idLookUpDTO.idNumber = Constants.lookupId
            val request = FieldAgentApi.retrofitService.getCustomerFullAsync(idLookUpDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    _detailsData.value = accResponse.data
                    if (accResponse.data.otherBorrowings.isNotEmpty()) {
                        _borrowingData.value = accResponse.data.otherBorrowings
                    } else {
                        _borrowingData.value = arrayListOf()
                    }
                    _statusCode.value = 1
                    _responseStatus.value = GeneralResponseStatus.DONE
                } else {
                    _statusMessage.value = accResponse.message
                    _statusCode.value = 0
                    _responseStatus.value = GeneralResponseStatus.DONE
                }

            } catch (e: Throwable) {
                _responseStatus.value = GeneralResponseStatus.DONE
                e.printStackTrace()
                if (e is IOException) {
                    _statusMessage.value = BaseApp.applicationContext()
                        .getString(R.string.no_network_connection)
                }
                _statusCode.value = e.hashCode()
            }
        }

    }

    fun updateCustomerBasicDetails(updateBasicInfoDTO: UpdateBasicInfoDTO) {
        uiScope.launch {
            val request = FieldAgentApi.retrofitService.updateBasicInfoAsync(updateBasicInfoDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    getCustomerFullDetails()
                    _status.value = 1
                } else {
                    _statusMessage.value = accResponse.message
                    _status.value = 0
                }

            } catch (e: Throwable) {
                _status.value = e.hashCode()
            }
        }

    }

    suspend fun uploadCustomerDocs(
        customerId: RequestBody,
        docTypeCode: RequestBody,
        file: MultipartBody.Part, isLastItem: Boolean
    ): Boolean {
        uiScope.launch(Dispatchers.Main) {
            _responseGStatus.value = GeneralResponseStatus.LOADING
        }

        val registerRequest =
            FieldAgentApi.retrofitService.uploadDocument2Async(
                customerId,
                docTypeCode,
                file
            )
        try {
            val registerResponse = registerRequest.await()
            when (registerResponse.status) {
                1 -> {
                    if (isLastItem) {
                        uiScope.launch(Dispatchers.Main) {
                            _responseGStatus.value = GeneralResponseStatus.DONE
                            _statusDocCode.value = registerResponse.status
                        }

                    }
                    return true
                }
                0 -> {
                    uiScope.launch(Dispatchers.Main) {
                        _statusMessage.value = registerResponse.message
                        _responseGStatus.value = GeneralResponseStatus.ERROR
                        _statusDocCode.value = 0
                    }

                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            uiScope.launch(Dispatchers.Main) {
                _statusDocCode.value = e.hashCode()
                _responseGStatus.value = GeneralResponseStatus.ERROR
            }

        }

        return false
    }

    suspend fun uploadCustomerCollateralDocs(
        customerId: RequestBody,
        docTypeCode: RequestBody,
        channelGeneratedCode: RequestBody,
        file: MultipartBody.Part, isLastItem: Boolean
    ): Boolean {
        uiScope.launch(Dispatchers.Main) {
            _responseUpCoDocStatus.value = GeneralResponseStatus.LOADING
        }

        val registerRequest =
            FieldAgentApi.retrofitService.uploadCollateralDocumentAsync(
                customerId,
                docTypeCode,
                channelGeneratedCode,
                file
            )
        try {
            val registerResponse = registerRequest.await()
            when (registerResponse.status) {
                1 -> {
                    if (isLastItem) {
                        uiScope.launch(Dispatchers.Main) {
                            _responseUpCoDocStatus.value = GeneralResponseStatus.DONE
                            _statusDocCode.value = registerResponse.status
                        }

                    }
                    return true
                }
                0 -> {
                    uiScope.launch(Dispatchers.Main) {
                        _statusMessage.value = registerResponse.message
                        _responseUpCoDocStatus.value = GeneralResponseStatus.ERROR
                        _statusDocCode.value = 0
                    }

                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            uiScope.launch(Dispatchers.Main) {
                _statusDocCode.value = e.hashCode()
                _responseUpCoDocStatus.value = GeneralResponseStatus.ERROR
            }

        }

        return false
    }

    suspend fun uploadCustomerGuarantorDocs(
        customerId: RequestBody,
        docTypeCode: RequestBody,
        channelGeneratedCode: RequestBody,
        file: MultipartBody.Part, isLastItem: Boolean
    ): Boolean {
        uiScope.launch(Dispatchers.Main) {
            _responseUpGDocStatus.value = GeneralResponseStatus.LOADING
        }

        val registerRequest =
            FieldAgentApi.retrofitService.uploadGuarantorDocumentAsync(
                customerId,
                docTypeCode,
                channelGeneratedCode,
                file
            )
        try {
            val registerResponse = registerRequest.await()
            when (registerResponse.status) {
                1 -> {
                    if (isLastItem) {
                        uiScope.launch(Dispatchers.Main) {
                            _responseUpGDocStatus.value = GeneralResponseStatus.DONE
                            _statusDocCode.value = registerResponse.status
                        }

                    }
                    return true
                }
                0 -> {
                    uiScope.launch(Dispatchers.Main) {
                        _statusMessage.value = registerResponse.message
                        _responseUpGDocStatus.value = GeneralResponseStatus.ERROR
                        _statusDocCode.value = 0
                    }

                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            uiScope.launch(Dispatchers.Main) {
                _statusDocCode.value = e.hashCode()
                _responseUpGDocStatus.value = GeneralResponseStatus.ERROR
            }

        }

        return false
    }

    fun updateCustomerBasicDetails(
        id_number: RequestBody,
        first_name: RequestBody,
        last_name: RequestBody,
        phone: RequestBody,
        email: RequestBody,
        dob: RequestBody,
        gender_id: RequestBody,
        spouse_name: RequestBody,
        spouse_phone: RequestBody,
        file: MultipartBody.Part
    ) {
        uiScope.launch {
            _responseStatus.value = GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.updateBasicInfoAsync(
                id_number,
                first_name,
                last_name,
                phone,
                email,
                dob,
                gender_id,
                spouse_name,
                spouse_phone,
                file
            )
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    getCustomerFullDetails()
                    _responseStatus.value = GeneralResponseStatus.DONE
                    _status.value = 1
                } else {
                    _responseStatus.value = GeneralResponseStatus.ERROR
                    _statusMessage.value = accResponse.message
                    _status.value = 0
                }

            } catch (e: Throwable) {
                _responseStatus.value = GeneralResponseStatus.ERROR
                _status.value = e.hashCode()
            }
        }

    }

    fun updateAdditionalDetails(updateAdditionalInfoDTO: UpdateAdditionalInfoDTO) {
        uiScope.launch {
            val request =
                FieldAgentApi.retrofitService.updateAdditionalnfoAsync(updateAdditionalInfoDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    getCustomerFullDetails()
                    _statusACode.value = 1
                } else {
                    _statusMessage.value = accResponse.message
                    _statusACode.value = 0
                }

            } catch (e: Throwable) {
                _statusACode.value = e.hashCode()
            }
        }

    }

    fun updateBusinessDetails(updateBusinessDetailsDTO: UpdateBusinessDetailsDTO) {
        uiScope.launch {
            val request =
                FieldAgentApi.retrofitService.updateBusinessDetailsAsync(updateBusinessDetailsDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    getCustomerFullDetails()
                    _statusBDCode.value = 1
                } else {
                    _statusMessage.value = accResponse.message
                    _statusBDCode.value = 0
                }

            } catch (e: Throwable) {
                _statusBDCode.value = e.hashCode()
            }
        }

    }

    fun updateBusinessAddress(updateBusinessAddressDTO: UpdateBusinessAddressDTO) {
        uiScope.launch {
            val request =
                FieldAgentApi.retrofitService.updateBusinessAddressAsync(updateBusinessAddressDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    getCustomerFullDetails()
                    _statusBACode.value = 1
                } else {
                    _statusMessage.value = accResponse.message
                    _statusBACode.value = 0
                }

            } catch (e: Throwable) {
                _statusBACode.value = e.hashCode()
            }
        }

    }

    fun updateGuarantorDetails(updateGuarantorsDTO: UpdateGuarantorsDTO) {
        uiScope.launch {
            _responseUpGStatus.value = GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.updateGuarantorAsync(updateGuarantorsDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    _responseUpGStatus.value = GeneralResponseStatus.DONE
                    getCustomerFullDetails()
                    _statusGCode.value = 1
                } else {
                    _responseUpGStatus.value = GeneralResponseStatus.ERROR
                    _statusMessage.value = accResponse.message
                    _statusGCode.value = 0
                }

            } catch (e: Throwable) {
                _responseUpGStatus.value = GeneralResponseStatus.ERROR
                _statusGCode.value = e.hashCode()
            }
        }

    }

    fun updateResidentialDetails(updateResidentialInfoDTO: UpdateResidentialInfoDTO) {
        uiScope.launch {
            val request =
                FieldAgentApi.retrofitService.updateResidentialnfoAsync(updateResidentialInfoDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    getCustomerFullDetails()
                    _statusRCode.value = 1
                } else {
                    _statusMessage.value = accResponse.message
                    _statusRCode.value = 0
                }

            } catch (e: Throwable) {
                _statusRCode.value = e.hashCode()
            }
        }

    }

    fun addCollateralDetails(addCollateralDTO: AddCollateralDTO) {
        uiScope.launch {
            _responseAddCoStatus.value = GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.addCollateralAsync(addCollateralDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    _responseAddCoStatus.value = GeneralResponseStatus.DONE
                    getCustomerFullDetails()
                    _status.value = 1
                } else {
                    _responseAddCoStatus.value = GeneralResponseStatus.ERROR
                    _statusMessage.value = accResponse.message
                    _status.value = 0
                }

            } catch (e: Throwable) {
                _responseAddCoStatus.value = GeneralResponseStatus.ERROR
                _status.value = e.hashCode()
            }
        }

    }

    fun updateCollateralDetails(updateCollateralDTO: UpdateCollateralDTO) {
        uiScope.launch {
            _responseUpCoStatus.value = GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.updateCollateralAsync(updateCollateralDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    _responseUpCoStatus.value = GeneralResponseStatus.DONE
                    getCustomerFullDetails()
                    _statusCoCode.value = 1
                } else {
                    _responseUpCoStatus.value = GeneralResponseStatus.ERROR
                    _statusMessage.value = accResponse.message
                    _statusCoCode.value = 0
                }

            } catch (e: Throwable) {
                _responseUpCoStatus.value = GeneralResponseStatus.ERROR
                _statusCoCode.value = e.hashCode()
            }
        }
    }

    fun deleteCollateralDetails(deleteCollateralDTO: DeleteCollateralDTO) {
        uiScope.launch {
            _responseDelCoStatus.value = GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.deleteCollateralAsync(deleteCollateralDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    _responseDelCoStatus.value = GeneralResponseStatus.DONE
                    getCustomerFullDetails()
                    _statusRGCode.value = 1
                } else {
                    _responseDelCoStatus.value = GeneralResponseStatus.ERROR
                    _statusMessage.value = accResponse.message
                    _statusRGCode.value = 0
                }

            } catch (e: Throwable) {
                _responseDelCoStatus.value = GeneralResponseStatus.ERROR
                _statusRGCode.value = e.hashCode()
            }
        }

    }

    fun addBorrowingDetails(addBorrowingDTO: AddBorrowingDTO) {
        uiScope.launch {
            _responseAddGStatus.value = GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.addBorrowingAsync(addBorrowingDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    _responseAddGStatus.value = GeneralResponseStatus.DONE
                    getCustomerFullDetails()
                    _status.value = 1
                } else {
                    _responseAddGStatus.value = GeneralResponseStatus.ERROR
                    _statusMessage.value = accResponse.message
                    _status.value = 0
                }

            } catch (e: Throwable) {
                _responseAddGStatus.value = GeneralResponseStatus.ERROR
                _status.value = e.hashCode()
            }
        }

    }

    fun updateBorrowingDetails(updateBorrowingsDTO: UpdateBorrowingsDTO) {
        uiScope.launch {
            _responseUpGStatus.value = GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.updateBorrowingAsync(updateBorrowingsDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    _responseUpGStatus.value = GeneralResponseStatus.DONE
                    getCustomerFullDetails()
                    _statusBCode.value = 1
                } else {
                    _responseUpGStatus.value = GeneralResponseStatus.ERROR
                    _statusMessage.value = accResponse.message
                    _statusBCode.value = 0
                }

            } catch (e: Throwable) {
                _responseUpGStatus.value = GeneralResponseStatus.ERROR
                _statusBCode.value = e.hashCode()
            }
        }

    }

    fun deleteBorrowingDetails(deleteBorrowingDTO: DeleteBorrowingDTO) {
        uiScope.launch {
            _responseRemGStatus.value = GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.deleteBorrowingAsync(deleteBorrowingDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    _responseRemGStatus.value = GeneralResponseStatus.DONE
                    getCustomerFullDetails()
                    _statusRGCode.value = 1

                } else {
                    _responseRemGStatus.value = GeneralResponseStatus.ERROR
                    _statusMessage.value = accResponse.message
                    _statusRGCode.value = 0
                }

            } catch (e: Throwable) {
                _responseRemGStatus.value = GeneralResponseStatus.ERROR
                _statusRGCode.value = e.hashCode()
            }
        }

    }

    fun updateNOKDetails(updateNokDTO: UpdateNokDTO) {
        uiScope.launch {
            val request = FieldAgentApi.retrofitService.updateNOkInfoAsync(updateNokDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    getCustomerFullDetails()
                    _statusNokCode.value = 1
                } else {
                    _statusMessage.value = accResponse.message
                    _statusNokCode.value = 0
                }

            } catch (e: Throwable) {
                _statusNokCode.value = e.hashCode()
            }
        }

    }

    fun deleteGuarantorDetails(removeGuarantorDTO: RemoveGuarantorDTO) {
        uiScope.launch {
            _responseRemGStatus.value = GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.removeGuarantorAsync(removeGuarantorDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    _responseRemGStatus.value = GeneralResponseStatus.DONE
                    getCustomerFullDetails()
                    _statusRGCode.value = 1
                } else {
                    _responseRemGStatus.value = GeneralResponseStatus.ERROR
                    _statusMessage.value = accResponse.message
                    _statusRGCode.value = 0
                }

            } catch (e: Throwable) {
                _responseRemGStatus.value = GeneralResponseStatus.ERROR
                _statusRGCode.value = e.hashCode()
            }
        }

    }

    fun addGuarantorDetails(addGuarantorsDTO: AddGuarantorsDTO) {
        uiScope.launch {
            _responseAddGStatus.value = GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.addGuarantorAsync(addGuarantorsDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    _responseAddGStatus.value = GeneralResponseStatus.DONE
                    getCustomerFullDetails()
                    _statusAGCode.value = 1
                } else {
                    _responseAddGStatus.value = GeneralResponseStatus.ERROR
                    _statusMessage.value = accResponse.message
                    _statusAGCode.value = 0
                }

            } catch (e: Throwable) {
                _responseAddGStatus.value = GeneralResponseStatus.ERROR
                _statusAGCode.value = e.hashCode()
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}