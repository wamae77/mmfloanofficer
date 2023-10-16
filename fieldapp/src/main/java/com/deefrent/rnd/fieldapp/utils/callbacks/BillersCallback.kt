package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.network.models.Biller

interface BillersCallback {
    fun onItemSelected(biller: Biller, pos:Int)
}