package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.room.entities.AssessBorrowing

interface AssessBorrowingCallBack {
    fun onItemSelected(pos:Int,lists: AssessBorrowing)
}