package com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer

import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.deefrent.rnd.common.abstractions.BaseDaggerFragment
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.data.CustomAction
import com.deefrent.rnd.fieldapp.data.CustomData
import com.deefrent.rnd.fieldapp.data.CustomerSummaryAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentSummaryBinding
import com.deefrent.rnd.fieldapp.databinding.SummaryFailedDialogBinding
import com.deefrent.rnd.fieldapp.dtos.OnboardCustomerDTO
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.fromSummary
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.pattern
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer.SummaryFragmentDirections
import com.google.gson.Gson
import id.zelory.compressor.Compressor
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class SummaryFragment : BaseDaggerFragment() {
    private lateinit var binding: FragmentSummaryBinding
    private lateinit var customerSummaryAdapter: CustomerSummaryAdapter
    private lateinit var customerDetailEntity: CustomerDetailsEntity
    private lateinit var customerDocsList: List<CustomerDocsEntity>
    private lateinit var cardBinding: SummaryFailedDialogBinding
    private var customerId = ""

    //private lateinit var documentName: String
    // private var uploadedDocsNames: ArrayList<String> = arrayListOf()
    val items: ArrayList<CustomData> = arrayListOf()
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(OnboardCustomerViewModel::class.java)
    }

    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences

//    @Inject
//    lateinit var viewModel: FingerPrintViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSummaryBinding.inflate(layoutInflater)
        viewmodel.stopObserving()
        initializeUI()
        loadSlider()
        binding.ivHome.setOnClickListener {
            findNavController().navigate(R.id.dashboardFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customerSummaryAdapter = CustomerSummaryAdapter(requireContext(), items)
        binding.vppager.adapter = customerSummaryAdapter
        Log.i("TAG", " customerSummaryAdapter.notifyDataSetChanged(): ")
        customerSummaryAdapter.setOnItemClickListener {
            when (it) {
                CustomAction.CustomerDetails -> {
                    fromSummary = 8
                    val directions =
                        SummaryFragmentDirections.actionSummaryFragmentToOnboardCustomerDetailsFragment(
                            1
                        )
                    findNavController().navigate(directions)
                }

                CustomAction.BusinessDetails -> {
                    fromSummary = 1
                    findNavController().navigate(R.id.businesDetailsFragment)
                }

                CustomAction.BusinessAddress -> {
                    fromSummary = 2
                    findNavController().navigate(R.id.businessAddressFragment)
                }

                CustomAction.ResidentialDetails -> {
                    fromSummary = 3
                    findNavController().navigate(R.id.residentialDetailsFragment)
                }

                CustomAction.Nextofkin -> {
                    fromSummary = 4
                    findNavController().navigate(R.id.nextOfKinFragment)
                }

                CustomAction.addHouseholdMembers -> {
                    fromSummary = 5
                    findNavController().navigate(R.id.addHouseholdMembersFragment)
                }

                CustomAction.AddIncome -> {
                    fromSummary = 6
                    findNavController().navigate(R.id.addIncomeFragment)
                }

                CustomAction.AddExpenses -> {
                    fromSummary = 7
                    findNavController().navigate(R.id.addExpensesFragment)
                }

                CustomAction.AdditionalDetails -> {
                    fromSummary = 0
                    findNavController().navigate(R.id.customerAdditionalDetailsFragment)
                }
            }

        }
        viewmodel.cIdNumber.observe(viewLifecycleOwner) { customerIDNumber ->
            getSavedItemsFromRoom(customerIDNumber)
        }
    }

    private fun initializeUI() {
        handleBackButton()
        binding.ivBack.setOnClickListener { v ->
            findNavController().navigate(R.id.action_summaryFragment_to_customerAdditionalDetailsFragment)
            //findNavController().navigate(R.id.step13EnrollFingerPrintFragmentMethod1)
        }
        binding.btnSubmit.setOnClickListener { v ->
            if (!binding.cbAccount.isChecked) {
                toastyErrors("Check to confirm the details provided are valid")
            } else {
                submitOnboardCustomer("")
                //performApiRequestEnrollWithMultipleImages()
            }

        }
        viewmodel.responseOnStatus.observe(viewLifecycleOwner) {
            if (null != it) {
                when (it) {
                    GeneralResponseStatus.LOADING -> {
                        binding.progressbar.mainPBar.makeVisible()
                        binding.progressbar.tvWait.text = "Submitting data..."
                    }

                    GeneralResponseStatus.DONE -> {
                        binding.progressbar.mainPBar.makeGone()
                    }

                    GeneralResponseStatus.ERROR -> {
                        binding.progressbar.mainPBar.makeGone()
                    }
                }
            }
        }
        viewmodel.statusCode.observe(viewLifecycleOwner) {
            if (null != it) {
                when (it) {
                    1 -> {
                        customerId = viewmodel.customerId
                        viewmodel.updateCustomerIsProcessed(
                            true,
                            nationalIdentity,
                            customerId
                        )
                        /*customerDetailEntity.apply {
                            lastStep = "SummaryFragment"
                            isComplete = true
                            saveCustomerFullDatLocally(customerDetailEntity)
                            val json = Gson()
                            Log.d(
                                "TAG",
                                "customerDetailEntity: ${json.toJson(customerDetailEntity)}"
                            )
                        }*/
                        //customerId = viewmodel.customerId
                        uploadRoomDocuments()
                        //performApiRequestEnrollWithMultipleImages()
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
        viewmodel.responseGStatus.observe(viewLifecycleOwner) {
            if (null != it) {
                when (it) {
                    GeneralResponseStatus.LOADING -> {
                        binding.progressbar.mainPBar.makeVisible()
                        binding.progressbar.tvWait.text = "Uploading documents..."
                    }

                    GeneralResponseStatus.DONE -> {
                        binding.progressbar.mainPBar.makeGone()
                    }

                    GeneralResponseStatus.ERROR -> {
                        binding.progressbar.mainPBar.makeGone()
                    }
                }
            }
        }
        viewmodel.statusDocCode.observe(viewLifecycleOwner) {
            if (null != it) {
                when (it) {
                    1 -> {
                        Log.e("TAG", "initializeUI: ${Gson().toJson(customerDetailEntity)}")
                        customerDocsList.forEach { uploadedDoc ->
                            Log.d("TAG", "uploadedDoc: $uploadedDoc")
                            deleteImageFromInternalStorage(requireContext(), uploadedDoc.docPath)
                            viewmodel.deleteCustomerD(customerDetailEntity)
                        }
                        findNavController().navigate(R.id.onboardCustomerSuccessFragment)
                        viewmodel.stopObserving()
                    }

                    0 -> {
                        viewmodel.stopObserving()
                        onInfoDialog(viewmodel.statusMessage.value)
                    }

                    else -> {
                        findNavController().navigate(R.id.dashboardFragment)
                        toastyInfos("Error occurred while uploading the documents...\nKindly try again later...")
                        viewmodel.stopObserving()

                    }
                }
            }
        }
    }

    private fun submitOnboardCustomer(fingerprint_reg_id: String) {
        items.clear()
        viewmodel.updateCustomerLastStep(true, nationalIdentity, "SummaryFragment")
        viewmodel.updateCustomerHasFinished(true, nationalIdentity)
        val onboardCustomerDTO = OnboardCustomerDTO(
            area_id,
//            fingerprint_reg_id,
            isCompletion,
            bsDistrictId,
            alias,
            customerNumber,
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
            dob,
            educationLevelId,
            email,
            employmentStatusId,
            firstName,
            genderId,
            guarantors,
            howClientKnewMmfId,
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
            otherBorrowings,
            phone,
            resAccommodationStatusId,
            resLivingSince,
            resPhysicalAddress,
            spouseName,
            spousePhone,
            eFood,
            medical,
            eRent,
            eFees,
            eTransport,
            eOthers,
            netSalaryI,
            grossSalaryI,
            totalSalesI,
            profitI,
            rentalIncomeI,
            donationI,
            otherIncomeI,
            householdMembers
        )
        Log.d("TAG", "initializeUI: ${Gson().toJson(onboardCustomerDTO)}")
        viewmodel.onBoardCustomerFirst(onboardCustomerDTO)
    }

    private fun uploadRoomDocuments() {
        if (customerDocsList.isEmpty()) {
            //   viewmodel.deleteCustomerD(customerDetailEntity)
            findNavController().navigate(R.id.onboardCustomerSuccessFragment)
        } else {
            initiateDocumentsUpload(customerDocsList)
        }
    }

    private fun initiateDocumentsUpload(customerDocsList: List<CustomerDocsEntity>) {
        /**run within the lifecycle of the view, if its destoryed it stop uploading */
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {

            var count = 0

            /**incase of an error occured while uplading image to stop uploading*/
            val filteredList = customerDocsList.filter { customerDocsEntity ->
                customerDocsEntity.docPath.isNotEmpty() && !pattern.containsMatchIn(
                    customerDocsEntity.docPath
                )
            }
            if (filteredList.isNotEmpty()) {
                Log.e("TAG", "initiateDocumentsUploadfilteredList: $filteredList")
                val lastIndex = filteredList.size.minus(1)
                while (count < filteredList.size) {
                    val customerDocsEntity = filteredList[count]
                    if (customerDocsEntity.docPath.isNotEmpty() && !pattern.containsMatchIn(
                            customerDocsEntity.docPath
                        )
                    ) {
                        val contextWrapper = ContextWrapper(requireContext())
                        // return a directory in internal storage
                        val directory =
                            contextWrapper.getDir(Constants.IMAGES_DIR, Context.MODE_PRIVATE)
                        val location = "${directory.absolutePath}/${customerDocsEntity.docPath}"
                        val compressedImages =
                            Compressor.compress(requireContext(), convertPathToFile(location))
                        val file = MultipartBody.Part.createFormData(
                            "file",
                            compressedImages.name, RequestBody.create(
                                "multipart/form-data".toMediaTypeOrNull(),
                                compressedImages
                            )
                        )
                        val customerID = RequestBody.create(
                            MultipartBody.FORM,
                            Constants.customerId.toString()
                        )
                        Log.d("TAG", "onBoardDocuments: ${Constants.customerId}")
                        val docTypeCode =
                            RequestBody.create(MultipartBody.FORM, customerDocsEntity.docCode)
                        val channelGeneratedCode =
                            RequestBody.create(
                                MultipartBody.FORM,
                                customerDocsEntity.docGeneratedUID
                            )
                        Log.d("TAG", "initiateDocumentsUpload:${customerDocsEntity.docCode} ")
                        val success = viewmodel.uploadCustomerDocs(
                            customerID, docTypeCode,
                            channelGeneratedCode, file, count == lastIndex
                        )
                        if (!success) break
                    }
                    count++

                }
                com.deefrent.rnd.common.utils.Constants.deleteCacheImageFromInternalStorage(
                    requireContext(),
                    "compressor"
                )
            } else {
                customerDocsList.forEach { uploadedDoc ->
                    Log.d("TAG", "uploadedDoc: $uploadedDoc")
                    deleteImageFromInternalStorage(requireContext(), uploadedDoc.docPath)
                    viewmodel.deleteCustomerD(customerDetailEntity)
                }
                withContext(Dispatchers.Main) {
                    findNavController().navigate(R.id.onboardCustomerSuccessFragment)
                    viewmodel.stopObserving()
                }
            }
        }
    }

    private fun loadSlider() {
        /**observe status code to know when to display error and empty page*/
        binding.tabDots.setupWithViewPager(binding.vppager, true)
        binding.vppager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
            }
        })
    }

    companion object {
        private var isCompletion: String = ""
        private var area_id: String = ""
        private var bsDistrictId = ""
        private var bsEconomicFactorId = ""
        private var bsEstablishmentTypeId = ""
        private var bsNameOfIndustry = ""
        private var bsNumberOfEmployees = ""
        private var bsPhoneNumber = ""
        private var customerNumber = ""
        private var bsPhysicalAddress = ""
        private var bsTypeOfBusinessId = ""
        private var bsVillageId = ""
        private var bsYearsInBusiness = ""
        private var collaterals: List<OnboardCustomerDTO.Collateral> = arrayListOf()
        private var dob = ""
        private var educationLevelId = ""
        private var email = ""
        private var employmentStatusId = ""
        private var firstName = ""
        private var genderId = ""
        private var guarantors: List<OnboardCustomerDTO.Guarantor> = arrayListOf()
        private var howClientKnewMmfId = ""
        private var kinFirstName = ""
        private var kinIdNumber = ""
        private var kinIdentityTypeId = ""
        private var kinLastName = ""
        private var kinPhoneNumber = ""
        private var kinRelationshipId = ""
        private var lastName = ""
        private var nationalIdentity = ""
        private var numberOfChildren = ""
        private var numberOfDependants = ""
        private var otherBorrowings: List<OnboardCustomerDTO.OtherBorrowing> = arrayListOf()
        private var phone = ""
        private var resAccommodationStatusId = ""
        private var resLivingSince = ""
        private var resPhysicalAddress = ""
        private var spouseName = ""
        private var spousePhone = ""
        private var alias = ""
        private var householdMembers: List<OnboardCustomerDTO.HouseholdMember> = arrayListOf()
        var netSalaryI: String = ""
        var grossSalaryI: String = ""
        var totalSalesI: String = ""
        var profitI: String = ""
        var rentalIncomeI: String = ""
        var donationI: String = ""
        var otherIncomeI: String = ""
        var eRent: String = ""
        var eFood: String = ""
        var eFees: String = ""
        var eTransport: String = ""
        var medical: String = ""
        var eOthers: String = ""

    }

    private fun saveCustomerFullDatLocally(customerEntity: CustomerDetailsEntity) {
        viewmodel.upsertCustomerD(customerEntity)
    }

    private fun showErrorDialog() {
        val dialog = Dialog(requireContext())
        cardBinding = SummaryFailedDialogBinding.inflate(LayoutInflater.from(context))
        cardBinding.ivCancel.setOnClickListener {
            dialog.dismiss()
        }
        cardBinding.btnNotNow.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(R.id.action_summaryFragment_to_dashboardFragment)
        }
        cardBinding.btnApplyLoan.setOnClickListener {
            items.clear()
            dialog.dismiss()
            /* val onboardCustomerDTO = OnboardCustomerDTO(
                 area_id,
                 isCompletion,
                 bsDistrictId,
                 alias,
                 customerNumber,
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
                 dob,
                 educationLevelId,
                 email,
                 employmentStatusId,
                 firstName,
                 genderId,
                 guarantors,
                 howClientKnewMmfId,
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
                 otherBorrowings,
                 phone,
                 resAccommodationStatusId,
                 resLivingSince,
                 resPhysicalAddress,
                 spouseName,
                 spousePhone,
                 eFood,
                 medical,
                 eRent,
                 eFees,
                 eTransport,
                 eOthers,
                 netSalaryI,
                 grossSalaryI,
                 totalSalesI,
                 profitI,
                 rentalIncomeI,
                 donationI,
                 otherIncomeI,
                 householdMembers
             )
             viewmodel.onBoardCustomerFirst(onboardCustomerDTO)*/
            //performApiRequestEnrollWithMultipleImages()
            submitOnboardCustomer("")
        }

        dialog.setContentView(cardBinding.root)
        dialog.show()
        dialog.setCancelable(false)
    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_summaryFragment_to_customerAdditionalDetailsFragment)
                    //findNavController().navigate(R.id.step13EnrollFingerPrintFragmentMethod1)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun getSavedItemsFromRoom(parentNationalId: String) {
        viewmodel.fetchCustomerDetails(parentNationalId)
            .observe(viewLifecycleOwner) { cdewithList ->

                customerDetailEntity = cdewithList.customerDetails
                customerDocsList = cdewithList.customerDocs
                /**customer details*/
                val customerDetails: ArrayList<Pair<String, String>> = arrayListOf()
                if (cdewithList.customerDetails.firstName.isNotEmpty()) {
                    customerDetails.add(Pair("First name", cdewithList.customerDetails.firstName))
                    firstName = cdewithList.customerDetails.firstName
                }
                if (cdewithList.customerDetails.lastName.isNotEmpty()) {
                    customerDetails.add(Pair("Last name", cdewithList.customerDetails.lastName))
                    lastName = cdewithList.customerDetails.lastName
                }
                if (cdewithList.customerDetails.alias.isNotEmpty()) {
                    customerDetails.add(Pair("Alias(AKA)", cdewithList.customerDetails.alias))
                    alias = cdewithList.customerDetails.alias
                }
                if (cdewithList.customerDetails.email.isNotEmpty()) {
                    customerDetails.add(Pair("Email address", cdewithList.customerDetails.email))
                    email = cdewithList.customerDetails.email
                }
                if (cdewithList.customerDetails.dob.isNotEmpty()) {
                    customerDetails.add(Pair("Date of birth", cdewithList.customerDetails.dob))
                    dob = cdewithList.customerDetails.dob
                }
                if (cdewithList.customerDetails.genderName.isNotEmpty()) {
                    customerDetails.add(Pair("Gender", cdewithList.customerDetails.genderName))
                }
                if (cdewithList.customerDetails.genderId.isNotEmpty()) {
                    genderId = cdewithList.customerDetails.genderId
                }
                if (cdewithList.customerDetails.spouseName.isNotEmpty()) {
                    customerDetails.add(Pair("Spouse name", cdewithList.customerDetails.spouseName))
                    spouseName = cdewithList.customerDetails.spouseName
                }
                if (cdewithList.customerDetails.spousePhone.isNotEmpty()) {
                    customerDetails.add(
                        Pair(
                            "Spouse Phone",
                            cdewithList.customerDetails.spousePhone
                        )
                    )
                    spousePhone = cdewithList.customerDetails.spousePhone
                }
                if (cdewithList.customerDetails.nationalIdentity.isNotEmpty()) {
                    customerDetails.add(
                        Pair(
                            "ID Number",
                            cdewithList.customerDetails.nationalIdentity
                        )
                    )
                    nationalIdentity = cdewithList.customerDetails.nationalIdentity

                }
                if (cdewithList.customerDetails.phone.isNotEmpty()) {
                    phone = cdewithList.customerDetails.phone
                }
                if (cdewithList.customerDetails.customerNumber.isNotEmpty()) {
                    customerNumber = cdewithList.customerDetails.customerNumber
                }
                isCompletion = cdewithList.customerDetails.completion
                area_id = cdewithList.customerDetails.subBranchId
                Log.e("TAG", "getSavedItemsFromRoomIS: $isCompletion")
                Log.e("TAG", "getSavedItemsFromRoomISC: ${cdewithList.customerDetails.completion}")
                items.add(
                    CustomData(
                        "Customer Details",
                        customerDetails,
                        CustomAction.CustomerDetails
                    )
                )
                /**Bs details*/
                val businessDetails: ArrayList<Pair<String, String>> = arrayListOf()
                // if (cdewithList.customerDetails.isBSButtonChecked){
                if (cdewithList.customerDetails.bsTypeOfBusiness.isNotEmpty()) {
                    businessDetails.add(
                        Pair(
                            "Type of business",
                            cdewithList.customerDetails.bsTypeOfBusiness
                        )
                    )
                }
                if (cdewithList.customerDetails.bsEconomicFactor.isNotEmpty()) {
                    businessDetails.add(
                        Pair(
                            "Economic Sector",
                            cdewithList.customerDetails.bsEconomicFactor
                        )
                    )
                }
                if (cdewithList.customerDetails.bsEstablishmentType.isNotEmpty()) {
                    businessDetails.add(
                        Pair(
                            "Establishment Type",
                            cdewithList.customerDetails.bsEstablishmentType
                        )
                    )
                }
                if (cdewithList.customerDetails.bsNameOfIndustry.isNotEmpty()) {
                    businessDetails.add(
                        Pair(
                            "Name of Industry",
                            cdewithList.customerDetails.bsNameOfIndustry
                        )
                    )
                    bsNameOfIndustry = (cdewithList.customerDetails.bsNameOfIndustry)
                }
                if (cdewithList.customerDetails.bsYearsInBusiness.isNotEmpty()) {
                    businessDetails.add(
                        Pair(
                            "Year In Business",
                            cdewithList.customerDetails.bsYearsInBusiness
                        )
                    )
                    bsYearsInBusiness = (cdewithList.customerDetails.bsYearsInBusiness)
                }
                if (cdewithList.customerDetails.bsTypeOfBusinessId.isNotEmpty()) {
                    bsTypeOfBusinessId = cdewithList.customerDetails.bsTypeOfBusinessId
                }
                if (cdewithList.customerDetails.bsEconomicFactorId.isNotEmpty()) {
                    bsEconomicFactorId = cdewithList.customerDetails.bsEconomicFactorId
                }
                if (cdewithList.customerDetails.bsEstablishmentTypeId.isNotEmpty()) {
                    bsEstablishmentTypeId = cdewithList.customerDetails.bsEstablishmentTypeId
                }
                items.add(
                    CustomData(
                        "Business Details",
                        businessDetails,
                        CustomAction.BusinessDetails
                    )
                )
                /**Bs address*/
                val businessAddress: ArrayList<Pair<String, String>> = arrayListOf()
                if (cdewithList.customerDetails.bsDistrict.isNotEmpty()) {
                    businessAddress.add(Pair("Province", cdewithList.customerDetails.bsDistrict))
                }
                if (cdewithList.customerDetails.bsVillage.isNotEmpty()) {
                    businessAddress.add(Pair("Town/City", cdewithList.customerDetails.bsVillage))
                }
                if (cdewithList.customerDetails.bsPhysicalAddress.isNotEmpty()) {
                    businessAddress.add(
                        Pair(
                            "Physical Address",
                            cdewithList.customerDetails.bsPhysicalAddress
                        )
                    )
                    bsPhysicalAddress = cdewithList.customerDetails.bsPhysicalAddress
                }
                if (cdewithList.customerDetails.bsPhoneNumber.isNotEmpty()) {
                    businessAddress.add(
                        Pair(
                            "Business Phone Number",
                            cdewithList.customerDetails.bsPhoneNumber
                        )
                    )
                    bsPhoneNumber = cdewithList.customerDetails.bsPhoneNumber
                }
                if (cdewithList.customerDetails.bsNumberOfEmployees.isNotEmpty()) {
                    businessAddress.add(
                        Pair(
                            "Number of employees",
                            cdewithList.customerDetails.bsNumberOfEmployees
                        )
                    )
                    bsNumberOfEmployees = cdewithList.customerDetails.bsNumberOfEmployees
                }
                if (cdewithList.customerDetails.bsDistrictId.isNotEmpty()) {
                    bsDistrictId = cdewithList.customerDetails.bsDistrictId
                }
                if (cdewithList.customerDetails.bsVillageId.isNotEmpty()) {
                    bsVillageId = cdewithList.customerDetails.bsVillageId
                }
                items.add(
                    CustomData(
                        "Business Address",
                        businessAddress,
                        CustomAction.BusinessAddress
                    )
                )
                /**residential details*/
                val residential: ArrayList<Pair<String, String>> = arrayListOf()
                if (cdewithList.customerDetails.resAccommodationStatus.isNotEmpty()) {
                    residential.add(
                        Pair(
                            "Accommodation Status",
                            cdewithList.customerDetails.resAccommodationStatus
                        )
                    )
                }
                if (cdewithList.customerDetails.resPhysicalAddress.isNotEmpty()) {
                    residential.add(
                        Pair(
                            "Physical Address",
                            cdewithList.customerDetails.resPhysicalAddress
                        )
                    )
                    resPhysicalAddress = cdewithList.customerDetails.resPhysicalAddress
                }
                if (cdewithList.customerDetails.resLivingSince.isNotEmpty()) {
                    residential.add(
                        Pair(
                            "Living Since",
                            cdewithList.customerDetails.resLivingSince
                        )
                    )
                    resLivingSince = cdewithList.customerDetails.resLivingSince
                }
                if (cdewithList.customerDetails.resAccommodationStatusId.isNotEmpty()) {
                    resAccommodationStatusId = cdewithList.customerDetails.resAccommodationStatusId
                }
                items.add(
                    CustomData(
                        "Residential Details",
                        residential,
                        CustomAction.ResidentialDetails
                    )
                )
                /**Nok*/
                val nok: ArrayList<Pair<String, String>> = arrayListOf()
                if (cdewithList.customerDetails.kinRelationship.isNotEmpty()) {
                    nok.add(Pair("Relationship", cdewithList.customerDetails.kinRelationship))
                }
                if (cdewithList.customerDetails.kinFirstName.isNotEmpty()) {
                    nok.add(Pair("First Name", cdewithList.customerDetails.kinFirstName))
                    kinFirstName = cdewithList.customerDetails.kinFirstName
                }
                if (cdewithList.customerDetails.kinLastName.isNotEmpty()) {
                    nok.add(Pair("Surname", cdewithList.customerDetails.kinLastName))
                    kinLastName = cdewithList.customerDetails.kinLastName
                }
                if (cdewithList.customerDetails.kinIdentityType.isNotEmpty()) {
                    nok.add(Pair("Identity Type", cdewithList.customerDetails.kinIdentityType))
                }
                if (cdewithList.customerDetails.kinIdNumber.isNotEmpty()) {
                    nok.add(Pair("ID Number", cdewithList.customerDetails.kinIdNumber))
                    kinIdNumber = cdewithList.customerDetails.kinIdNumber
                }
                if (cdewithList.customerDetails.kinPhoneNumber.isNotEmpty()) {
                    nok.add(Pair("Phone Number", cdewithList.customerDetails.kinPhoneNumber))
                    kinPhoneNumber = cdewithList.customerDetails.kinPhoneNumber
                }
                if (cdewithList.customerDetails.kinRelationshipId.isNotEmpty()) {
                    kinRelationshipId = cdewithList.customerDetails.kinRelationshipId
                }
                if (cdewithList.customerDetails.kinIdentityTypeId.isNotEmpty()) {
                    kinIdentityTypeId = cdewithList.customerDetails.kinIdentityTypeId
                }
                items.add(CustomData("Next of kin details", nok, CustomAction.Nextofkin))
                /**Income Details*/
                val incomeDetails: ArrayList<Pair<String, String>> = arrayListOf()
                if (cdewithList.customerDetails.grossSalary.isNotEmpty()) {
                    incomeDetails.add(Pair("Gross Income", cdewithList.customerDetails.grossSalary))
                    grossSalaryI = cdewithList.customerDetails.grossSalary
                }
                if (cdewithList.customerDetails.netSalary.isNotEmpty()) {
                    incomeDetails.add(Pair("Net Income", cdewithList.customerDetails.netSalary))
                    netSalaryI = cdewithList.customerDetails.netSalary
                }
                if (cdewithList.customerDetails.totalSales.isNotEmpty()) {
                    incomeDetails.add(Pair("Total Sales", cdewithList.customerDetails.totalSales))
                    totalSalesI = cdewithList.customerDetails.totalSales
                }
                if (cdewithList.customerDetails.profit.isNotEmpty()) {
                    incomeDetails.add(Pair("Sales profit", cdewithList.customerDetails.profit))
                    profitI = cdewithList.customerDetails.profit
                }
                if (cdewithList.customerDetails.rIncome.isNotEmpty()) {
                    incomeDetails.add(Pair("Rental Income", cdewithList.customerDetails.rIncome))
                    rentalIncomeI = cdewithList.customerDetails.rIncome
                }
                if (cdewithList.customerDetails.donation.isNotEmpty()) {
                    incomeDetails.add(
                        Pair(
                            "Donation/Remittance",
                            cdewithList.customerDetails.donation
                        )
                    )
                    donationI = cdewithList.customerDetails.donation
                }
                if (cdewithList.customerDetails.otherIncome.isNotEmpty()) {
                    incomeDetails.add(Pair("Other Income", cdewithList.customerDetails.otherIncome))
                    otherIncomeI = cdewithList.customerDetails.otherIncome
                }
                items.add(CustomData("Income Details", incomeDetails, CustomAction.AddIncome))
                /**expenses*/
                val expensesDetails: ArrayList<Pair<String, String>> = arrayListOf()
                if (cdewithList.customerDetails.rentalsExpenses.isNotEmpty()) {
                    expensesDetails.add(
                        Pair(
                            "Rental Expenses",
                            FormatDigit.formatDigits(cdewithList.customerDetails.rentalsExpenses)
                        )
                    )
                    eRent = cdewithList.customerDetails.rentalsExpenses
                }
                if (cdewithList.customerDetails.food.isNotEmpty()) {
                    expensesDetails.add(
                        Pair(
                            "Food",
                            FormatDigit.formatDigits(cdewithList.customerDetails.food)
                        )
                    )
                    eFood = cdewithList.customerDetails.food
                }
                if (cdewithList.customerDetails.schoolFees.isNotEmpty()) {
                    expensesDetails.add(
                        Pair(
                            "School Fees",
                            FormatDigit.formatDigits(cdewithList.customerDetails.schoolFees)
                        )
                    )
                    eFees = cdewithList.customerDetails.schoolFees
                }
                if (cdewithList.customerDetails.transport.isNotEmpty()) {
                    expensesDetails.add(
                        Pair(
                            "Transport",
                            FormatDigit.formatDigits(cdewithList.customerDetails.transport)
                        )
                    )
                    eTransport = cdewithList.customerDetails.transport
                }
                if (cdewithList.customerDetails.medicalAidOrContributions.isNotEmpty()) {
                    expensesDetails.add(
                        Pair(
                            "Medical Aid/Contributions",
                            FormatDigit.formatDigits(cdewithList.customerDetails.medicalAidOrContributions)
                        )
                    )
                    medical = cdewithList.customerDetails.medicalAidOrContributions
                }
                if (cdewithList.customerDetails.otherExpenses.isNotEmpty()) {
                    expensesDetails.add(
                        Pair(
                            "Other Expenses",
                            FormatDigit.formatDigits(cdewithList.customerDetails.otherExpenses)
                        )
                    )
                    eOthers = cdewithList.customerDetails.otherExpenses
                }
                items.add(CustomData("Expenses Details", expensesDetails, CustomAction.AddExpenses))
                /**Additional details*/
                val additionalDetails: ArrayList<Pair<String, String>> = arrayListOf()
                if (cdewithList.customerDetails.educationLevel.isNotEmpty()) {
                    additionalDetails.add(
                        Pair(
                            "Education Level",
                            cdewithList.customerDetails.educationLevel
                        )
                    )
                }
                if (cdewithList.customerDetails.educationLevelId.isNotEmpty()) {
                    educationLevelId = cdewithList.customerDetails.educationLevelId
                }
                if (cdewithList.customerDetails.howClientKnewMmfId.isNotEmpty()) {
                    howClientKnewMmfId = cdewithList.customerDetails.howClientKnewMmfId
                }
                if (cdewithList.customerDetails.employmentStatusId.isNotEmpty()) {
                    employmentStatusId = cdewithList.customerDetails.employmentStatusId
                }
                if (cdewithList.customerDetails.howClientKnewMmf.isNotEmpty()) {
                    additionalDetails.add(
                        Pair(
                            "How did you hear about us",
                            cdewithList.customerDetails.howClientKnewMmf
                        )
                    )
                }
                if (cdewithList.customerDetails.employmentStatus.isNotEmpty()) {
                    additionalDetails.add(
                        Pair(
                            "Employment Status",
                            cdewithList.customerDetails.employmentStatus
                        )
                    )
                }
                if (cdewithList.customerDetails.numberOfChildren.isNotEmpty()) {
                    additionalDetails.add(
                        Pair(
                            "Number Of Children",
                            cdewithList.customerDetails.numberOfChildren
                        )
                    )
                    numberOfChildren = cdewithList.customerDetails.numberOfChildren
                }
                if (cdewithList.customerDetails.numberOfDependants.isNotEmpty()) {
                    additionalDetails.add(
                        Pair(
                            "Number Of Dependants",
                            cdewithList.customerDetails.numberOfDependants
                        )
                    )
                    numberOfDependants = cdewithList.customerDetails.numberOfDependants
                }
                items.add(
                    CustomData(
                        "Additional Details",
                        additionalDetails,
                        CustomAction.AdditionalDetails
                    )
                )
                customerSummaryAdapter.notifyDataSetChanged()
                val guarantorList = cdewithList.guarantors.map { guarantorModel ->
                    OnboardCustomerDTO.Guarantor(
                        guarantorModel.idNumber,
                        guarantorModel.name,
                        guarantorModel.phone,
                        guarantorModel.relationshipId,
                        guarantorModel.residenceAddress,
                        guarantorModel.guarantorGeneratedUID
                    )
                }
                guarantors = guarantorList
                Log.e("TAG", "getSavedItemsFromRoomG: $guarantorList")
                Log.e("TAG", "getSavedItemsFromRoomGL: $guarantors")

                /**collateralList Details*/
                val collateralList = cdewithList.collateral.map {
                    OnboardCustomerDTO.Collateral(
                        it.assetTypeId,
                        it.estimateValue,
                        it.model,
                        it.name,
                        it.serialNumber,
                        it.collateralGeneratedUID
                    )
                }
                collaterals = collateralList

                /**other borrowing Details*/
                val borrowingList = cdewithList.otherBorrowing.map { borrowModel ->
                    OnboardCustomerDTO.OtherBorrowing(
                        borrowModel.institutionName,
                        borrowModel.amount,
                        borrowModel.totalAmountPaidToDate,
                        borrowModel.status,
                        borrowModel.monthlyInstallmentPaid
                    )
                }
                otherBorrowings = borrowingList

                /**householdMember*/
                val household = cdewithList.householdMember.map { memb ->
                    OnboardCustomerDTO.HouseholdMember(
                        memb.fullName,
                        memb.incomeOrFeesPaid,
                        "",
                        memb.natureOfActivity,
                        memb.occupationId,
                        memb.relationshipId
                    )
                }
                householdMembers = household
                val json = Gson()
                Log.d("TAG", "household: ${json.toJson(household)}")

            }


    }

//    private fun performApiRequestEnrollWithMultipleImages() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.enrollCustomerWithMultipleImages(
//                idNumber = customerId,
//                finger_index = "1",
//                hand_type = "1",
//            ).collect {
//                when (it) {
//                    is ResourceNetworkFlow.Error -> {
//                        binding.progressbar.mainPBar.makeGone()
//                        showErrorDialog()
//                    }
//
//                    is ResourceNetworkFlow.Loading -> {
//                        binding.progressbar.mainPBar.makeVisible()
//                    }
//
//                    is ResourceNetworkFlow.Success -> {
//                        binding.progressbar.mainPBar.makeGone()
//                        if (it.data?.status == 200) {
//                            submitOnboardCustomer(it.data?.data?.userUid.toString())
//                            lifecycleScope.launch {
//                                viewModel.deleteByPhoneNumber(phone)
//                            }
//
//                        } else {
//                            showErrorDialog()
//                            Log.e("", "ESLE RESPONSE: ${it.data?.message.toString()}")
//                        }
//                    }
//
//                    else -> {
//                        Log.e("", "else RESPONSE:")
//                    }
//                }
//            }
//        }
//    }
}




