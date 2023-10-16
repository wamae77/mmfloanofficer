package com.deefrent.rnd.fieldapp.models.merchantAgentTypes

import androidx.annotation.Keep

@Keep
data class MerchantAgentTypeData(
    val merchant_agent: List<MerchantAgent>
)