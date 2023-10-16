package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.network.models.DocumentData

interface DocCallBack {
    fun onItemSelected(pos:Int,lists:DocumentData )
}