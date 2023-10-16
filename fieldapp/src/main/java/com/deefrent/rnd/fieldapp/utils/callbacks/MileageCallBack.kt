package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.models.mileage.Mileage

interface MileageCallBack {
    fun onItemSelected(pos:Int,lists: Mileage)
}