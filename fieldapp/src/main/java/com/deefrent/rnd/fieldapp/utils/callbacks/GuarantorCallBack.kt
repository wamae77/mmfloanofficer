package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.network.models.GuarantorInfo

interface GuarantorCallBack {
    fun onItemSelected(pos:Int,items: GuarantorInfo)
}