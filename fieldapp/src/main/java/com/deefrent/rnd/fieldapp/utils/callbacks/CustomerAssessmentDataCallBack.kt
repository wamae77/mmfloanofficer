package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.network.models.CustomerAssessmentData

interface CustomerAssessmentDataCallBack {
    fun onItemSelected(pos:Int,items: CustomerAssessmentData)
}