package com.deefrent.rnd.fieldapp.models.funeralcashplan.responses

import com.deefrent.rnd.fieldapp.utils.capitalizeWords
import com.google.gson.annotations.SerializedName


data class SavingAccountsResponse(
    @SerializedName("status")
    val status: Int, // 200
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val `data`: ArrayList<SavingAccountData>
)

data class SavingAccountData(
    @SerializedName("accountId")
    val accountId: Int, // 355
    @SerializedName("accountName")
    val accountName: String, // ALPHA DEPOSIT
    @SerializedName("accountNo")
    val accountNo: String, // BS002021/02
    @SerializedName("allowCredit")
    val allowCredit: Int, // 0
    @SerializedName("allowDebit")
    val allowDebit: Int, // 0
    @SerializedName("isTransactional")
    val isTransactional: Int, // 0
    @SerializedName("productId")
    val productId: Int, // 34
    val currentBalance: String,
    val availableBalance: String
) {
    override fun toString(): String {
       // Timber.d("TAG $accountNo")
        val accNumber = accountNo.replace("(?<=.{2}).(?=.{2})".toRegex(), "*")
        return "${accountName.capitalizeWords} - $accNumber"
    }
}
