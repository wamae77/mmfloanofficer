package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.assessment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.deefrent.rnd.common.utils.Constants.isFromLocal
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.EditPhotsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentAssessCustomerDetailsBinding
import com.deefrent.rnd.fieldapp.network.models.Gender
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.frontIDCode
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.pattern
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.profilePicCode
import com.deefrent.rnd.fieldapp.viewModels.AssessmentDashboardViewModel
import com.deefrent.rnd.fieldapp.viewModels.DropdownItemsViewModel
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import dev.ronnie.github.imagepicker.ImagePicker
import dev.ronnie.github.imagepicker.ImageResult
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AssessCustomerDetailsFragment : Fragment() {
    private lateinit var binding: FragmentAssessCustomerDetailsBinding
    private lateinit var cardBinding: EditPhotsDialogBinding
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(AssessmentDashboardViewModel::class.java)
    }
    private var gendeId = ""
    private var sBranchId = ""
    private var phoneNumber = ""
    private var customerphone = ""
    private lateinit var compStatus: String
    private var cNumber = ""
    private lateinit var assessCustomer: AssessCustomerEntity
    private var guarantor = arrayListOf<AssessGuarantor>()
    private var collateral = arrayListOf<AssessCollateral>()
    private var household = arrayListOf<AssessHouseholdMemberEntity>()
    private var otherBorrowing = arrayListOf<AssessBorrowing>()
    private var customerDocs = arrayListOf<AssessCustomerDocsEntity>()
    private lateinit var customerFrontID: AssessCustomerDocsEntity
    private lateinit var customerFaceID: AssessCustomerDocsEntity
    private var passportImageSizeList = arrayListOf<AssessCustomerDocsEntity>()
    private var frontIDImageList = arrayListOf<AssessCustomerDocsEntity>()
    lateinit var imagePicker: ImagePicker
    private var frontIDImageName = ""
    private var passportImageName = ""
    private var frontIDUri: Uri? = null

    private var passportPhotoUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePicker = ImagePicker(fragment = this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAssessCustomerDetailsBinding.inflate(layoutInflater)
        initializeUI()
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
        binding.rbMyself.isChecked = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackButton()
        Log.e("TAG", "onViewCreated: $isFromLocal")
        binding.apply {
            rbMyself.isChecked = true
            viewmodel.parentId.observe(viewLifecycleOwner) { nationalId ->
                getAssessmentDetailsFromRoom(nationalId)
            }

            rbMyself.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    clOption.makeVisible()
                    rbOthers.isChecked = false
                }
            }
            rbOthers.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    rbMyself.isChecked = false
                    clOption.makeGone()
                }

            }
        }
        binding.etDob.setOnClickListener { pickDob() }
        binding.apply {
            /**gender spinner impl*/
            dropdownItemsViewModel.getAllGender().observe(viewLifecycleOwner) {
                Log.d("TAG", "onViewCreated: $it")
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
            btnContinue.setOnClickListener {
                if (rbMyself.isChecked) {
                    if (validateYesFields()) {
                        assessCustomer.apply {
                            binding.apply {
                                lastStep = "AssessCustomerDetailsFragment"
                                isComplete = false
                                hasFinished = false
                                isProcessed = false
                                phone = customerphone
                                assessmentPercentage = compStatus
                                customerNumber = cNumber
                                firstName = etFname.text.toString()
                                lastName = etSurname.text.toString()
                                alsoKnownAs = etAka.text.toString()
                                idNumber = etIdNo.text.toString()
                                dob = etDob.text.toString()
                                gender = spinnerGender.text.toString()
                                genderId = gendeId
                                emailAddress = etEmail.text.toString()
                                firstName = etFname.text.toString()
                                spouseName = etSpName.text.toString()
                                spousePhone = etSpousePhone.text.toString()
                                isMarried = true
                                subBranchId = sBranchId
                            }

                        }
                        saveAssessmentDataLocally(assessCustomer)
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
                        viewmodel.assessCustomerEntity.postValue(assessCustomer)
                        findNavController().navigate(R.id.action_assessCustomerDetailsFragment_to_assessBusinesDetailsFragment)
                    }
                } else {
                    if (validateNoFields()) {
                        assessCustomer.apply {
                            binding.apply {
                                lastStep = "AssessCustomerDetailsFragment"
                                isComplete = false
                                hasFinished = false
                                isProcessed = false
                                assessmentPercentage = compStatus
                                phone = customerphone
                                customerNumber = cNumber
                                firstName = etFname.text.toString()
                                lastName = etSurname.text.toString()
                                alsoKnownAs = etAka.text.toString()
                                idNumber = etIdNo.text.toString()
                                dob = etDob.text.toString()
                                gender = spinnerGender.text.toString()
                                genderId = gendeId
                                emailAddress = etEmail.text.toString()
                                firstName = etFname.text.toString()
                                isMarried = false
                                subBranchId = sBranchId
                            }

                        }
                        viewmodel.assessCustomerEntity.postValue(assessCustomer)
                        saveAssessmentDataLocally(assessCustomer)
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
                        findNavController().navigate(R.id.action_assessCustomerDetailsFragment_to_assessBusinesDetailsFragment)

                    }
                }

            }
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

    //CallBack for result
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

    private fun imageCallBack(imageResult: ImageResult<Uri>, type: String, from: String) {
        when (imageResult) {
            is ImageResult.Success -> {
                val uri = imageResult.value
                if (type == "PassportPhoto") {
                    passportImageName = generateUniqueDocName(
                        binding.etIdNo.text.toString(),
                        profilePicCode
                    )
                    passportPhotoUri = uri
                    if (passportImageSizeList.isNotEmpty()) {
                        customerFaceID.docPath = passportImageName
                        Log.i("TAG", "imageCallBack: ${Gson().toJson(customerFaceID)}")
                        customerDocs.mapInPlace { element ->
                            if (element.docCode == profilePicCode) customerFaceID else element
                        }
                    } else {
                        val generatedUUID = UUID.randomUUID().toString()
                        val customerDocsEntity = AssessCustomerDocsEntity(
                            0,
                            binding.etIdNo.text.toString(),
                            profilePicCode,
                            generatedUUID,
                            passportImageName
                        )
                        customerDocs.add(customerDocsEntity)
                    }
                    binding.tvAttachPassport.text =
                        "Passport Photo Size - $passportImageName"
                } else {
                    frontIDImageName = generateUniqueDocName(
                        binding.etIdNo.text.toString(),
                        frontIDCode
                    )
                    frontIDUri = uri
                    if (frontIDImageList.isNotEmpty()) {
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
                        val customerDocsEntity = AssessCustomerDocsEntity(
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
                Toast.makeText(requireContext(), errorString, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun selectFromCamera(type: String) {
        imagePicker.takeFromCamera { imageResult ->
            imageCallBack(imageResult, type, "Camera")

        }
    }

    private fun selectFromGallery(type: String) {
        imagePicker.pickFromStorage { imageResult ->
            imageCallBack(imageResult, type, "Gallery")

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
                    binding.tlDob.error = getString(R.string.age_should_be_more_than_18years)
                }
            }
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
            gendeId = selected.id.toString()
        }

    }

    private fun initializeUI() {
        binding.ivBack.setOnClickListener { v ->
            //Navigation.findNavController(v).navigateUp()
            findNavController().navigate(R.id.incompleteAssesmentFragment)
        }
    }

    private fun validateYesFields(): Boolean {
        var isValid = false
        binding.apply {
            val fName = etFname.text.toString().trim()
            val lName = etSurname.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val id = etIdNo.text.toString().trim()
            val spGender = spinnerGender.text.toString().trim()
            val spSubBranch = acSubBranch.text.toString().trim()

            val datob = etDob.text.toString().trim()
            val sName = etSpName.text.toString().trim()
            val validMsg = FieldValidators.VALIDINPUT
            phoneNumber = FieldValidators().formatPhoneNumber(binding.etSpousePhone.text.toString())
            val validPhone = FieldValidators().validPhoneNUmber(phoneNumber)
            when {
                fName.isEmpty() || lName.isEmpty() || id.isEmpty() || datob.isEmpty() || spSubBranch.isEmpty() || spGender.isEmpty() || sName.isEmpty()
                        || !validPhone.contentEquals(validMsg) -> {
                    isValid = false
                    toastyInfos("Fill out all the fields to continue")

                    /*   fName.isEmpty() -> {
                       tlFname.error = getString(R.string.required)
                       isValid = false
                   }
                   lName.isEmpty() -> {
                       tlFname.error = ""
                       tlFname.clearFocus()
                       tlSname.error = getString(R.string.required)
                       isValid = false
                   }
                   id.isEmpty() -> {
                       tlEmail.error = ""
                       tlIdNo.error = getString(R.string.required)
                       isValid = false
                   }
                   datob.isEmpty() -> {
                       tlIdNo.error = ""
                       tlIdNo.clearFocus()
                       tlDob.error = getString(R.string.required)
                       isValid = false
                   }
                       spGender.isEmpty() -> {
                       tlDob.error = ""
                       toastyErrors(getString(R.string.select_gender))
                       isValid = false
                   }
                   sName.isEmpty() -> {
                       tlSpouseName.error = getString(R.string.required)
                       isValid = false
                   }
                   !validPhone.contentEquals(validMsg) -> {
                       isValid = false
                       etSpousePhone.requestFocus()
                       tlSpouseName.error = ""
                       tlSpousePhone.error = validPhone*/
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
            val spGender = spinnerGender.text.toString().trim()
            val spSubBranch = acSubBranch.text.toString().trim()
            val id = etIdNo.text.toString().trim()
            val datob = etDob.text.toString().trim()
            when {
                fName.isEmpty() || lName.isEmpty() || id.isEmpty() || datob.isEmpty() || spGender.isEmpty() || spSubBranch.isEmpty() -> {
                    isValid = false
                    toastyInfos("Fill out all the fields to continue")
                }

                binding.tvAttachFrontIDDoc.text.toString() == resources.getString(R.string.attach_customer_front_id) -> {
                    isValid = false
                    toastyErrors(getString(R.string.attach_customer_front_id))
                }

                binding.tvAttachPassport.text.toString() == resources.getString(R.string.attach_passport_photo_size) -> {
                    isValid = false
                    toastyErrors(getString(R.string.attach_passport_photo_size))
                }
                /* fName.isEmpty() -> {
                     tlFname.error=getString(R.string.required)
                     isValid=false
                 }
                 lName.isEmpty() -> {
                     tlFname.error=""
                     tlFname.clearFocus()
                     tlSname.error=getString(R.string.required)
                     isValid=false
                 }
                 id.isEmpty() -> {
                     tlEmail.error=""
                     tlIdNo.error=getString(R.string.required)
                     isValid=false
                 }
                 datob.isEmpty() -> {
                     tlIdNo.error=""
                     tlIdNo.clearFocus()
                     tlDob.error = getString(R.string.required)
                     isValid=false
                 }
                 spGender.isEmpty() -> {
                     tlDob.error=""
                     toastyErrors(getString(R.string.select_gender))

                 }*/
                else -> {
                    isValid = true
                    tlFname.error = ""
                    tlSname.error = ""
                    tlIdNo.error = ""
                    tlEmail.error = ""
                    tlDob.error = ""
                }
            }
        }
        return isValid

    }

    private fun saveAssessmentDataLocally(assessCustomerEntity: AssessCustomerEntity) {
        viewmodel.insertAssessmentData(
            assessCustomerEntity,
            customerDocs,
            collateral,
            guarantor,
            otherBorrowing, household
        )
    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    //findNavController().navigateUp()
                    //findNavController().navigate(R.id.customerDetailsAssessmentFragment)
                    findNavController().navigate(R.id.incompleteAssesmentFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun getAssessmentDetailsFromRoom(parentId: String) {
        binding.apply {
            viewmodel.fetchCustomerDetails(parentId).observe(viewLifecycleOwner) {
                assessCustomer = it.assessCustomerEntity
                Log.e("TAG", "fetchCustomerDetails: ${Gson().toJson(it)}")
                guarantor.clear()
                guarantor.addAll(it.assessGua)
                otherBorrowing.clear()
                otherBorrowing.addAll(it.assessBorrow)
                household.clear()
                household.addAll(it.householdMember)
                customerDocs.clear()
                customerDocs.addAll(it.customerDocs)
                collateral.clear()
                collateral.addAll(it.assessCollateral)
                Log.e("TAG", "assessCollateral: $collateral")
                customerphone = it.assessCustomerEntity.phone
                cNumber = it.assessCustomerEntity.customerNumber
                compStatus = it.assessCustomerEntity.assessmentPercentage
                etFname.setText(it.assessCustomerEntity.firstName)
                etSurname.setText(it.assessCustomerEntity.lastName)
                etIdNo.setText(it.assessCustomerEntity.idNumber)
                etAka.setText(it.assessCustomerEntity.alsoKnownAs)
                etDob.setText(it.assessCustomerEntity.dob)
                binding.spinnerGender.setText(it.assessCustomerEntity.gender, false)
                gendeId = it.assessCustomerEntity.genderId.toString()
                binding.acSubBranch.setText(it.assessCustomerEntity.subBranch, false)
                sBranchId = it.assessCustomerEntity.subBranchId
                etEmail.setText(it.assessCustomerEntity.emailAddress)
                if (it.assessCustomerEntity.spouseName.isNotEmpty() || it.assessCustomerEntity.spousePhone.isNotEmpty()) {
                    rbMyself.isChecked = true
                    rbOthers.isChecked = false
                    etSpName.setText(it.assessCustomerEntity.spouseName)
                    etSpousePhone.setText(it.assessCustomerEntity.spousePhone)
                } else {
                    rbMyself.isChecked = false
                    rbOthers.isChecked = true
                }

                frontIDImageList.clear()
                val frontIDImage =
                    it.customerDocs.filter { customerDocsEntity -> customerDocsEntity.docCode == frontIDCode }
                frontIDImageList.addAll(frontIDImage)
                passportImageSizeList.clear()
                val passportImage =
                    it.customerDocs.filter { customerDocsEntity -> customerDocsEntity.docCode == profilePicCode }
                passportImageSizeList.addAll(passportImage)
                Log.e(
                    "TAG",
                    "customerDocsEntity Phase 2: ${Gson().toJson(frontIDImageList)}"
                )
                Log.e(
                    "TAG",
                    "customerDocsEntity Phase 3: ${Gson().toJson(passportImageSizeList)}"
                )
                if (it.customerDocs.isNotEmpty()) {
                    if (frontIDImageList.isNotEmpty()) {
                        customerFrontID = frontIDImageList.first()
                        if (customerFrontID.docPath.isNotEmpty()) {
                            val uri = Uri.fromFile(File(customerFrontID.docPath))
                            val imageName = getFileName(uri, requireActivity())
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
                    if (passportImageSizeList.isNotEmpty()) {
                        customerFaceID = passportImageSizeList.first()
                        Log.e("TAG", "customerFaceID2: ${Gson().toJson(customerFaceID)}")
                        if (customerFaceID.docPath.isNotEmpty()) {
                            val uriFace = Uri.fromFile(File(customerFaceID.docPath))
                            val imageFace = getFileName(uriFace, requireActivity())
                            if (pattern.containsMatchIn(customerFaceID.docPath)
                            ) {
                                Log.d(
                                    "TAG",
                                    "getSavedItemsFromRoomPath: ${customerFaceID.docPath}"
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
        }
    }

    private fun showEditPhotoDialog(customerDocsEntity: AssessCustomerDocsEntity) {
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