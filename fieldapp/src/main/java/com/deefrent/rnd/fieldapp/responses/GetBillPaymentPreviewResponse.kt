package com.deefrent.rnd.fieldapp.responses

import androidx.annotation.Keep
import com.deefrent.rnd.fieldapp.models.billers.BillPaymentPreviewData

@Keep
data class GetBillPaymentPreviewResponse(
    val `data`: BillPaymentPreviewData,
    val message: String,
    val status: Int
)