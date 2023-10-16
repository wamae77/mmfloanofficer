package com.deefrent.rnd.jiboostfieldapp.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.deefrent.rnd.common.repo.SampleRepository
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val sampleRepository: SampleRepository,
    private val app: Application
) : AndroidViewModel(app) {

    fun getData(): String {
        return sampleRepository.data
    }
}