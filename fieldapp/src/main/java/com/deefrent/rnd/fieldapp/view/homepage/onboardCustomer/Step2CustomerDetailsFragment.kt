package com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.deefrent.rnd.jiboostfieldapp.AppPreferences
import com.deefrent.rnd.common.abstractions.BaseDaggerFragment
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.common.network.ResourceNetworkFlow
import com.deefrent.rnd.common.utils.showOneButtonDialog
import com.deefrent.rnd.common.utils.showTwoButtonDialog
import com.deefrent.rnd.common.utils.visibilityView
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.EditPhotsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentStep2CustomerDetailsBinding
import com.deefrent.rnd.fieldapp.models.xaraniIdCheck.request.XaraniIdCheckRequest
import com.deefrent.rnd.fieldapp.models.xaraniIdCheck.response.XaraniIdCheckData
import com.deefrent.rnd.fieldapp.models.xaraniIdCheck.response.XaraniIdCheckResponse
import com.deefrent.rnd.fieldapp.network.models.Gender
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.fromSummary
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.frontIDCode
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.isFromCustomerDetails
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.pattern
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.profilePicCode
import com.deefrent.rnd.fieldapp.viewModels.DropdownItemsViewModel
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.smartengines.ScanSmartActivity
import dev.ronnie.github.imagepicker.ImagePicker
import dev.ronnie.github.imagepicker.ImageResult
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * This onboarding process has given me a real character devepment, cheeeei
 * Good day coder before you start modifying code, kindly recheck it men!
 * that's all
 * */
class Step2CustomerDetailsFragment : BaseDaggerFragment() {
    private lateinit var binding: FragmentStep2CustomerDetailsBinding
    private lateinit var cardBinding: EditPhotsDialogBinding
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private var guarantor = arrayListOf<Guarantor>()
    private var collateral = arrayListOf<Collateral>()
    private lateinit var customerDocs: ArrayList<CustomerDocsEntity>
    private var household = arrayListOf<HouseholdMemberEntity>()
    private var customerDetailsEntity = CustomerDetailsEntity()
    private var isButtonCheck = true
    private var otherBorrowing = arrayListOf<OtherBorrowing>()
    private var customerImagePassport = arrayListOf<CustomerDocsEntity>()
    private var customerImage = arrayListOf<CustomerDocsEntity>()
    private var frontIDImageName = ""
    private var passportImageName = ""
    private var frontIDUri: Uri? = null
    private var passportPhotoUri: Uri? = null
    private lateinit var maxColateral: String
    private lateinit var maxGuarantor: String
    private lateinit var oldNationalID: String

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var idCustomerLookUpViewModel: IDCustomerLookUpViewModel

    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences

