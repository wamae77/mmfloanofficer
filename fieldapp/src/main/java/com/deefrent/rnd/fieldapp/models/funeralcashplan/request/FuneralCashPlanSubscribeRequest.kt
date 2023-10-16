package request


import com.google.gson.annotations.SerializedName

data class FuneralCashPlanSubscribeRequest(
    @SerializedName("packageId") var packageId: Int? = null,
    @SerializedName("dependants") var dependants: ArrayList<Dependant> = arrayListOf(),
    @SerializedName("paymentAmount") var paymentAmount: Int? = null,
    @SerializedName("walletAccountId") var walletAccountId: Int? = null,
    @SerializedName("customerIdNumber") var customerIdNumber: String? = null

//    @SerializedName("dependants")
//    val dependants: List<Dependant>,
//    @SerializedName("packageId")
//    val packageId: Int,
//    @SerializedName("paymentAmount")
//    val paymentAmount: Int,
//    @SerializedName("walletAccountId")
//    val walletAccountId: Int
)

data class Dependant(
    @SerializedName("dob")
    val dob: String,
    @SerializedName("idNumber")
    val idNumber: String,
    @SerializedName("isBeneficiary")
    val isBeneficiary: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("relationshipId")
    val relationshipId: Int,
    val contributionAmount: String = ""
) {
    companion object {
        fun getDependant() = listOf(
            Dependant(
                name = "Myself",
                relationshipId = 0,
                phone = "",
                idNumber = "",
                dob = "",
                isBeneficiary = 1,
                contributionAmount = "Amount: 2USD"
            ), Dependant(
                name = "Myself",
                relationshipId = 0,
                phone = "",
                idNumber = "",
                dob = "",
                isBeneficiary = 1,
                contributionAmount = "Amount: 2USD"
            ), Dependant(
                name = "Myself",
                relationshipId = 0,
                phone = "",
                idNumber = "",
                dob = "",
                isBeneficiary = 1,
                contributionAmount = "Amount: 2USD"
            ), Dependant(
                name = "Myself",
                relationshipId = 0,
                phone = "",
                idNumber = "",
                dob = "",
                isBeneficiary = 1,
                contributionAmount = "Amount: 2USD"
            )
        )
    }
}
