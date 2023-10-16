package com.deefrent.rnd.fieldapp.models.funeralcashplan

/**
 * Created by Tom Munyiri on 18/07/2022
 * Email: munyiri.thomas@electics.io
 * Company: Eclectics International Ltd
 */
data class PremiumsPaidData(
    val name: String,
    val relationshipId: String,
    val phone: String,
    val idNumber: String,
    val dob: String,
    val isBeneficiary: Int,
    val contributionAmount: String,
    val date: String,
    val time: String,
) {
    companion object {
        fun getPremiumsPaidData() = listOf(
            PremiumsPaidData(
                name = "Premium Paid",
                relationshipId = "",
                phone = "",
                idNumber = "",
                dob = "",
                isBeneficiary = 1,
                contributionAmount = "6 USD",
                date = "Jan 14, 2022",
                time = "10:32am",
            ), PremiumsPaidData(
                name = "Premium Paid",
                relationshipId = "",
                phone = "",
                idNumber = "",
                dob = "",
                isBeneficiary = 1,
                contributionAmount = "6 USD",
                date = "Jan 14, 2022",
                time = "10:32am",
            )
        )
    }
}
