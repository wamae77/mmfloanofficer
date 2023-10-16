package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.network.models.DisbursableLoan

interface PendingDisburseCallBack {
    fun onItemSelected(items: DisbursableLoan)
}