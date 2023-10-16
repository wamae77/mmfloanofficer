package com.deefrent.rnd.fieldapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.fieldapp.dtos.*
import com.deefrent.rnd.fieldapp.network.FieldAgentApi
import com.deefrent.rnd.fieldapp.network.models.*
import com.deefrent.rnd.fieldapp.responses.GetDocumentTypesResponse
import com.deefrent.rnd.fieldapp.responses.UploadDocumentResponse
import com.deefrent.rnd.fieldapp.room.database.FieldAppDatabase
import com.deefrent.rnd.fieldapp.room.repos.DropdownItemRepository
import com.deefrent.rnd.fieldapp.utils.resolveException
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception

class DocumentViewModel : ViewModel() {
    private var _status = MutableLiveData<Int?>()
    val status: MutableLiveData<Int?>
        get() = _status
    private var _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String>
        get() = _statusMessage
    private var _statusCode = MutableLiveData<Int?>()
    val statusCode: MutableLiveData<Int?>
        get() = _statusCode
    private var _responseStatus = MutableLiveData<GeneralResponseStatus>()
    val responseStatus: LiveData<GeneralResponseStatus>
        get() = _responseStatus

    /**guarantors details*/
    private var _docData = MutableLiveData<List<DocumentData>>()
    val docData: LiveData<List<DocumentData>>
        get() = _docData
    private val viewModelJob = Job()
    val repo: DropdownItemRepository
    private val uiScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        val genderDao =
            FieldAppDatabase.getFieldAppDatabase(BaseApp.applicationContext()).dropdownItemDao()
        repo = DropdownItemRepository(genderDao)
        _status.value = null
        _statusCode.value = null
    }

    fun stopObserving() {
        _status.value = null
        _statusCode.value = null

    }

    fun getCustomerDoc(docDTO: DocDTO) {
        uiScope.launch {
            _docData.value = arrayListOf()
            _responseStatus.value = GeneralResponseStatus.LOADING
            val request = FieldAgentApi.retrofitService.getDocumentAsync(docDTO)
            try {
                val accResponse = request.await()
                if (accResponse.status == 1) {
                    _docData.value = accResponse.data
                    if (accResponse.data.isNotEmpty()) {
                        _docData.value = accResponse.data
                    } else {
                        _docData.value = arrayListOf()
                    }
                    _statusCode.value = 1
                    _responseStatus.value = GeneralResponseStatus.DONE
                } else {
                    _statusMessage.value = accResponse.message
                    _statusCode.value = 0
                    _responseStatus.value = GeneralResponseStatus.ERROR
                }

            } catch (e: Throwable) {
                _responseStatus.value = GeneralResponseStatus.ERROR
                e.printStackTrace()
                _statusCode.value = e.hashCode()
            }
        }

    }

    fun getDocumentTypes(): LiveData<GetDocumentTypesResponse> {
        val myApiResponse = MutableLiveData<GetDocumentTypesResponse>()
        uiScope.launch {
            val getDocumentTypesResponse = FieldAgentApi.retrofitService.getDocumentTypesAsync()
            try {
                val response = getDocumentTypesResponse.await()
                myApiResponse.postValue(
                    GetDocumentTypesResponse(
                        response.data,
                        response.message,
                        response.status
                    )
                )
            } catch (e: Exception) {
                myApiResponse.postValue(
                    GetDocumentTypesResponse(
                        arrayListOf(),
                        resolveException(e),
                        hashCode()
                    )
                )
            }
        }
        return myApiResponse
    }

    suspend fun uploadCustomerDoc(
        customerId: RequestBody, docTypeCode: RequestBody,
        file: MultipartBody.Part
    ): LiveData<UploadDocumentResponse> {
        Log.d("TAG", "uploadCustomerDocs: $customerId")
        val myApiResponse = MutableLiveData<UploadDocumentResponse>()
        uiScope.launch {
            val uploadDocumentResponse = FieldAgentApi.retrofitService.uploadDocument2Async(
                customerId,
                docTypeCode,
                file
            )
            try {
                val response = uploadDocumentResponse.await()
                myApiResponse.postValue(
                    UploadDocumentResponse(
                        response.data,
                        response.message,
                        response.status
                    )
                )
            } catch (e: Exception) {
                myApiResponse.postValue(
                    UploadDocumentResponse(
                        null,
                        resolveException(e),
                        hashCode()
                    )
                )
            }
        }
        return myApiResponse
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}