package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.network.models.OtherBorrowing

interface BorrowingCallBack {
    fun onItemSelected(pos:Int,items: OtherBorrowing)
}