package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.network.models.CollateralInfo

interface CollateralCallBack {
    fun onItemSelected(pos:Int,items: CollateralInfo)
}