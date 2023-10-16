package com.deefrent.rnd.fieldapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.deefrent.rnd.fieldapp.models.dsrProfile.GetDsrProfileResponse
import com.deefrent.rnd.fieldapp.repositories.DsrProfileRepo

class DsrProfileViewModel: ViewModel() {
    private var dsrProfileRepo:DsrProfileRepo = DsrProfileRepo()
    fun getDsrProfile(): LiveData<GetDsrProfileResponse> {
        return dsrProfileRepo.getDsrProfile()
    }
}