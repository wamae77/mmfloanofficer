package com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.deefrent.rnd.common.abstractions.BaseDaggerFragment
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.EditPhotsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentCustomerAdditionalDetailsBinding
import com.deefrent.rnd.fieldapp.network.models.EducationLevel
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.educationDocCode
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.fromSummary
import com.deefrent.rnd.fieldapp.viewModels.DropdownItemsViewModel
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import dev.ronnie.github.imagepicker.ImagePicker
import dev.ronnie.github.imagepicker.ImageResult
import es.dmoral.toasty.Toasty
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class Step12CustomerAdditionalDetailsFragment : BaseDaggerFragment() {
    private lateinit var binding: FragmentCustomerAdditionalDetailsBinding
    private lateinit var cardBinding: EditPhotsDialogBinding
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private var eduId = ""
    private var identifierId = ""
    private var empId = ""
    private var guarantor = arrayListOf<Guarantor>()
    private var collateral = arrayListOf<Collateral>()
    private var otherBorrowing = arrayListOf<OtherBorrowing>()
    private var household = arrayListOf<HouseholdMemberEntity>()
    lateinit var imagePicker: ImagePicker
    private lateinit var nationaid: String
    private var customerDocs: ArrayList<CustomerDocsEntity> = arrayListOf()
    private var customerCert: ArrayList<CustomerDocsEntity> = arrayListOf()
    private lateinit var customerEducationCertificate: CustomerDocsEntity
    private var imageUrl = ""
    private var docType = ""
    private var educationDocImageName = ""
    private var educationDocUri: Uri? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewmodel by lazy {
        ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(OnboardCustomerViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCustomerAdditionalDetailsBinding.inflate(layoutInflater)

        binding.apply {
            tvAttachDoc.setOnClickListener {
                if (tvAttachDoc.text.contains(getString(R.string.view_edu_certificate))) {
                    Log.d("TAG", "imageUrl:$imageUrl, $docType")
                    showEditPhotoDialog(imageUrl, docType)
                } else {
                    showPickerOptionsDialog()
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackButton()
        viewmodel.cIdNumber.observe(viewLifecycleOwner) { customerIDNumber ->
            nationaid = customerIDNumber
            getSavedItemsFromRoom(customerIDNumber)
        }
        binding.apply {
            ivBack.setOnClickListener {
                if (fromSummary == 0) {
                    //findNavController().navigate(R.id.summaryFragment)
                    findNavController().navigate(R.id.step13EnrollFingerPrintFragmentMethod1)
                } else {
                    fromSummary = -1
                    findNavController().navigate(R.id.action_customerAdditionalDetailsFragment_to_addExpensesFragment)
                }
            }
            btnContinue.setOnClickListener {
                val noOfChildren = etBAddress.text.toString()
                val noOfDependant = etDSale.text.toString()
                val empName = spinnerStatus.text.toString()
                val identif = spinnerBsType.text.toString()
                val eduSpinner = binding.spinnerIsource.text.toString()
                if (eduSpinner.isEmpty()) {
                    toastyErrors("Education level required")
                } else if (identif.isEmpty()) {
                    toastyErrors("Select how you heard about us")
                } else if (noOfChildren.isEmpty()) {
                    tlBAddress.error = getString(R.string.required)
                } else if (noOfDependant.isEmpty()) {
                    tlDSale.error = getString(R.string.required)
                } else if (empName.isEmpty()) {
                    toastyErrors("Select employment status")
                } else {
                    Log.d("TAG", "onViewCreated: test")
                    viewmodel.customerEntityData.observe(viewLifecycleOwner) { customerD ->
                        Log.d("TAG", "satyyyyy: ${customerD.lastStep}")
                        customerD.apply {
                            lastStep = "CustomerAdditionalDetailsFragment"
                            isComplete = false
                            isProcessed = false
                            hasFinished = false
                            educationLevelId = eduId
                            howClientKnewMmfId = identifierId
                            numberOfChildren = etBAddress.text.toString()
                            numberOfDependants = etDSale.text.toString()
                            employmentStatusId = empId
                            educationLevel = binding.spinnerIsource.text.toString()
                            howClientKnewMmf = binding.spinnerBsType.text.toString()
                            employmentStatus = binding.spinnerStatus.text.toString()
                            viewmodel.customerEntityData.postValue(customerD)
                            saveCustomerFullDatLocally(customerD)
                            if (educationDocUri != null) {
                                saveImageToInternalAppStorage(
                                    educationDocUri!!,
                                    requireContext(),
                                    educationDocImageName
                                )
                            }
                            val json = Gson()
                            Log.d("TAG", "initiaUI: ${json.toJson(customerD.lastStep)}")
                        }

                    }
                    if (fromSummary == 0) {
                        findNavController().navigate(R.id.summaryFragment)
                    } else {
                        fromSummary = -1
                        findNavController().navigate(R.id.action_customerAdditionalDetailsFragment_to_summaryFragment)
                        // findNavController().navigate(R.id.step13EnrollFingerPrintFragmentMethod1)
                    }
                }
            }
            dropdownItemsViewModel.getAllEduLevel().observe(viewLifecycleOwner) { eduList ->
                populateEduLevel(eduList)

            }
            dropdownItemsViewModel.getAllIdentifiers()
                .observe(viewLifecycleOwner) { identifierList ->
                    populateIdentifies(identifierList)
                }
            dropdownItemsViewModel.getEmployments().observe(viewLifecycleOwner) { emp ->
                Log.d("TAG", "onViewCreated:empList${emp} ")

                populateEmpStatus(emp)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePicker = ImagePicker(fragment = this)
    }

    private fun showPickerOptionsDialog() {
        val options =
            arrayOf<CharSequence>("Take Photo", "Choose From Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Option")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    dialog.dismiss()
                    selectFromCamera()
                }

                options[item] == "Choose From Gallery" -> {
                    dialog.dismiss()
                    selectFromGallery()
                }

                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun selectFromGallery() {
        imagePicker.pickFromStorage { imageResult ->
            imageCallBack(
                imageResult, "Gallery"
            )
        }
    }

    private fun selectFromCamera() {
        imagePicker.takeFromCamera { imageResult ->
            imageCallBack(imageResult, "Camera")
        }
    }

    //CallBack for result
    private fun imageCallBack(imageResult: ImageResult<Uri>, from: String) {
        when (imageResult) {
            is ImageResult.Success -> {
                val uri = imageResult.value
                educationDocImageName = generateUniqueDocName(
                    nationaid,
                    educationDocCode
                )
                educationDocUri = uri
                if (customerCert.isNotEmpty()) {
                    customerEducationCertificate.docPath = educationDocImageName
                    Log.i("TAG", "imageCallBack: ${Gson().toJson(customerEducationCertificate)}")
                    customerDocs.mapInPlace {
                        /**IF DOCCODE FROM THE LIST is equal to the doccode for front id, and path at that items has changedd
                         *  we replace the element /value
                         * with the new  vale, else if the element has not been modified, we retain it*/
                            element ->
                        if (element.docCode == educationDocCode) customerEducationCertificate else element
                    }
                } else {
                    val generatedUUID = UUID.randomUUID().toString()
                    val customerDocsEntity = CustomerDocsEntity(
                        0,
                        nationaid,
                        educationDocCode,
                        generatedUUID,
                        educationDocImageName
                    )
                    customerDocs.add(customerDocsEntity)
                }
                binding.tvAttachDoc.text =
                    "Education Certificate -$educationDocImageName"

            }

            is ImageResult.Failure -> {
                val errorString = imageResult.errorString
                Toasty.error(requireContext(), errorString, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun populateEduLevel(eduList: List<EducationLevel>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, eduList)
        binding.spinnerIsource.setAdapter(typeAdapter)
        binding.spinnerIsource.keyListener = null
        binding.spinnerIsource.setOnItemClickListener { parent, _, position, _ ->
            val selected: EducationLevel = parent.adapter.getItem(position) as EducationLevel
            binding.spinnerIsource.setText(selected.name, false)
            eduId = selected.id.toString()
        }
    }

    private fun populateIdentifies(identifyList: List<IdentifyEntity>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, identifyList)
        binding.spinnerBsType.setAdapter(typeAdapter)
        binding.spinnerBsType.keyListener = null
        binding.spinnerBsType.setOnItemClickListener { parent, _, position, _ ->
            val selected: IdentifyEntity = parent.adapter.getItem(position) as IdentifyEntity
            binding.spinnerBsType.setText(selected.name, false)
            identifierId = selected.id.toString()
        }
    }

    private fun populateEmpStatus(empStatusList: List<EmploymentEntity>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, empStatusList)
        binding.spinnerStatus.setAdapter(typeAdapter)
        binding.spinnerStatus.keyListener = null
        binding.spinnerStatus.setOnItemClickListener { parent, _, position, _ ->
            val selected: EmploymentEntity = parent.adapter.getItem(position) as EmploymentEntity
            binding.spinnerStatus.setText(selected.name, false)
            empId = selected.id.toString()
        }
    }

    private fun saveCustomerFullDatLocally(customerDetailsEntity: CustomerDetailsEntity) {
        viewmodel.insertCustomerFullDetails(
            customerDetailsEntity,
            guarantor,
            collateral,
            otherBorrowing,
            household, customerDocs
        )
    }

    private fun getSavedItemsFromRoom(parentNationalId: String) {
        viewmodel.fetchCustomerDetails(parentNationalId).observe(viewLifecycleOwner) {
            Log.d("TAG", "getSavedItemsFromRoomDebug2: ${Gson().toJson(it)}")
            Log.d("TAG", "getSavedItemsFromRoomDebugC2: ${Gson().toJson(it.collateral)}")
            Log.d("TAG", "getSavedItemsFromRoomDebugG2: ${Gson().toJson(it.guarantors)}")
            Log.d("TAG", "getSavedItemsFromRoomDebugO2: ${Gson().toJson(it.otherBorrowing)}")
            binding.apply {
                if (it.customerDetails.educationLevel.isNotEmpty()) {
                    spinnerIsource.setText(it.customerDetails.educationLevel, false)
                }
                if (it.customerDetails.howClientKnewMmf.isNotEmpty()) {
                    spinnerBsType.setText(it.customerDetails.howClientKnewMmf, false)
                }
                if (it.customerDetails.numberOfChildren.isNotEmpty()) {
                    etBAddress.setText(it.customerDetails.numberOfChildren)
                }
                if (it.customerDetails.numberOfDependants.isNotEmpty()) {
                    etDSale.setText(it.customerDetails.numberOfDependants)
                }
                if (it.customerDetails.employmentStatus.isNotEmpty()) {
                    spinnerStatus.setText(it.customerDetails.employmentStatus, false)
                }
                if (it.customerDetails.educationLevelId.isNotEmpty()) {
                    eduId = (it.customerDetails.educationLevelId)
                }
                if (it.customerDetails.howClientKnewMmfId.isNotEmpty()) {
                    identifierId = (it.customerDetails.howClientKnewMmfId)
                }
                if (it.customerDetails.employmentStatusId.isNotEmpty()) {
                    empId = (it.customerDetails.employmentStatusId)
                }
                customerDocs = it.customerDocs as ArrayList<CustomerDocsEntity>
                collateral = it.collateral as ArrayList<Collateral>
                guarantor = it.guarantors as ArrayList<Guarantor>
                otherBorrowing = it.otherBorrowing as ArrayList<OtherBorrowing>
                household = it.householdMember as ArrayList<HouseholdMemberEntity>
                Log.e("TAG", "customerDocsEntity Phase 1: ${Gson().toJson(it.customerDocs)}")
                customerCert.clear()
                val certDoc =
                    it.customerDocs.filter { customerDocsEntity -> customerDocsEntity.docCode == educationDocCode }
                customerCert.addAll(certDoc)
                if (customerCert.isNotEmpty()) {
                    customerEducationCertificate = customerCert.first()
                    if (customerEducationCertificate.docPath.isNotEmpty()) {
                        val uri = Uri.fromFile(File(customerEducationCertificate.docPath))
                        val imageName = getFileName(uri, requireActivity())
                        if (Constants.pattern.containsMatchIn(customerEducationCertificate.docPath)) {
                            binding.tvAttachDoc.text = getString(R.string.view_edu_certificate)
                            imageUrl = customerEducationCertificate.docPath
                            docType = customerEducationCertificate.docCode
                        } else {
                            Log.e("TAG", "uri: $imageName")
                            binding.tvAttachDoc.text =
                                "Education Certificate - $imageName"
                        }
                    } else {
                        binding.tvAttachDoc.text =
                            resources.getString(R.string.attach_education_certificate)
                    }
                } else {
                    binding.tvAttachDoc.text =
                        resources.getString(R.string.attach_education_certificate)
                }

            }
        }

    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    if (fromSummary == 0) {
                        findNavController().navigate(R.id.summaryFragment)
                    } else {
                        fromSummary = -1
                        findNavController().navigate(R.id.action_customerAdditionalDetailsFragment_to_addExpensesFragment)
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun showEditPhotoDialog(uri: String, docType: String) {
        cardBinding = EditPhotsDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        cardBinding.apply {
            Glide.with(requireActivity()).load(uri)
                .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                .into(userLogo)
            tvTitle.text = "Education Certificate"
            tvEdit.setOnClickListener {
                dialog.dismiss()
                Log.d("TAG", "showEditPhotoDialog: $docType")
                showPickerOptionsDialog()
            }
            cardBinding.userLogo.setOnClickListener {
                val mBuilder: AlertDialog.Builder =
                    AlertDialog.Builder(context, R.style.WrapContentDialog)
                val mView: View =
                    layoutInflater.inflate(R.layout.preview_image, null)
                val ivImagePreview = mView.findViewById<PhotoView>(R.id.iv_preview_image)
                Glide.with(requireActivity()).load(uri)
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