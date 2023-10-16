package com.deefrent.rnd.fieldapp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deefrent.rnd.fieldapp.repositories.AssetsRepo
import com.deefrent.rnd.fieldapp.responses.SubmitAssetResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AssetsViewModel : ViewModel() {
    private var assetsRepo: AssetsRepo = AssetsRepo()
    fun submitAsset(
        assetDetails: RequestBody,
        assetFiles: MultipartBody.Part
    ): MutableLiveData<SubmitAssetResponse?> {
        return assetsRepo.submitAsset(assetDetails,assetFiles)
    }
}