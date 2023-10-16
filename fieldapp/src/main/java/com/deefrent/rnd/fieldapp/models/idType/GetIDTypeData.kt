package com.deefrent.rnd.fieldapp.models.idType

import androidx.annotation.Keep

@Keep
data class GetIDTypeData(
    val identificationTypeList: List<IdentificationType>
)