package com.deefrent.rnd.fieldapp.dtos


import com.google.gson.annotations.SerializedName

 data class OnboardCustomerDTO(
     @SerializedName("area_id")
     val area_id: String,
//     @SerializedName("fingerprint_reg_id")
//     val fingerprint_reg_id: String,
     @SerializedName("is_completion")
     val isCompletion: String,
     @SerializedName("bs_district_id")
     val bsDistrictId: String,
     @SerializedName("also_known_as")
     val also_known_as: String,
     @SerializedName("customer_number")
     val customer_number: String,
     @SerializedName("bs_economic_factor_id")
     val bsEconomicFactorId: String,
     @SerializedName("bs_establishment_type_id")
     val bsEstablishmentTypeId: String,
     @SerializedName("bs_name_of_industry")
     val bsNameOfIndustry: String,
     @SerializedName("bs_number_of_employees")
     val bsNumberOfEmployees: String,
     @SerializedName("bs_phone_number")
     val bsPhoneNumber: String,
     @SerializedName("bs_physical_address")
     val bsPhysicalAddress: String,
     @SerializedName("bs_type_of_business_id")
     val bsTypeOfBusinessId: String,
     @SerializedName("bs_village_id")
     val bsVillageId: String,
     @SerializedName("bs_years_in_business")
     val bsYearsInBusiness: String,
     @SerializedName("collaterals")
     val collaterals: List<Collateral>,
     @SerializedName("dob")
     val dob: String,
     @SerializedName("education_level_id")
     val educationLevelId: String,
     @SerializedName("email")
     val email: String,
     @SerializedName("employment_status_id")
     val employmentStatusId: String,
     @SerializedName("first_name")
     val firstName: String,
     @SerializedName("gender_id")
     val genderId: String,
     @SerializedName("guarantors")
     val guarantors: List<Guarantor>,
     @SerializedName("how_client_knew_mmf_id")
     val howClientKnewMmfId: String,
     @SerializedName("kin_first_name")
     val kinFirstName: String,
     @SerializedName("kin_id_number")
     val kinIdNumber: String,
     @SerializedName("kin_identity_type_id")
     val kinIdentityTypeId: String,
     @SerializedName("kin_last_name")
     val kinLastName: String,
     @SerializedName("kin_phone_number")
     val kinPhoneNumber: String,
     @SerializedName("kin_relationship_id")
     val kinRelationshipId: String,
     @SerializedName("last_name")
     val lastName: String,
     @SerializedName("national_identity")
     val nationalIdentity: String,
     @SerializedName("number_of_children")
     val numberOfChildren: String,
     @SerializedName("number_of_dependants")
     val numberOfDependants: String,
     @SerializedName("otherBorrowings")
     val otherBorrowings: List<OtherBorrowing>,
     @SerializedName("phone")
     val phone: String,
     @SerializedName("res_accommodation_status_id")
     val resAccommodationStatusId: String,
     @SerializedName("res_living_since")
     val resLivingSince: String,
     @SerializedName("res_physical_address")
     val resPhysicalAddress: String,
     @SerializedName("spouse_name")
     val spouseName: String,
     @SerializedName("spouse_phone")
     val spousePhone: String,
     /**addional*/
     /**addional*/
     @SerializedName("expenseFood")
     val expenseFood: String,
     @SerializedName("expenseMedicalAidOrContributions")
     val expenseMedicalAidOrContributions: String,
     @SerializedName("expenseRentals")
     val expenseRentals: String,
     @SerializedName("expenseSchoolFees")
     val expenseSchoolFees: String,
     @SerializedName("expenseTransport")
     val expenseTransport: String,
     @SerializedName("otherExpenses")
     val otherExpenses: String,
     /**incomes*/
     @SerializedName("incomeNetSalary")
     val incomeNetSalary: String,
     @SerializedName("incomeOwnSalary")
     val incomeGrossSalary: String,
     @SerializedName("incomeTotalSales")
     val incomeTotalSale: String,
     @SerializedName("incomeProfit")
     val incomeProfit: String,
     @SerializedName("incomeRental")
     val incomeRental: String,
     @SerializedName("incomeRemittanceOrDonation")
     val incomeRemittanceOrDonation: String,
     @SerializedName("otherIncomes")
     val otherIncomes: String,
     @SerializedName("householdMembers")
     val householdMembers: List<HouseholdMember>,
) {
    data class Collateral(
        @SerializedName("asset_type_id")
        val assetTypeId: Int,
        @SerializedName("estimate_value")
        val estimateValue: String,
        @SerializedName("model")
        val model: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("serial_number")
        val serialNumber: String,
        @SerializedName("channelGeneratedCode")
        val channelGeneratedCode: String
    )

    data class Guarantor(
        @SerializedName("id_number")
        val idNumber: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("phone")
        val phone: String,
        @SerializedName("relationship_id")
        val relationshipId: Int,
        @SerializedName("residence_address")
        val residenceAddress: String,
        @SerializedName("channelGeneratedCode")
        val channelGeneratedCode: String
    )

    data class OtherBorrowing(
        @SerializedName("institution_name")
        val institutionName: String,
        @SerializedName("amount")
        val amount: String,
        @SerializedName("total_amount_paid_to_date")
        val totalAmountPaidToDate: String,
        @SerializedName("status")
        val status: Int,
        @SerializedName("monthly_installment_paid")
        val monthlyInstallmentPaid: String,
    )
     data class HouseholdMember(
         @SerializedName("fullName")
         val fullName: String,
         @SerializedName("incomeOrFeesPaid")
         val incomeOrFeesPaid: String,
         @SerializedName("memberId")
         val memberId: String,
         @SerializedName("natureOfActivity")
         val natureOfActivity: String,
         @SerializedName("occupationId")
         val occupationId: String,
         @SerializedName("relationshipId")
         val relationshipId: String,
     )
}