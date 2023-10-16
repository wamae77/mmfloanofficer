package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.network.models.LoanLookupData

interface LoanLookUpDataCallBack {
    fun onItemSelected(pos:Int,loanLookupData: LoanLookupData)
}