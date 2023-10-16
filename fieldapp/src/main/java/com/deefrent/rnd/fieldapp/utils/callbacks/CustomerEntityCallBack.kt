package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.room.entities.CusomerDetailsEntityWithList

interface CustomerEntityCallBack {
    fun onItemSelected(pos:Int,items: CusomerDetailsEntityWithList)
}