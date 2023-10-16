package com.deefrent.rnd.fieldapp.models.customerDocuments

import androidx.annotation.Keep

@Keep
data class CustomerDocumentType(
    val code: String,
    val name: String
){
    override fun toString(): String {
        return name
    }
}