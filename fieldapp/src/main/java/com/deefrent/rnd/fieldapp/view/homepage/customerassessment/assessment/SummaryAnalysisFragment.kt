package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.assessment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentSummaryAnalysisBinding
import com.deefrent.rnd.fieldapp.databinding.SummaryFailedDialogBinding
import com.deefrent.rnd.fieldapp.dtos.*
import com.deefrent.rnd.fieldapp.room.entities.AssessCustomerDocsEntity
import com.deefrent.rnd.fieldapp.room.entities.AssessCustomerEntity
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.pattern
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.viewModels.AssessmentDashboardViewModel
import com.google.gson.Gson
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*
import kotlin.collections.ArrayList

class SummaryAnalysisFragment : Fragment() {
    private var customerId = ""
    private lateinit var binding: FragmentSummaryAnalysisBinding
    private lateinit var cardBinding: SummaryFailedDialogBinding
    private lateinit var assessCustomerEntity: AssessCustomerEntity
    private var customerDocsList: ArrayList<AssessCustomerDocsEntity> = arrayListOf()
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(AssessmentDashboardViewModel::class.java)
    }

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSummaryAnalysisBinding.inflate(layoutInflater)
        viewmodel.stopObserving()
        viewmodel.parentId.observe(viewLifecycleOwner) { customerIDNumber ->
            getSavedItemsFromRoom(customerIDNumber)
        }
        binding.apply {
            binding.ivBack.setOnClickListener { v ->
                Navigation.findNavController(v)
                    .navigateUp()
            }
            btnSubmit.setOnClickListener {
                val remarks = etAssessment.text.toString()
                if (remarks.isEmpty()) {
                    toastyErrors("Add assessment remarks")
                } else if (!binding.cbAccount.isChecked) {
                    toastyErrors("Check to confirm the details provided are valid")
                } else {
                    assessmentRemarks = etAssessment.text.toString()
                    viewmodel.updateCustomerHasFinished(true, nationalIdentity)
                    viewmodel.updateCustomerLastStep(
                        etAssessment.text.toString(),
                        true,
                        nationalIdentity,
                        "SummaryAnalysisFragment"
                    )
                    val assessCustomerDTO = AssessCustomerDTO(
                        subBranchId,
                        alsoKnownAs,
                        assessmentRemarks,
                        bsDistrictId,
                        bsEconomicFactorId,
                        bsEstablishmentTypeId,
                        bsNameOfIndustry,
                        bsNumberOfEmployees,
                        bsPhoneNumber,
                        bsPhysicalAddress,
                        bsTypeOfBusinessId,
                        bsVillageId,
                        bsYearsInBusiness,
                        collaterals,
                        customerIdNumber,
                        dob,
                        educationLevelId,
                        email,
                        employmentStatusId,
                        expenseFood,
                        expenseMedicalAidOrContributions,
                        expenseRentals,
                        expenseSchoolFees,
                        expenseTransport,
                        firstName,
                        genderId,
                        guarantors,
                        householdMembers,
                        howClientKnewMmfId,
                        incomeNetSalary,
                        incomeOwnSalary,
                        incomeProfit,
                        incomeRemittanceOrDonation,
                        incomeRental,
                        incomeTotalSales,
                        kinFirstName,
                        kinIdNumber,
                        kinIdentityTypeId,
                        kinLastName,
                        kinPhoneNumber,
                        kinRelationshipId,
                        lastName,
                        nationalIdentity,
                        numberOfChildren,
                        numberOfDependants,
                        otherExpenses,
                        otherIncomes,
                        phone,
                        resAccommodationStatusId,
                        resLivingSince,
                        resPhysicalAddress,
                        spouseName,
                        spousePhone,
                        otherBorrowings
                    )
                    viewmodel.assessCustomer(assessCustomerDTO)
                }
            }
        }
        binding.btnCheckXDSScore.setOnClickListener {
            checkCreditScore()
        }
        return binding.root
    }

    private fun checkCreditScore() {
        val checkCreditScoreDTO = CheckCreditScoreDTO(customerIdNumber)
        viewmodel.checkCreditScore(checkCreditScoreDTO)
        viewmodel.creditScoreData.observe(viewLifecycleOwner) { checkCreditScoreData ->
            binding.tvXDSSore.text = checkCreditScoreData.data
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            ivHome.setOnClickListener {
                findNavController().navigate(R.id.dashboardFragment)
            }
            viewmodel.responseXStatus.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it) {
                        GeneralResponseStatus.LOADING -> {
                            binding.btnSubmit.isEnabled = false
                            binding.progressbar.mainPBar.makeVisible()
                            binding.progressbar.tvWait.text = "Uploading documents..."
                        }
                        GeneralResponseStatus.DONE -> {
                            binding.btnSubmit.isEnabled = true
                            binding.progressbar.mainPBar.makeGone()
                        }
                        else -> {
                            binding.btnSubmit.isEnabled = true
                            binding.progressbar.mainPBar.makeGone()
                        }
                    }
                }
            }
            viewmodel.statusCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            viewmodel.updateCustomerIsProcessed(true, nationalIdentity)
                            if (customerDocsList.isEmpty()) {
                                viewmodel.deleteAssessedCustomer(assessCustomerEntity)
                                findNavController().navigate(R.id.assessSuccessFragment)
                            } else {
                                initiateDocumentsUpload(customerDocsList)
                            }
                            viewmodel.stopObserving()
                        }
                        0 -> {
                            viewmodel.stopObserving()
                            onInfoDialog(viewmodel.statusMessage.value)
                        }
                        else -> {
                            viewmodel.stopObserving()
                            showErrorDialog()
                        }
                    }
                }
            }
            viewmodel.responseAssessStatus.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        GeneralResponseStatus.LOADING -> {
                            binding.btnSubmit.isEnabled = false
                            binding.progressbar.tvWait.text = "Please wait as we submit the data..."
                            binding.progressbar.mainPBar.makeVisible()
                        }
                        GeneralResponseStatus.DONE -> {
                            binding.btnSubmit.isEnabled = true
                            binding.progressbar.mainPBar.makeGone()
                        }
                        else -> {
                            binding.btnSubmit.isEnabled = true
                            binding.progressbar.mainPBar.makeGone()
                        }
                    }
                }
            }
            viewmodel.responseCreditScoreStatus.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        GeneralResponseStatus.LOADING -> {
                            binding.btnSubmit.isEnabled = false
                            binding.progressbar.tvWait.text = "Checking Credit Score..."
                            binding.progressbar.mainPBar.makeVisible()
                        }
                        GeneralResponseStatus.DONE -> {
                            binding.btnSubmit.isEnabled = true
                            binding.progressbar.mainPBar.makeGone()
                        }
                        else -> {
                            binding.btnSubmit.isEnabled = true
                            binding.progressbar.mainPBar.makeGone()
                        }
                    }
                }
            }
            viewmodel.statusDocCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            customerDocsList.forEach { uploadedDoc ->
                                deleteImageFromInternalStorage(
                                    requireContext(),
                                    uploadedDoc.docPath.capitalizeWords
                                )
                                viewmodel.deleteAssessedCustomer(assessCustomerEntity)
                            }
                            findNavController().navigate(R.id.assessSuccessFragment)
                            viewmodel.stopObserving()
                        }
                        0 -> {
                            viewmodel.stopObserving()
                            onInfoDialog(viewmodel.statusMessage.value)
                        }
                        else -> {
                            findNavController().navigate(R.id.dashboardFragment)
                            toastyInfos("Error Occurred while uploading the documents...\nKindly try again later...")
                            viewmodel.stopObserving()
                        }
                    }
                }
            }
        }
    }

    private fun initiateDocumentsUpload(customerDocsList: List<AssessCustomerDocsEntity>) {
        /**run within the lifecycle of the view, if its destoryed it stop uploading */
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            var count = 0

            /**incase of an error occured while uplading image to stop uploading*/
            val filteredList = customerDocsList.filter { assessCustomerDocsEntity ->
                assessCustomerDocsEntity.docPath.isNotEmpty() && !pattern.containsMatchIn(
                    assessCustomerDocsEntity.docPath
                )
            }
            if (filteredList.isNotEmpty()) {
                Log.d("TAG", "initiateDocumentsUploadfilteredList: ${Gson().toJson(filteredList)}")
                val lastIndex = filteredList.size.minus(1)
                while (count < filteredList.size) {
                    val customerDocsEntity = filteredList[count]
                    val contextWrapper = ContextWrapper(requireContext())
                    // return a directory in internal storage
                    val directory =
                        contextWrapper.getDir(Constants.IMAGES_DIR, Context.MODE_PRIVATE)
                    val location = "${directory.absolutePath}/${customerDocsEntity.docPath}"
                    val convertedFile = convertPathToFile(location)
                    //  if (convertedFile.exists()){
                    val compressedImages = Compressor.compress(requireContext(), convertedFile)
                    val file = MultipartBody.Part.createFormData(
                        "file",
                        compressedImages.name, RequestBody.create(
                            "multipart/form-data".toMediaTypeOrNull(),
                            compressedImages
                        )
                    )
                    val customerID = RequestBody.create(
                        MultipartBody.FORM,
                        customerId
                    )
                    val docTypeCode =
                        RequestBody.create(MultipartBody.FORM, customerDocsEntity.docCode)
                    val channelGeneratedCode =
                        RequestBody.create(MultipartBody.FORM, customerDocsEntity.docGeneratedUID)
                    Log.d("TAG", "initiateDocumentsUpload:${customerDocsEntity.docCode} ")
                    val success = viewmodel.uploadCustomerDocs(
                        customerID, docTypeCode,
                        channelGeneratedCode, file, count == lastIndex
                    )
                    if (!success) break
                    //  }

                    count++

                }
                com.deefrent.rnd.common.utils.Constants.deleteCacheImageFromInternalStorage(
                    requireContext(),
                    "compressor"
                )
            } else {
                customerDocsList.forEach { uploadedDoc ->
                    deleteImageFromInternalStorage(requireContext(), uploadedDoc.docPath)
                    viewmodel.deleteAssessedCustomer(assessCustomerEntity)
                }
                withContext(Dispatchers.Main) {
                    findNavController().navigate(R.id.assessSuccessFragment)
                    viewmodel.stopObserving()
                }
            }
        }
    }

    companion object {
        var subBranchId:String=""
        var alsoKnownAs: String = ""
        var assessmentRemarks: String = ""
        var bsDistrictId: String = "" // 1
        var bsEconomicFactorId: String = ""
        var bsEstablishmentTypeId: String = "" // 1
        var bsNameOfIndustry: String = ""
        var bsNumberOfEmployees: String = "" // 10
        var bsPhoneNumber: String = ""
        var bsPhysicalAddress: String = ""
        var bsTypeOfBusinessId: String = ""
        var bsVillageId: String = ""
        var bsYearsInBusiness: String = ""
        var collaterals: List<Collateral> = arrayListOf()
        var customerIdNumber: String = ""
        var dob: String = ""
        var educationLevelId: String = "" // 85
        var email: String = ""
        var employmentStatusId: String = "" // 1
        var expenseDomesticWorkersWages: String = "" // 490
        var expenseFood: String = "" // 300
        var expenseFuneralPolicy: String = "" // 800.0000
        var expenseMedicalAidOrContributions: String = "" // 100
        var expenseRentals: String = "" // 1000
        var expenseSchoolFees: String = "" // 900
        var expenseTithe: String = "" // 100
        var expenseTransport: String = "" // 987
        var firstName: String = "" // Lucy
        var genderId: String = "" // 1
        var guarantors: List<Guarantor> = arrayListOf()
        var householdMembers: List<HouseholdMember> = arrayListOf()
        var howClientKnewMmfId: String = "" // 1
        var incomeNetSalary: String = "" // 1000
        var incomeOtherBusinesses: String = "" // 800
        var incomeOwnSalary: String = "" // 1000
        var incomeProfit: String = "" // 1000
        var incomeRemittanceOrDonation: String = "" // 900
        var incomeRental: String = "" // 100
        var incomeRoscals: String = "" // 490
        var incomeTotalSales: String = "" // 1000
        var kinFirstName: String = "" // Josiah
        var kinIdNumber: String = "" // 98908767
        var kinIdentityTypeId: String = "" // 1
        var kinLastName: String = "" // Omondi
        var kinPhoneNumber: String = "" // 0735678967
        var kinRelationshipId: String = "" // 143
        var lastName: String = "" // Lovega2
        var nationalIdentity: String = "" // 57890986
        var numberOfChildren: String = "" // 3
        var numberOfDependants: String = "" // 4
        var otherExpenses: String = "" // 400
        var otherIncomes: String = "" // 987
        var phone: String = "" // 071091222
        var resAccommodationStatusId: String = "" // 2
        var resLivingSince: String = "" // 2022-01-12
        var resPhysicalAddress: String = "" // Box 1689,Harare
        var spouseName: String = "" // Dan Juma
        var spousePhone: String = ""// 0728616055
        var otherBorrowings: List<OtherBorrowing> = arrayListOf()

    }

    private fun getSavedItemsFromRoom(parentID: String) {
        viewmodel.fetchCustomerDetails(parentID).observe(viewLifecycleOwner) {
            if (it != null) {
                assessCustomerEntity = it.assessCustomerEntity
                customerDocsList.clear()
                customerDocsList.addAll(it.customerDocs)
                collaterals = it.assessCollateral.map { collateralInfo ->
                    Collateral(
                        collateralInfo.assetTypeId,
                        collateralInfo.estimateValue,
                        collateralInfo.model,
                        collateralInfo.name,
                        collateralInfo.serialNumber,
                        collateralInfo.collateralGeneratedUID
                    )
                }
                guarantors = it.assessGua.map { guarantorInfo ->
                    Guarantor(
                        guarantorInfo.residenceAddress, guarantorInfo.generatedUID,
                        guarantorInfo.idNumber,
                        guarantorInfo.name,
                        guarantorInfo.phone, guarantorInfo.relationship
                    )
                }
                otherBorrowings = it.assessBorrow.map { otherBorrowings ->
                    OtherBorrowing(
                        otherBorrowings.institutionName,
                        otherBorrowings.amount,
                        otherBorrowings.totalAmountPaidToDate,
                        otherBorrowings.statusId.toString(),
                        otherBorrowings.monthlyInstallmentPaid
                    )
                }
                householdMembers = it.householdMember.map { householdMembers ->
                    HouseholdMember(
                        householdMembers.fullName,
                        householdMembers.incomeOrFeesPaid,
                        householdMembers.natureOfActivity,
                        householdMembers.occupationId,
                        householdMembers.relationshipId
                    )
                }
                phone = it.assessCustomerEntity.phone
                customerId = it.assessCustomerEntity.customerId
                customerIdNumber = it.assessCustomerEntity.customerNumber
                firstName = (it.assessCustomerEntity.firstName)
                lastName = (it.assessCustomerEntity.lastName)
                nationalIdentity = (it.assessCustomerEntity.idNumber)
                alsoKnownAs = (it.assessCustomerEntity.alsoKnownAs)
                subBranchId = (it.assessCustomerEntity.subBranchId)
                dob = (it.assessCustomerEntity.dob)
                genderId = it.assessCustomerEntity.genderId.toString()
                email = (it.assessCustomerEntity.emailAddress)
                spousePhone = (it.assessCustomerEntity.spouseName)
                spousePhone = (it.assessCustomerEntity.spousePhone)
                bsTypeOfBusinessId = it.assessCustomerEntity.businessTypeId.toString()
                bsEconomicFactorId = it.assessCustomerEntity.economicFactorId.toString()
                bsEstablishmentTypeId = it.assessCustomerEntity.establishmentTypeId
                bsNameOfIndustry = (it.assessCustomerEntity.nameOfIndustry)
                bsYearsInBusiness = (it.assessCustomerEntity.yearsInBusiness)
                bsDistrictId = it.assessCustomerEntity.businessDistrictId
                bsVillageId = it.assessCustomerEntity.businessVillageId
                bsPhysicalAddress = (it.assessCustomerEntity.businessPhysicalAddress)
                bsPhoneNumber = (it.assessCustomerEntity.businessPhone)
                bsNumberOfEmployees = (it.assessCustomerEntity.numberOfEmployees)
                resPhysicalAddress = (it.assessCustomerEntity.resPhysicalAddress)
                resAccommodationStatusId = it.assessCustomerEntity.resAccomadationStatus
                resLivingSince = (it.assessCustomerEntity.resLivingSince)
                kinRelationshipId = it.assessCustomerEntity.kinRelationshipId
                kinIdentityTypeId = it.assessCustomerEntity.kinIdentityTypeId.toString()
                kinFirstName = (it.assessCustomerEntity.kinFirstName)
                kinLastName = (it.assessCustomerEntity.kinLastName)
                kinPhoneNumber = (it.assessCustomerEntity.kinPhoneNumber)
                kinIdNumber = (it.assessCustomerEntity.kinIdNumber)
                educationLevelId = it.assessCustomerEntity.educationLevelId
                howClientKnewMmfId = it.assessCustomerEntity.identifierId.toString()
                employmentStatusId = it.assessCustomerEntity.empStatusId.toString()
                numberOfChildren = (it.assessCustomerEntity.numberOfChildren.trim())
                numberOfDependants = (it.assessCustomerEntity.numberOfDependants.trim())
                if (it.assessCustomerEntity.netSalary.isNotEmpty()) {
                    incomeNetSalary =
                        (FormatDigit.roundTo(it.assessCustomerEntity.netSalary.toDouble()))
                }
                if (it.assessCustomerEntity.grossSalary.isNotEmpty()) {
                    incomeOwnSalary =
                        (FormatDigit.roundTo(it.assessCustomerEntity.grossSalary.toDouble()))
                }
                if (it.assessCustomerEntity.totalSales.isNotEmpty()) {
                    incomeTotalSales =
                        (FormatDigit.roundTo(it.assessCustomerEntity.totalSales.toDouble()))
                }
                if (it.assessCustomerEntity.profit.isNotEmpty()) {
                    incomeProfit = (FormatDigit.roundTo(it.assessCustomerEntity.profit.toDouble()))
                }
                if (it.assessCustomerEntity.rentalIncome.isNotEmpty()) {
                    incomeRental =
                        (FormatDigit.roundTo(it.assessCustomerEntity.rentalIncome.toDouble()))
                }
                if (it.assessCustomerEntity.donation.isNotEmpty()) {
                    incomeRemittanceOrDonation =
                        (FormatDigit.roundTo(it.assessCustomerEntity.donation.toDouble()))
                }
                if (it.assessCustomerEntity.otherIncome.isNotEmpty()) {
                    otherIncomes =
                        (FormatDigit.roundTo(it.assessCustomerEntity.otherIncome.toDouble()))
                }
                if (it.assessCustomerEntity.expenseRentals.isNotEmpty()) {
                    expenseRentals =
                        (FormatDigit.roundTo(it.assessCustomerEntity.expenseRentals.toDouble()))
                }
                if (it.assessCustomerEntity.expenseTransport.isNotEmpty()) {
                    expenseTransport =
                        (FormatDigit.roundTo(it.assessCustomerEntity.expenseTransport.toDouble()))
                }
                if (it.assessCustomerEntity.expenseSchoolFees.isNotEmpty()) {
                    expenseSchoolFees =
                        (FormatDigit.roundTo(it.assessCustomerEntity.expenseSchoolFees.toDouble()))
                }
                if (it.assessCustomerEntity.expenseFood.isNotEmpty()) {
                    expenseFood =
                        (FormatDigit.roundTo(it.assessCustomerEntity.expenseFood.toDouble()))
                }
                if (it.assessCustomerEntity.expenseMedicalAidOrContributions.isNotEmpty()) {
                    expenseMedicalAidOrContributions =
                        (FormatDigit.roundTo(it.assessCustomerEntity.expenseMedicalAidOrContributions.toDouble()))
                }
                if (it.assessCustomerEntity.otherExpenses.isNotEmpty()) {
                    otherExpenses =
                        (FormatDigit.roundTo(it.assessCustomerEntity.otherExpenses.toDouble()))
                }
                val idNumber =
                    it?.assessCustomerEntity?.idNumber?.replace("(?<=.{2}).(?=.{3})".toRegex(), "*")
                val customerName =
                    "${it?.assessCustomerEntity?.firstName} ${it?.assessCustomerEntity?.lastName}"
                binding.tvAccName.text = String.format(
                    getString(R.string.acc), "$customerName -" +
                            "\n$idNumber"
                )
                // nationalIdentity=it.assessCustomerEntity.idNumber
                if (it.assessCustomerEntity.spousePhone.isNotEmpty() && it.assessCustomerEntity.spousePhone.isNotEmpty()) {
                    binding.cbMaritalStatus.text = "Married"
                } else {
                    binding.cbMaritalStatus.text = "Not Married"
                }
                val custAge = formatYear(it.assessCustomerEntity.dob).toInt()
                Log.d("TAG", "custAge: $custAge")
                val age = Calendar.getInstance().get(Calendar.YEAR)
                Log.d("TAG", "custAge: $age")
                val finalAge = age - custAge
                binding.cbProofOfIncome.text =
                    "Income\n${it.assessCustomerEntity.totalIncome.toString()}"
                binding.cbEducationLevel.text =
                    "Education Level\n${it.assessCustomerEntity.educationLevel}"
                binding.cbBusinessAge.text =
                    "Age of Business\n${it.assessCustomerEntity.yearsInBusiness}"
                binding.cbCustomerAge.text = "Customer Age\n$finalAge years"
                binding.cbProofOfResidence.text =
                    "Proof of Residence\nLiving Since ${it.assessCustomerEntity.resLivingSince}"
            }
        }
    }

    private fun showErrorDialog() {
        val dialog = Dialog(requireContext())
        cardBinding = SummaryFailedDialogBinding.inflate(LayoutInflater.from(context))
        cardBinding.ivCancel.setOnClickListener {
            dialog.dismiss()
        }
        cardBinding.btnNotNow.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(R.id.dashboardFragment)
        }
        cardBinding.btnApplyLoan.setOnClickListener {
            dialog.dismiss()
            val assessCustomerDTO = AssessCustomerDTO(
                subBranchId,
                alsoKnownAs,
                assessmentRemarks,
                bsDistrictId,
                bsEconomicFactorId,
                bsEstablishmentTypeId,
                bsNameOfIndustry,
                bsNumberOfEmployees,
                bsPhoneNumber,
                bsPhysicalAddress,
                bsTypeOfBusinessId,
                bsVillageId,
                bsYearsInBusiness,
                collaterals,
                customerIdNumber,
                dob,
                educationLevelId,
                email,
                employmentStatusId,
                expenseFood,
                expenseMedicalAidOrContributions,
                expenseRentals,
                expenseSchoolFees,
                expenseTransport,
                firstName,
                genderId,
                guarantors,
                householdMembers,
                howClientKnewMmfId,
                incomeNetSalary,
                incomeOwnSalary,
                incomeProfit,
                incomeRemittanceOrDonation,
                incomeRental,
                incomeTotalSales,
                kinFirstName,
                kinIdNumber,
                kinIdentityTypeId,
                kinLastName,
                kinPhoneNumber,
                kinRelationshipId,
                lastName,
                nationalIdentity,
                numberOfChildren,
                numberOfDependants,
                otherExpenses,
                otherIncomes,
                phone,
                resAccommodationStatusId,
                resLivingSince,
                resPhysicalAddress,
                spouseName,
                spousePhone,
                otherBorrowings
            )
            viewmodel.assessCustomer(assessCustomerDTO)
        }

        dialog.setContentView(cardBinding.root)
        dialog.show()
        dialog.setCancelable(false)
    }


}