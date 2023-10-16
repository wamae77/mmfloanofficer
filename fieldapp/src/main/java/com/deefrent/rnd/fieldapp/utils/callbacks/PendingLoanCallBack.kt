package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.network.models.LoansPendingApproval

interface PendingLoanCallBack {
    fun onItemSelected(pos:Int,lists: LoansPendingApproval)
}