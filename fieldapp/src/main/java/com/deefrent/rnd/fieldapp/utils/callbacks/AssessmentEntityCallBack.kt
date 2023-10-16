package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.room.entities.AssessCustomerEntityWithList

interface AssessmentEntityCallBack {
    fun onItemSelected(pos:Int,listItems: AssessCustomerEntityWithList)
}