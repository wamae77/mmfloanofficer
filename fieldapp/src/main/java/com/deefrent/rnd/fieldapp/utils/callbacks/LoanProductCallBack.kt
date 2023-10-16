package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.network.models.LoanProduct

interface LoanProductCallBack {
    fun onItemSelected(loanProduct: LoanProduct)
}