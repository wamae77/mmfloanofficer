package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.network.models.CustomerIncompleteData

interface IncompleteRegCallBack {
    fun onItemSelected(pos:Int,items:CustomerIncompleteData)
}