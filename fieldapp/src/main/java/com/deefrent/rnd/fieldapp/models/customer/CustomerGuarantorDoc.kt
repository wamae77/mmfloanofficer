package com.deefrent.rnd.fieldapp.models.customer

import androidx.annotation.Keep

@Keep
data class CustomerGuarantorDoc(
    val customerID: String,
    var docCode: String,
    var docPath: String,
    var channelGeneratedCode: String,
    var documentId: Int?
)