    private val viewmodel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory)[OnboardCustomerViewModel::class.java]
    }
    private var gId = ""
    private var sBranchId = ""
    private var phoneNumber = ""
    private var customerPhone = ""
    private var isComp = "0"
    private var customerDetailValue = ""
    lateinit var imagePicker: ImagePicker
    private lateinit var customerFrontID: CustomerDocsEntity
    private lateinit var customerFaceID: CustomerDocsEntity
    val args: Step2CustomerDetailsFragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePicker = ImagePicker(fragment = this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStep2CustomerDetailsBinding.inflate(layoutInflater)
        viewmodel.stopObserving()
        customerDocs = arrayListOf()
        initializeUI()
        binding.rbMyself.isChecked = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TAG", "onViewCreated: ${viewmodel.isFromLookup}")
        if (!viewmodel.isFromLookup) {
            viewmodel.cIdNumber.observe(viewLifecycleOwner) { national ->
                if (national != null) {
                    getSavedItemsFromRoom(national)
                }
            }
        }

        maxColateral = AppPreferences.maxCollateral.toString()
        maxGuarantor = AppPreferences.maxGuarantor.toString()
        Log.d("TAG", "onCreateView: max gua: $maxColateral")
        Log.d("TAG", "onCreateView: min gua: $maxGuarantor")

        setUpBindingApply()
        // performXaraniIdCheck()

    }

    private fun setUpBindingApply() {
        binding.apply {
            tvAttachFrontIDDoc.setOnClickListener {
                if (tvAttachFrontIDDoc.text.contains("View Front National ID")) {
                    showEditPhotoDialog(customerFrontID)
                } else {
                    showPickerOptionsDialog("CustomerID")
                }
            }
            tvAttachPassport.setOnClickListener {
                if (tvAttachPassport.text.contains("View Passport Size Photo")) {
                    showEditPhotoDialog(customerFaceID)
                } else {
                    showPickerOptionsDialog("PassportPhoto")
                }
            }
        }

        binding.apply {
            viewmodel.customerPhoneNumber.observe(viewLifecycleOwner) { custPhone ->
                customerPhone = custPhone!!

            }
            rbMyself.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    isButtonCheck = true
                    clOption.makeVisible()
                    rbOthers.isChecked = false
                }
            }
            rbOthers.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    isButtonCheck = false
                    rbMyself.isChecked = false
                    clOption.makeGone()
                }

            }
        }
        binding.etDob.setOnClickListener { pickDob() }
        binding.tiSubBranch.editText?.addTextChangedListener(CustomTextWatcher(binding.tiSubBranch))
        binding.etDob.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.tlDob.error = null
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.tlDob.error = null
            }

            override fun afterTextChanged(p0: Editable?) {
                binding.tlDob.error = null
            }
        })
        binding.apply {
            /**gender spinner impl*/
            dropdownItemsViewModel.getAllGender().observe(viewLifecycleOwner) {
                if (it != null) {
                    populateGender(it)
                } else {
                    toastyErrors(
                        "An error occurred. Please try again"
                    )
                    findNavController().navigateUp()
                }
            }
            /**sub branches spinner impl*/
            dropdownItemsViewModel.getAllOfficerSubBranches().observe(viewLifecycleOwner) {
                if (it != null) {
                    populateSubBranches(it)
                } else {
                    toastyErrors(
                        "An error occurred. Please try again"
                    )
                    findNavController().navigateUp()
                }
            }
            binding.etIdNo.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding.tlIdNo.error = null
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding.tlIdNo.error = null
                }

                override fun afterTextChanged(p0: Editable?) {
                    binding.tlIdNo.error = null
                }
            })
            binding.etSpousePhone.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding.etSpousePhone.error = null
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding.etSpousePhone.error = null
                }

                override fun afterTextChanged(p0: Editable?) {
                    binding.etSpousePhone.error = null
                }
            })
            btnContinue.setOnClickListener {

                if (rbMyself.isChecked) {
                    if (validateYesFields()) {
                        customerDetailsEntity.apply {
                            lastStep = "CustomerDetailsFragment"
                            isComplete = false
                            hasFinished = false
                            isProcessed = false
                            firstName = binding.etFname.text.toString()
                            lastName = binding.etSurname.text.toString()
                            alias = etAka.text.toString()
                            email = binding.etEmail.text.toString()
                            genderName = binding.spinnerGender.text.toString()
                            nationalIdentity = binding.etIdNo.text.toString()
                            dob = binding.etDob.text.toString()
                            phone = customerPhone
                            genderId = gId
                            subBranchId = sBranchId
                            spouseName = binding.etSpName.text.toString()
                            spousePhone = binding.etSpousePhone.text.toString()
                            isButtonChecked = true
                            viewmodel.isFromLookup = false

                            when (args.fragmentType) {
                                2 -> {
                                    completion = "0"
                                    customerNumber = ""
                                    /*viewmodel.accountLookUpData.observe(viewLifecycleOwner) {
                                        Log.d(
                                            "TAG",
                                            "getSavedItemsFromRoomDebugG3: ${it.maxCollaterals}"
                                        )
                                        maximumColateral = it.maxCollaterals
                                        maximumGuarantor = it.maxGuarantors
                                    }*/
                                    findNavController().navigate(R.id.action_onboardCustomerDetailsFragment_to_businesDetailsFragment)
                                }

                                1 -> {
                                    findNavController().navigate(R.id.summaryFragment)
                                }
                                /**from incomplete*/
                                3 -> {
                                    /*maximumColateral = maxColateral
                                    maximumGuarantor = maxGuarantor*/
                                    Log.d("TAG", "onViewCreatedisComp: $isComp")
                                    completion = isComp
                                    customerNumber = customerDetailValue
                                    findNavController().navigate(R.id.action_onboardCustomerDetailsFragment_to_businesDetailsFragment)

                                }
                                /**from back incomplete*/
                                4 -> {
                                    /*maximumColateral = maxColateral
                                    maximumGuarantor = maxGuarantor*/
                                    completion = isComp
                                    customerNumber = customerDetailValue
                                    findNavController().navigate(R.id.action_onboardCustomerDetailsFragment_to_businesDetailsFragment)
                                }
                            }
                            customerDetailsEntity.maximumColateral = maxColateral
                            customerDetailsEntity.maximumGuarantor = maxGuarantor
                            customerDetailsEntity.minimumCollateral =
                                AppPreferences.minCollateral.toString()
                            customerDetailsEntity.minimumGuarantor =
                                AppPreferences.minGuarantor.toString()
                            viewmodel.customerEntityData.postValue(customerDetailsEntity)
                            if (fromSummary == 8) {
                                updateCustomerNationalID(
                                    binding.etIdNo.text.toString(),
                                    binding.etFname.text.toString(),
                                    binding.etSurname.text.toString(),
                                    etAka.text.toString(),
                                    binding.etEmail.text.toString(),
                                    sBranchId,
                                    binding.etDob.text.toString(),
                                    gId,
                                    binding.spinnerGender.text.toString(),
                                    binding.etSpName.text.toString(),
                                    binding.etSpousePhone.text.toString()
                                )
                            } else {
                                saveCustomerFullDatLocally(customerDetailsEntity)
                            }
                            if (frontIDUri != null) {
                                saveImageToInternalAppStorage(
                                    frontIDUri!!,
                                    requireContext(),
                                    frontIDImageName
                                )
                            }
                            if (passportPhotoUri != null) {
                                saveImageToInternalAppStorage(
                                    passportPhotoUri!!,
                                    requireContext(),
                                    passportImageName
                                )
                            }
                        }
                    }

                } else {
                    if (validateNoFields()) {
                        customerDetailsEntity.apply {
                            lastStep = "CustomerDetailsFragment"
                            isComplete = false
                            isProcessed = false
                            firstName = binding.etFname.text.toString()
                            lastName = binding.etSurname.text.toString()
                            alias = etAka.text.toString()
                            email = binding.etEmail.text.toString()
                            genderName = binding.spinnerGender.text.toString()
                            dob = binding.etDob.text.toString()
                            nationalIdentity = binding.etIdNo.text.toString()
                            phone = customerPhone
                            customerDetailsEntity.isButtonChecked = false
                            genderId = gId
                            subBranchId = sBranchId
                            viewmodel.isFromLookup = false
                            when (args.fragmentType) {
                                2 -> {
                                    completion = "0"
                                    customerNumber = ""
                                    /* viewmodel.accountLookUpData.observe(viewLifecycleOwner) {
                                         Log.d(
                                             "TAG",
                                             "getSavedItemsFromRoomDebugGkkkkkk3: ${it.maxCollaterals}"
                                         )
                                         maximumColateral = it.maxCollaterals
                                         maximumGuarantor = it.maxGuarantors
                                         Log.d(
                                             "TAG",
                                             "getSavedItemsFromRoomDebugGkkkkkk3: ${it.maxCollaterals}"
                                         )

                                     }*/
                                    /**from phone lookup*/
                                    findNavController().navigate(R.id.action_onboardCustomerDetailsFragment_to_businesDetailsFragment)
                                }

                                1 -> {
                                    findNavController().navigate(R.id.summaryFragment)
                                }

                                3 -> {
                                    /*maximumColateral = maxColateral
                                    maximumGuarantor = maxGuarantor*/
                                    Log.d("TAG", "onViewCreatedisComp2: $isComp")
                                    /**from incomplete registration*/
                                    completion = isComp
                                    customerNumber = customerDetailValue
                                    findNavController().navigate(R.id.action_onboardCustomerDetailsFragment_to_businesDetailsFragment)

                                }

                                4 -> {
                                    /*maximumColateral = maxColateral
                                    maximumGuarantor = maxGuarantor*/
                                    completion = isComp
                                    customerNumber = customerDetailValue
                                    findNavController().navigate(R.id.action_onboardCustomerDetailsFragment_to_businesDetailsFragment)

                                }
                            }
                            customerDetailsEntity.maximumColateral = maxColateral
                            customerDetailsEntity.maximumGuarantor = maxGuarantor
                            customerDetailsEntity.minimumCollateral =
                                AppPreferences.minCollateral.toString()
                            customerDetailsEntity.minimumGuarantor =
                                AppPreferences.minGuarantor.toString()
                            viewmodel.customerEntityData.postValue(customerDetailsEntity)
                            if (fromSummary == 8) {
                                updateCustomerNationalID(
                                    binding.etIdNo.text.toString(),
                                    binding.etFname.text.toString(),
                                    binding.etSurname.text.toString(),
                                    etAka.text.toString(),
                                    binding.etEmail.text.toString(),
                                    sBranchId,
                                    binding.etDob.text.toString(),
                                    gId,
                                    binding.spinnerGender.text.toString(),
                                    "",
                                    ""
                                )
                            } else {
                                saveCustomerFullDatLocally(customerDetailsEntity)
                            }
                            //saveCustomerFullDatLocally(customerDetailsEntity)
                            if (frontIDUri != null) {
                                saveImageToInternalAppStorage(
                                    frontIDUri!!,
                                    requireContext(),
                                    frontIDImageName
                                )
                            }
                            if (passportPhotoUri != null) {
                                saveImageToInternalAppStorage(
                                    passportPhotoUri!!,
                                    requireContext(),
                                    passportImageName
                                )
                            }
                            val json = Gson()
                            Log.d("TAG", "savDatLocally: ${json.toJson(customerDetailsEntity)}")
                        }
                    }
                }
            }
        }
        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                // Handle the Intent
            }
        }

        binding.btnXaraniIDLookUp.setOnClickListener {
           // startForResult.launch(ScanSmartActivity.getStartIntent(requireContext()))
            if (binding.etIdNo.text.toString().isEmpty()) {
                binding.etIdNo.error = "ID Number required"
            } else {
                performXaraniIdCheck()
            }
        }
    }

    private fun performXaraniIdCheck() {
        lifecycleScope.launch {
            val xaraniIdCheckRequest = XaraniIdCheckRequest(
                idNumber = binding.etIdNo.text.toString().trim()
            )
            commonSharedPreferences.saveStringData(
                CommonSharedPreferences.CU_ID_NUMBER,
                binding.etIdNo.text.toString().trim()
            )

//            binding.progressbar.mainPBar.makeGone()
//            val response = XaraniIdCheckResponse(status = 1, message = "skip", data = XaraniIdCheckData(askUserToDoManualRegistration = true) )//it.data!!  TODO undo this line @kelvin
//            Log.e("RESPONSE", "${response.data}")
//            if (response.status == 1) {
//                binding.clCustomerData.visibilityView(true)
//                binding.btnContinue.visibilityView(true)
//                binding.btnXaraniIDLookUp.visibilityView(false)
//                //
//                //populateUserData(response.data)
//                if (response.data.askUserToDoManualRegistration) {
//                    showTwoButtonDialog(
//                        title = "Oops!",
//                        description = getString(com.deefrent.rnd.common.R.string.id_number_verification_service_not_available),
//                        listenerCancel = {
//                            findNavController().popBackStack(
//                                R.id.dashboardFragment,
//                                false
//                            )
//                        },
//                        listenerConfirm = {
//                            Log.e("", "COFIRMED")
//                        }
//                    )
//                    //
//                    binding.clCustomerData.visibilityView(true)
//                    binding.btnContinue.visibilityView(true)
//                    binding.btnXaraniIDLookUp.visibilityView(false)
//                    //
//                }
            idCustomerLookUpViewModel.xaraniIdCheck(xaraniIdCheckRequest = xaraniIdCheckRequest)
                .collect {
                    when (it) {
                        is ResourceNetworkFlow.Error -> {
                            //showDummyDataForSimulation()
                            binding.progressbar.mainPBar.makeGone()
                            binding.btnXaraniIDLookUp.visibilityView(true)
                            showOneButtonDialog(
                                image = com.deefrent.rnd.common.R.drawable.ic_baseline_error_outline_24,
                                title = "Oops!",
                                description = "Looks like we have a Problem.Try again later.",
                                listener = {

                                }
                            )
                        }

                        is ResourceNetworkFlow.Loading -> {
                            binding.progressbar.mainPBar.makeVisible()
                            binding.btnXaraniIDLookUp.visibilityView(false)
                        }

                        is ResourceNetworkFlow.Success -> {
                            binding.progressbar.mainPBar.makeGone()
                            val response = XaraniIdCheckResponse(status = 1, message = "skip", data = XaraniIdCheckData(askUserToDoManualRegistration = true) )//it.data!!  TODO undo this line @kelvin
                            Log.e("RESPONSE", "${response.data}")
                            if (response.status == 1) {
                                binding.clCustomerData.visibilityView(true)
                                binding.btnContinue.visibilityView(true)
                                binding.btnXaraniIDLookUp.visibilityView(false)
                                //
                                //populateUserData(response.data)
                                if (response.data.askUserToDoManualRegistration) {
                                    showTwoButtonDialog(
                                        title = "Oops!",
                                        description = getString(com.deefrent.rnd.common.R.string.id_number_verification_service_not_available),
                                        listenerCancel = {
                                            findNavController().popBackStack(
                                                R.id.dashboardFragment,
                                                false
                                            )
                                        },
                                        listenerConfirm = {
                                            Log.e("", "COFIRMED")
                                        }
                                    )
                                    //
                                    binding.clCustomerData.visibilityView(true)
                                    binding.btnContinue.visibilityView(true)
                                    binding.btnXaraniIDLookUp.visibilityView(false)
                                    //
                                } else {
                                    populateUserData(response.data)
                                }
                            } else if (response.status == 0) {
                                //
                                Timber.d("\n\n")
                                Log.e("RESPONSE", "${response.data}")
                                Timber.d("\n\n")
                                //
                                //showDummyDataForSimulation()
                                if (response.data.askUserToDoManualRegistration) {
                                    showTwoButtonDialog(
                                        title = "Oops!",
                                        description = getString(com.deefrent.rnd.common.R.string.id_number_verification_service_not_available),
                                        listenerCancel = {
                                            findNavController().popBackStack(
                                                R.id.dashboardFragment,
                                                false
                                            )
                                        },
                                        listenerConfirm = {
                                            Log.e("", "COFIRMED")
                                        }
                                    )
                                    //
                                    binding.clCustomerData.visibilityView(true)
                                    binding.btnContinue.visibilityView(true)
                                    binding.btnXaraniIDLookUp.visibilityView(false)
                                    //
                                } else if (response.data.askUserToDoManualRegistration == false) {
                                    showOneButtonDialog(
                                        image = com.deefrent.rnd.common.R.drawable.ic_baseline_error_outline_24,
                                        title = "Oops! ",
                                        description = response.message.toString(),
                                        listener = {
                                            findNavController().popBackStack(
                                                R.id.dashboardFragment,
                                                false
                                            )
                                        }
                                    )
                                }

                            }
                        }
                    }
                }
        }
    }

    private fun showDummyDataForSimulation() {
        binding.clCustomerData.visibilityView(true)
        binding.btnContinue.visibilityView(true)
        binding.btnXaraniIDLookUp.visibilityView(false)
        populateUserData(XaraniIdCheckData.populateDummyCustomerData())
    }

    private fun populateUserData(data: XaraniIdCheckData) {
        binding.apply {
            etDob.setText(data.dob)
            etFname.setText(data.firstName)
            etSurname.setText(data.lastName)
            spinnerGender.setText(data.genderName)
            gId = data.genderId.toString()
            etIdNo.setText(data.idNumber.toString())
            //
            etIdNo.isFocusable = false;
            etIdNo.isEnabled = false
            etIdNo.alpha = 1f
            etIdNo.alpha = 1f
            //
            etDob.isEnabled = false;
            etDob.alpha = 1f
            tlDob.alpha = 1f
            //
            etFname.isFocusable = false;
            etSurname.isFocusable = false;
            //
            spinnerGender.isEnabled = false;
            spinnerGender.alpha = 1f
            tlGender.alpha = 1f
        }
    }

    private fun pickDob() {
        val dateListener: DatePickerDialog.OnDateSetListener
        val myCalendar = Calendar.getInstance()
        val currYear = myCalendar[Calendar.YEAR]
        val currMonth = myCalendar[Calendar.MONTH]
        val currDay = myCalendar[Calendar.DAY_OF_MONTH]
        dateListener =
            DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = monthOfYear
                myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                var isDateOk = true
                if (currYear - year < 18) isDateOk = false else if (currYear == 18) {
                    if (currMonth - monthOfYear < 0) isDateOk = false
                    if (currMonth == monthOfYear && currDay - dayOfMonth < 0) isDateOk = false
                }
                if (isDateOk) {
                    val preferredFormat = "dd-MM-yyyy"
                    val date =
                        SimpleDateFormat(preferredFormat, Locale.US).format(myCalendar.time)
                    binding.etDob.setText(date)
                } else {
                    toastyErrors(getString(R.string.age_should_be_more_than_18years))
                }
            }
        myCalendar.add(Calendar.YEAR, -18)
        val dialog = DatePickerDialog(
            requireContext(), dateListener, myCalendar[Calendar.YEAR],
            myCalendar[Calendar.MONTH],
            myCalendar[Calendar.DAY_OF_MONTH]
        )
        dialog.datePicker.maxDate = myCalendar.timeInMillis
        dialog.show()
    }

    private fun populateGender(genderList: List<Gender>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, genderList)
        binding.spinnerGender.setAdapter(typeAdapter)
        binding.spinnerGender.keyListener = null
        binding.spinnerGender.setOnItemClickListener { parent, _, position, _ ->
            val selected: Gender = parent.adapter.getItem(position) as Gender
            binding.spinnerGender.setText(selected.name, false)
            gId = selected.id.toString()
        }
    }

    private fun populateSubBranches(subBranchList: List<SubBranchEntity>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, subBranchList)
        binding.acSubBranch.setAdapter(typeAdapter)
        binding.acSubBranch.keyListener = null
        binding.acSubBranch.setOnItemClickListener { parent, _, position, _ ->
            val selected: SubBranchEntity = parent.adapter.getItem(position) as SubBranchEntity
            binding.acSubBranch.setText(selected.name, false)
            sBranchId = selected.id.toString()
        }
    }

    private fun initializeUI() {
        handleBackButton()
        if (fromSummary == 8) {
            binding.etIdNo.isFocusable = true
        } else {
            fromSummary = -1
            binding.etIdNo.isFocusable = true

        }
        binding.ivBack.setOnClickListener { v ->
            when (args.fragmentType) {
                4 -> {
                    isFromCustomerDetails = true
                    findNavController().navigate(R.id.action_onboardCustomerDetailsFragment_to_incompleteRegDashboardFragment)
                }

                1 -> {
                    isFromCustomerDetails = false
                    findNavController().navigate(R.id.summaryFragment)
                }

                else -> {
                    isFromCustomerDetails = false
                    findNavController().navigate(R.id.action_onboardCustomerDetailsFragment_to_incompleteRegDashboardFragment)

                }

            }
        }
    }

    private fun showPickerOptionsDialog(type: String) {
        val options =
            arrayOf<CharSequence>("Take Photo", "Choose From Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Option")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    dialog.dismiss()
                    selectFromCamera(type)
                }

                options[item] == "Choose From Gallery" -> {
                    dialog.dismiss()
                    selectFromGallery(type)
                }

                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun selectFromGallery(type: String) {
        imagePicker.pickFromStorage { imageResult ->
            imageCallBack(
                imageResult, type, "Gallery"
            )
        }
    }

    private fun selectFromCamera(type: String) {
        imagePicker.takeFromCamera { imageResult ->
            imageCallBack(imageResult, type, "Camera")
        }
    }

    //CallBack for result
    private fun imageCallBack(imageResult: ImageResult<Uri>, type: String, from: String) {
        when (imageResult) {
            is ImageResult.Success -> {
                val uri = imageResult.value
                //Log.d("TAG", "imageCallBack: $absolutePath")
                Log.d("TAG", "imageCallBack: $uri")
                if (type == "PassportPhoto") {
                    //  if (isFromIncomplete) {
                    //passportImageName = getFileName2(uri, requireContext())
                    passportImageName = generateUniqueDocName(
                        binding.etIdNo.text.toString(),
                        profilePicCode
                    )
                    passportPhotoUri = uri
                    if (customerImagePassport.isNotEmpty()) {
                        customerFaceID.docPath = passportImageName
                        Log.i("TAG", "imageCallBack: ${Gson().toJson(customerFaceID)}")
                        customerDocs.mapInPlace {
                            /**IF DOCCODE FROM THE LIST is equal to the doccode for front id, and path at that items has changedd
                             *  we replace the element /value
                             * with the new  vale, else if the element has not been modified, we retain it*/
                                element ->
                            if (element.docCode == profilePicCode) customerFaceID else element
                        }
                    } else {
                        val generatedUUID = UUID.randomUUID().toString()
                        val customerDocsEntity = CustomerDocsEntity(
                            0,
                            binding.etIdNo.text.toString(),
                            profilePicCode,
                            generatedUUID,
                            passportImageName
                        )
                        customerDocs.add(customerDocsEntity)
                    }
                    binding.tvAttachPassport.text =
                        "Passport Size Photo - $passportImageName"
                } else {
                    //   if (isFromIncomplete) {
                    frontIDImageName = generateUniqueDocName(
                        binding.etIdNo.text.toString(),
                        frontIDCode
                    )
                    frontIDUri = uri
                    if (customerImage.isNotEmpty()) {
                        customerFrontID.docPath = frontIDImageName
                        Log.i("TAG", "imageCallBack: ${Gson().toJson(customerFrontID)}")
                        customerDocs.mapInPlace {
                            /**IF DOCCODE FROM THE LIST is equal to the doccode for front id, and path at that items has changedd
                             *  we replace the element /value
                             * with the new  vale, else if the element has not been modified, we retain it*/
                                element ->
                            if (element.docCode == frontIDCode) customerFrontID else element
                        }
                    } else {
                        val generatedUUID = UUID.randomUUID().toString()
                        val customerDocsEntity = CustomerDocsEntity(
                            0,
                            binding.etIdNo.text.toString(),
                            frontIDCode,
                            generatedUUID,
                            frontIDImageName
                        )
                        customerDocs.add(customerDocsEntity)
                    }
                    binding.tvAttachFrontIDDoc.text =
                        "Customer Front National ID - $frontIDImageName"

                }

            }

            is ImageResult.Failure -> {
                val errorString = imageResult.errorString
                Toasty.error(requireContext(), errorString, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateYesFields(): Boolean {
        var isValid = false
        binding.apply {
            val fName = etFname.text.toString().trim()
            val lName = etSurname.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val id = etIdNo.text.toString().trim()
            val datob = etDob.text.toString().trim()
            val sName = etSpName.text.toString().trim()
            val sGender = spinnerGender.text.toString().trim()
            val subBranch = acSubBranch.text.toString().trim()
            val validMsg = FieldValidators.VALIDINPUT
            phoneNumber = FieldValidators().formatPhoneNumber(binding.etSpousePhone.text.toString())
            val validPhone = FieldValidators().validPhoneNUmber(phoneNumber)
            when {
                fName.isEmpty() -> {
                    tlFname.error = getString(R.string.required)
                    isValid = false
                }

                lName.isEmpty() -> {
                    tlFname.error = ""
                    tlFname.clearFocus()
                    tlSname.error = getString(R.string.required)
                    isValid = false
                }

                id.length < 8 -> {
                    tlEmail.error = ""
                    tlIdNo.error = "Invalid ID number"
                    isValid = false
                }

                datob.isEmpty() -> {
                    tlIdNo.error = ""
                    tlIdNo.clearFocus()
                    tlDob.error = getString(R.string.required)
                    isValid = false
                }

                sGender.isEmpty() -> {
                    tlDob.error = ""
                    toastyErrors(getString(R.string.select_gender))
                    isValid = false
                }

                sName.isEmpty() -> {
                    tlSpouseName.error = getString(R.string.required)
                    isValid = false
                }

                subBranch.isEmpty() -> {
                    tiSubBranch.error = getString(R.string.required)
                    isValid = false
                }

                !validPhone.contentEquals(validMsg) -> {
                    isValid = false
                    etSpousePhone.requestFocus()
                    tlSpouseName.error = ""
                    tlSpousePhone.error = validPhone
                }

                binding.tvAttachFrontIDDoc.text.toString() == resources.getString(R.string.attach_customer_front_id) -> {
                    isValid = false
                    toastyErrors(getString(R.string.attach_customer_front_id))
                }

                binding.tvAttachPassport.text.toString() == resources.getString(R.string.attach_passport_photo_size) -> {
                    isValid = false
                    toastyErrors(getString(R.string.attach_passport_photo_size))
                }

                else -> {
                    isValid = true
                    tlFname.error = ""
                    tlSname.error = ""
                    tlIdNo.error = ""
                    tlEmail.error = ""
                    tlDob.error = ""
                    tlSpouseName.error = ""
                    tlSpousePhone.error = ""
                    tiSubBranch.error = ""
                    //binding.btnContinue.isEnabled = false
                    viewmodel.cIdNumber.postValue(binding.etIdNo.text.toString())

                }
            }
        }
        return isValid
    }

    private fun validateNoFields(): Boolean {
        var isValid = false
        binding.apply {
            val fName = etFname.text.toString().trim()
            val lName = etSurname.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val id = etIdNo.text.toString().trim()
            val datob = etDob.text.toString().trim()
            val sGender = spinnerGender.text.toString().trim()
            val subBranch = acSubBranch.text.toString().trim()

            when {
                fName.isEmpty() -> {
                    tlFname.error = getString(R.string.required)
                    isValid = false
                }

                lName.isEmpty() -> {
                    tlFname.error = ""
                    tlFname.clearFocus()
                    tlSname.error = getString(R.string.required)
                    isValid = false
                }

                id.length < 8 -> {
                    tlEmail.error = ""
                    tlIdNo.error = "Invalid ID number"
                    isValid = false
                }

                subBranch.isEmpty() -> {
                    tiSubBranch.error = getString(R.string.required)
                    isValid = false
                }

                datob.isEmpty() -> {
                    tlIdNo.error = ""
                    tlIdNo.clearFocus()
                    tlDob.error = getString(R.string.required)
                    isValid = false
                }

                sGender.isEmpty() -> {
                    tlDob.error = ""
                    toastyErrors(getString(R.string.select_gender))
                    isValid = false
                }

                binding.tvAttachFrontIDDoc.text.toString() == resources.getString(R.string.attach_customer_front_id) -> {
                    isValid = false
                    toastyErrors(getString(R.string.attach_customer_front_id))
                }

                binding.tvAttachPassport.text.toString() == resources.getString(R.string.attach_passport_photo_size) -> {
                    isValid = false
                    toastyErrors(getString(R.string.attach_passport_photo_size))
                }

                else -> {
                    isValid = true
                    tlFname.error = ""
                    tlSname.error = ""
                    tlIdNo.error = ""
                    tlEmail.error = ""
                    tlDob.error = ""
                    tiSubBranch.error = ""
                    viewmodel.cIdNumber.postValue(binding.etIdNo.text.toString())
                }
            }
        }
        return isValid

    }

    private fun saveCustomerFullDatLocally(customerDetailsEntity: CustomerDetailsEntity) {
        /*customerDocs.forEach { customerDoc ->
            customerDoc.parentNationalIdentity = binding.etIdNo.text.toString()
        }*/
        Log.d("TAG", "saveCustomerFullDatLocally: ${customerDocs.size}")
        viewmodel.insertCustomerFullDetails(
            customerDetailsEntity,
            guarantor,
            collateral,
            otherBorrowing, household, customerDocs
        )
    }

    private fun updateCustomerNationalID(
        newNationalID: String, firstName: String,
        lastName: String,
        alias: String,
        email: String,
        subBranchID: String,
        dob: String,
        genderId: String,
        genderName: String,
        spouseName: String?,
        spousePhone: String?
    ) {
        /*customerDocs.forEach { customerDoc ->
            customerDoc.parentNationalIdentity = binding.etIdNo.text.toString()
        }*/
        Log.d("TAG", "saveCustomerFullDatLocally: ${customerDocs.size}")
        viewmodel.updateCustomerNationalID(
            oldNationalID,
            newNationalID,
            firstName,
            lastName,
            alias,
            email,
            subBranchID,
            dob,
            genderId,
            genderName,
            spouseName,
            spousePhone
        )
    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    when (args.fragmentType) {
                        4 -> {
                            isFromCustomerDetails = true
                            findNavController().navigate(R.id.action_onboardCustomerDetailsFragment_to_incompleteRegDashboardFragment)
                        }

                        1 -> {
                            isFromCustomerDetails = false
                            findNavController().navigate(R.id.summaryFragment)
                        }

                        else -> {
                            isFromCustomerDetails = false
                            findNavController().navigate(R.id.action_onboardCustomerDetailsFragment_to_incompleteRegDashboardFragment)
                        }
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun getSavedItemsFromRoom(parentNationalId: String) {
        Log.d("TAG", "parentNationalId:$parentNationalId ")
        viewmodel.getCustomerDetails(parentNationalId)
        viewmodel.customerDetails.observe(viewLifecycleOwner) { customerDetais ->
            customerDetais.let {
                if (it != null) {
                    Log.d("TAG", "getSavedItemsFromRoomDebug: ${Gson().toJson(it)}")
                    Log.d("TAG", "getSavedItemsFromRoomDebugC: ${Gson().toJson(it.collateral)}")
                    Log.d("TAG", "getSavedItemsFromRoomDebugG: ${Gson().toJson(it.guarantors)}")
                    Log.d("TAG", "getSavedItemsFromRoomDebugO: ${Gson().toJson(it.otherBorrowing)}")
                    binding.apply {
                        maxColateral = it.customerDetails.maximumColateral
                        maxGuarantor = it.customerDetails.maximumGuarantor
                        if (it.customerDetails.firstName.isNotEmpty()) {
                            etFname.setText(it.customerDetails.firstName)
                        }
                        if (it.customerDetails.lastName.isNotEmpty()) {
                            etSurname.setText(it.customerDetails.lastName)
                        }
                        if (it.customerDetails.alias.isNotEmpty()) {
                            etAka.setText(it.customerDetails.alias)
                        }
                        if (it.customerDetails.email.isNotEmpty()) {
                            etEmail.setText(it.customerDetails.email)
                        }
                        if (it.customerDetails.dob.isNotEmpty()) {
                            etDob.setText(it.customerDetails.dob)
                        }
                        if (it.customerDetails.nationalIdentity.isNotEmpty()) {
                            etIdNo.setText(it.customerDetails.nationalIdentity)
                            oldNationalID = it.customerDetails.nationalIdentity
                        }
                        if (it.customerDetails.genderName.isNotEmpty()) {
                            spinnerGender.setText(it.customerDetails.genderName, false)
                        }
                        if (it.customerDetails.genderId.isNotEmpty()) {
                            gId = it.customerDetails.genderId
                        }
                        if (it.customerDetails.spouseName.isNotEmpty()) {
                            etSpName.setText(it.customerDetails.spouseName)
                        }
                        if (it.customerDetails.spousePhone.isNotEmpty()) {
                            etSpousePhone.setText(it.customerDetails.phone)
                            phoneNumber = it.customerDetails.phone
                        }
                        if (it.customerDetails.customerNumber.isNotEmpty()) {
                            customerDetailValue = it.customerDetails.customerNumber
                        }
                        if (it.customerDetails.phone.isNotEmpty()) {
                            customerPhone = it.customerDetails.phone
                        }
                        if (it.customerDetails.completion != null) {
                            isComp = it.customerDetails.completion
                            Log.e("TAG", "getSavedItemsFromRoomC: $isComp")
                            Log.e(
                                "TAG",
                                "getSavedItemsFromRoomISC: ${it.customerDetails.completion}"
                            )
                        }
                        if (it.customerDetails.isButtonChecked != null) {
                            if (it.customerDetails.isButtonChecked) {
                                binding.rbMyself.isChecked = true
                                binding.rbOthers.isChecked = false

                            } else {
                                binding.rbMyself.isChecked = false
                                binding.rbOthers.isChecked = true
                            }
                            isButtonCheck = it.customerDetails.isButtonChecked

                        }
                        customerDetailsEntity = it.customerDetails
                        guarantor.clear()
                        guarantor.addAll(it.guarantors)
                        otherBorrowing.clear()
                        otherBorrowing.addAll(it.otherBorrowing)
                        household.clear()
                        household.addAll(it.householdMember)
                        customerDocs.clear()
                        customerDocs.addAll(it.customerDocs)
                        //customerDocs.addAll(customerDocs.filter { customerDoc -> customerDoc.parentNationalIdentity == parentNationalId })
                        collateral.clear()
                        collateral.addAll(it.collateral)
                        Log.e(
                            "TAG",
                            "customerDocsEntity Phase 1: ${Gson().toJson(it.customerDocs)}"
                        )
                        customerImage.clear()
                        val frontIDImage =
                            it.customerDocs.filter { customerDocsEntity -> customerDocsEntity.docCode == frontIDCode }
                        customerImage.addAll(frontIDImage)
                        customerImagePassport.clear()
                        val passportImage =
                            it.customerDocs.filter { customerDocsEntity -> customerDocsEntity.docCode == profilePicCode }
                        customerImagePassport.addAll(passportImage)
                        Log.e("TAG", "customerDocsEntity Phase 2: ${Gson().toJson(customerImage)}")
                        Log.e(
                            "TAG",
                            "customerDocsEntity Phase 3: ${Gson().toJson(customerImagePassport)}"
                        )
                        if (it.customerDocs.isNotEmpty()) {
                            if (customerImage.isNotEmpty()) {
                                customerFrontID = customerImage.first()
                                if (customerFrontID.docPath.isNotEmpty()) {
                                    val uri = Uri.fromFile(File(customerFrontID.docPath))
                                    val imageName = getFileName2(uri, requireContext())
                                    if (pattern.containsMatchIn(customerFrontID.docPath)) {
                                        binding.tvAttachFrontIDDoc.text = "View Front National ID"
                                    } else {
                                        Log.e("TAG", "uri: $imageName")
                                        binding.tvAttachFrontIDDoc.text =
                                            "Customer Front National ID - $imageName"
                                    }
                                } else {
                                    binding.tvAttachFrontIDDoc.text =
                                        resources.getString(R.string.attach_customer_front_id)
                                }
                            } else {
                                binding.tvAttachFrontIDDoc.text =
                                    resources.getString(R.string.attach_customer_front_id)
                            }
                            if (customerImagePassport.isNotEmpty()) {
                                customerFaceID = customerImagePassport.first()
                                Log.e("TAG", "customerFaceID2: ${Gson().toJson(customerFaceID)}")
                                if (customerFaceID.docPath.isNotEmpty()) {
                                    val uriFace =
                                        Uri.fromFile(File(customerFaceID.docPath))
                                    val imageFace = getFileName2(uriFace, requireContext())
                                    if (pattern.containsMatchIn(customerFaceID.docPath)) {
                                        Log.d(
                                            "TAG",
                                            "getSavedItemsFromRoomPath: ${customerImagePassport.first().docPath}"
                                        )
                                        binding.tvAttachPassport.text = "View Passport Size Photo"
                                    } else {
                                        Log.d("TAG", "getSavedItemsFromRoom URI: $imageFace")
                                        binding.tvAttachPassport.text =
                                            "Passport Size Photo- $imageFace"
                                    }

                                    Log.e("TAG", "uri2: $imageFace")

                                } else {
                                    binding.tvAttachPassport.text =
                                        resources.getString(R.string.attach_passport_photo_size)
                                }
                            } else {
                                binding.tvAttachPassport.text =
                                    resources.getString(R.string.attach_passport_photo_size)
                            }
                        }


                    }
                } else {
                    binding.apply {
                        etFname.setText(null)
                        etSurname.setText(null)
                        etAka.setText(null)
                        etEmail.setText(null)
                        etDob.setText(null)
                        etIdNo.setText(null)
                        spinnerGender.setText(null)
                        gId = ""
                        phoneNumber = ""
                        customerDetailValue = ""
                        etSpName.setText(null)
                        etSpousePhone.setText(null)
                        etSpName.setText(null)
                        isComp = "0"
                        binding.rbMyself.isChecked = true
                        //  binding.rbOthers.isChecked = false
                        /*guarantor = arrayListOf()
                        collateral = arrayListOf()
                         customerDocs = arrayListOf()
                          household = arrayListOf()*/
                        customerDetailsEntity = CustomerDetailsEntity()
                        customerImage.clear()
                        customerImagePassport.clear()
                        binding.tvAttachFrontIDDoc.text =
                            getString(R.string.attach_customer_front_id)
                        binding.tvAttachPassport.text =
                            getString(R.string.attach_passport_photo_size)
                    }

                }
            }

        }

    }

    private fun showEditPhotoDialog(customerDocsEntity: CustomerDocsEntity) {
        cardBinding = EditPhotsDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        cardBinding.apply {

            if (customerDocsEntity.docCode == profilePicCode) {
                Glide.with(requireActivity()).load(customerDocsEntity.docPath)
                    .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                    .into(userLogo)
                tvTitle.text = "Passport Photo Size"
                tvEdit.setOnClickListener {
                    dialog.dismiss()
                    showPickerOptionsDialog("PassportPhoto")
                }
            } else {
                Glide.with(requireActivity()).load(customerDocsEntity.docPath)
                    .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                    .into(userLogo)
                tvTitle.text = "Customer Front ID"
                tvEdit.setOnClickListener {
                    dialog.dismiss()
                    showPickerOptionsDialog("CustomerID")
                }
            }
            cardBinding.userLogo.setOnClickListener {
                val mBuilder: AlertDialog.Builder =
                    AlertDialog.Builder(context, R.style.WrapContentDialog)
                val mView: View =
                    layoutInflater.inflate(R.layout.preview_image, null)
                val ivImagePreview = mView.findViewById<PhotoView>(R.id.iv_preview_image)
                Glide.with(requireActivity()).load(customerDocsEntity.docPath)
                    .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                    .into(ivImagePreview)
                mBuilder.setView(mView)
                val mDialog: AlertDialog = mBuilder.create()
                mDialog.show()
            }

            ImageCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.setContentView(cardBinding.root)
        dialog.show()

    }
}