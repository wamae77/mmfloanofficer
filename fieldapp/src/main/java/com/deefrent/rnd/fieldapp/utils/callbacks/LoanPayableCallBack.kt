package com.deefrent.rnd.fieldapp.utils.callbacks

import com.deefrent.rnd.fieldapp.network.models.RepayableLoan

interface LoanPayableCallBack {
    fun onItemSelected(loanProduct: RepayableLoan)
}