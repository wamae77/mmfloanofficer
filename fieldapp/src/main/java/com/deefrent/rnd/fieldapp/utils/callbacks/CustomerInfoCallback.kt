package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.models.customer.CustomerInfo

interface CustomerInfoCallback {
    fun onItemSelected(pos:Int,customerInfo: CustomerInfo)
}