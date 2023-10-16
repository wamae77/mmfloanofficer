package com.deefrent.rnd.fieldapp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deefrent.rnd.fieldapp.repositories.ComplainsRepo
import com.deefrent.rnd.fieldapp.responses.CreateComplainResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ComplainsViewModel : ViewModel() {
    private var complainsRepo: ComplainsRepo = ComplainsRepo()
    fun createComplain(complainDetails: RequestBody, complainFiles: MultipartBody.Part):
            MutableLiveData<CreateComplainResponse?> {
        return complainsRepo.createComplain(complainDetails, complainFiles)
    }
}