package com.deefrent.rnd.fieldapp.responses

import androidx.annotation.Keep
import com.deefrent.rnd.fieldapp.models.customerDocuments.CustomerDocumentType

@Keep
data class GetDocumentTypesResponse(
    val `data`: List<CustomerDocumentType>?,
    val message: String,
    val status: Int
)