package com.deefrent.rnd.fieldapp.dtos


import com.google.gson.annotations.SerializedName

data class AssessCustomerDTO(
    val area_id: String ,
    @SerializedName("also_known_as")
    val alsoKnownAs: String, // Licia
    @SerializedName("assessmentRemarks")
    val assessmentRemarks: String, // Testing full assessment
    @SerializedName("bs_district_id")
    val bsDistrictId: String, // 1
    @SerializedName("bs_economic_factor_id")
    val bsEconomicFactorId: String, // 1
    @SerializedName("bs_establishment_type_id")
    val bsEstablishmentTypeId: String, // 1
    @SerializedName("bs_name_of_industry")
    val bsNameOfIndustry: String, // ICT
    @SerializedName("bs_number_of_employees")
    val bsNumberOfEmployees: String, // 10
    @SerializedName("bs_phone_number")
    val bsPhoneNumber: String, // 0725786709
    @SerializedName("bs_physical_address")
    val bsPhysicalAddress: String, // Silanga
    @SerializedName("bs_type_of_business_id")
    val bsTypeOfBusinessId: String, // 1
    @SerializedName("bs_village_id")
    val bsVillageId: String,
    @SerializedName("bs_years_in_business")
    val bsYearsInBusiness: String, // 15
    @SerializedName("collaterals")
    val collaterals: List<Collateral>,
    @SerializedName("customer_number")
    val customer_number: String, // 57890986
    @SerializedName("dob")
    val dob: String, // 12-12-1986
    @SerializedName("education_level_id")
    val educationLevelId: String, // 85
    @SerializedName("email")
    val email: String, // lovegalucia2@gmail.com
    @SerializedName("employment_status_id")
    val employmentStatusId: String, // 1
    @SerializedName("expenseFood")
    val expenseFood: String, // 300 // 800.0000
    @SerializedName("expenseMedicalAidOrContributions")
    val expenseMedicalAidOrContributions: String, // 100
    @SerializedName("expenseRentals")
    val expenseRentals: String, // 1000
    @SerializedName("expenseSchoolFees")
    val expenseSchoolFees: String,
    @SerializedName("expenseTransport")
    val expenseTransport: String, // 987
    @SerializedName("first_name")
    val firstName: String, // Lucy
    @SerializedName("gender_id")
    val genderId: String, // 1
    @SerializedName("guarantors")
    val guarantors: List<Guarantor>,
    @SerializedName("householdMembers")
    val householdMembers: List<HouseholdMember>,
    @SerializedName("how_client_knew_mmf_id")
    val howClientKnewMmfId: String, // 1
    @SerializedName("incomeNetSalary")
    val incomeNetSalary: String,
    @SerializedName("incomeOwnSalary")
    val incomeOwnSalary: String, // 1000
    @SerializedName("incomeProfit")
    val incomeProfit: String, // 1000
    @SerializedName("incomeRemittanceOrDonation")
    val incomeRemittanceOrDonation: String, // 900
    @SerializedName("incomeRental")
    val incomeRental: String,
    @SerializedName("incomeTotalSales")
    val incomeTotalSales: String, // 1000
    @SerializedName("kin_first_name")
    val kinFirstName: String, // Josiah
    @SerializedName("kin_id_number")
    val kinIdNumber: String, // 98908767
    @SerializedName("kin_identity_type_id")
    val kinIdentityTypeId: String, // 1
    @SerializedName("kin_last_name")
    val kinLastName: String, // Omondi
    @SerializedName("kin_phone_number")
    val kinPhoneNumber: String, // 0735678967
    @SerializedName("kin_relationship_id")
    val kinRelationshipId: String, // 143
    @SerializedName("last_name")
    val lastName: String, // Lovega2
    @SerializedName("national_identity")
    val nationalIdentity: String, // 57890986
    @SerializedName("number_of_children")
    val numberOfChildren: String, // 3
    @SerializedName("number_of_dependants")
    val numberOfDependants: String, // 4
    @SerializedName("otherExpenses")
    val otherExpenses: String, // 400
    @SerializedName("otherIncomes")
    val otherIncomes: String, // 987
    @SerializedName("phone")
    val phone: String, // 071091222
    @SerializedName("res_accommodation_status_id")
    val resAccommodationStatusId: String, // 2
    @SerializedName("res_living_since")
    val resLivingSince: String, // 2022-01-12
    @SerializedName("res_physical_address")
    val resPhysicalAddress: String, // Box 1689,Harare
    @SerializedName("spouse_name")
    val spouseName: String, // Dan Juma
    @SerializedName("spouse_phone")
    val spousePhone: String ,// 0728616055
    @SerializedName("otherBorrowings")
    val otherBorrowings: List<OtherBorrowing>,
)
    data class Collateral(
        @SerializedName("assetTypeId")
        val assetTypeId: String, // 36
        @SerializedName("estimatedValue")
        val estimatedValue: String, // 60000
        @SerializedName("model")
        val model: String, // BMW 2020
        @SerializedName("name")
        val name: String, // BMW
        @SerializedName("serialNumber")
        val serialNumber: String,
        @SerializedName("channelGeneratedCode")
        val channelGeneratedCode: String,
    )

    data class Guarantor(
        @SerializedName("address")
        val address: String, // P.O Box St QuStringin
        @SerializedName("channelGeneratedCode")
        val channelGeneratedCode: String, // 6203
        @SerializedName("idNumber")
        val idNumber: String, // 278909871
        @SerializedName("name")
        val name: String, // Jayson
        @SerializedName("phone")
        val phone: String, // 0730567890
        @SerializedName("relationshipId")
        val relationshipId: String // 143
    )
    data class HouseholdMember(
        @SerializedName("fullName")
        val fullName: String, // Tomas Munyiri
        @SerializedName("incomeOrFeesPaid")
        val incomeOrFeesPaid: String,
        @SerializedName("natureOfActivity")
        val natureOfActivity: String, // Learning Coding
        @SerializedName("occupationId")
        val occupationId: String, // 1
        @SerializedName("relationshipId")
        val relationshipId: String // 143
    )
    data class OtherBorrowing(
    @SerializedName("institution_name")
    val institutionName: String,
    @SerializedName("amount")
    val amount: String,
    @SerializedName("total_amount_paid_to_date")
    val totalAmountPaidToDate: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("monthly_installment_paid")
    val monthlyInstallmentPaid: String,
)


