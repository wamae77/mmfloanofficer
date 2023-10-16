package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.room.entities.OtherBorrowing

interface BorrowEntityCallBack {
    fun onItemSelected(pos:Int,lists:OtherBorrowing )
}