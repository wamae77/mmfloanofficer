package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.assessment

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.EditPhotsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentCustomerAdditionalDetailsBinding
import com.deefrent.rnd.fieldapp.network.models.EducationLevel
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.educationDocCode
import com.deefrent.rnd.fieldapp.viewModels.AssessmentDashboardViewModel
import com.deefrent.rnd.fieldapp.viewModels.DropdownItemsViewModel
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import dev.ronnie.github.imagepicker.ImagePicker
import dev.ronnie.github.imagepicker.ImageResult
import es.dmoral.toasty.Toasty
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class AssessAdditionalDetailsFragment : Fragment() {
    private lateinit var binding: FragmentCustomerAdditionalDetailsBinding
    private lateinit var cardBinding: EditPhotsDialogBinding
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private var eduId = ""
    private var identifiersId = ""
    private var empId = ""
    private lateinit var nationaid: String
    private var guarantor = arrayListOf<AssessGuarantor>()
    private var collateral = arrayListOf<AssessCollateral>()
    private var household = arrayListOf<AssessHouseholdMemberEntity>()
    private var otherBorrowing = arrayListOf<AssessBorrowing>()
    private var customerDocs = arrayListOf<AssessCustomerDocsEntity>()
    private var customerCert: ArrayList<AssessCustomerDocsEntity> = arrayListOf()
    private lateinit var customerEducationCertificate: AssessCustomerDocsEntity
    private var imageUrl = ""
    private var docType = ""
    lateinit var imagePicker: ImagePicker
    private var educationDocImageName = ""
    private var educationDocUri: Uri? = null

    private val viewmodel by lazy {
        ViewModelProvider(
            requireActivity()
        ).get(AssessmentDashboardViewModel::class.java)
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
        binding.apply {
            viewmodel.parentId.observe(viewLifecycleOwner) { nationalId ->
                nationaid = nationalId
                getSavedItemsFromRoom(nationalId)
            }
            ivBack.setOnClickListener {
                findNavController().navigate(R.id.assessExpensesFragment)
            }

            btnContinue.setOnClickListener {
                val noOfChildren = etBAddress.text.toString()
                val noOfDependant = etDSale.text.toString()
                val education = spinnerIsource.text.toString()
                val identifiers = spinnerBsType.text.toString()
                val statusEmp = spinnerStatus.text.toString()

                if (education.isEmpty()) {
                    toastyErrors("Education level required")
                } else if (identifiers.isEmpty()) {
                    toastyErrors("Select how you heard about us")
                } else if (noOfChildren.isEmpty()) {
                    tlBAddress.error = getString(R.string.required)
                } else if (noOfDependant.isEmpty()) {
                    tlDSale.error = getString(R.string.required)
                } else if (statusEmp.isEmpty()) {
                    toastyErrors("Select employment status")
                } else {
                    viewmodel.assessCustomerEntity.observe(viewLifecycleOwner) { customerD ->
                        customerD.apply {
                            lastStep = "AssessAdditionalDetailsFragment"
                            isComplete = false
                            isProcessed = false
                            hasFinished = false
                            educationLevelId = eduId
                            identifier = spinnerBsType.text.toString().trim()
                            numberOfChildren = etBAddress.text.toString().trim()
                            numberOfDependants = etDSale.text.toString().trim()
                            empStatusId = empId
                            educationLevel = spinnerIsource.text.toString().trim()
                            empStatus = spinnerStatus.text.toString().trim()
                            identifierId = identifiersId
                            viewmodel.assessCustomerEntity.postValue(customerD)
                            saveAssessmentDataLocally(customerD)
                            if (educationDocUri != null) {
                                saveImageToInternalAppStorage(
                                    educationDocUri!!,
                                    requireContext(),
                                    educationDocImageName
                                )
                            }
                            viewmodel.stopObserving()
                            findNavController().navigate(R.id.summaryAnalysisFragment)
                            val json = Gson()
                            Log.d("TAG", "initiaUI: ${json.toJson(customerD)}")
                        }

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
                    val customerDocsEntity = AssessCustomerDocsEntity(
                        0,
                        nationaid, educationDocCode,
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
            identifiersId = selected.id.toString()
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


    private fun saveAssessmentDataLocally(assessCustomerEntity: AssessCustomerEntity) {
        viewmodel.insertAssessmentData(
            assessCustomerEntity, customerDocs,
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
                    findNavController().navigate(R.id.assessExpensesFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun getSavedItemsFromRoom(parentId: String) {
        binding.apply {
            viewmodel.fetchCustomerDetails(parentId).observe(viewLifecycleOwner) {
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
                eduId = it.assessCustomerEntity.educationLevelId
                identifiersId = it.assessCustomerEntity.identifierId
                empId = it.assessCustomerEntity.empStatusId
                spinnerIsource.setText(it.assessCustomerEntity.educationLevel.trim(), false)
                spinnerBsType.setText(it.assessCustomerEntity.identifier.trim(), false)
                etBAddress.setText(it.assessCustomerEntity.numberOfChildren.trim())
                etDSale.setText(it.assessCustomerEntity.numberOfDependants.trim())
                spinnerStatus.setText(it.assessCustomerEntity.empStatus.trim(), false)
                customerCert.clear()
                val certDoc =
                    it.customerDocs.filter { customerDocsEntity -> customerDocsEntity.docCode == educationDocCode }
                customerCert.addAll(certDoc)
                if (customerCert.isNotEmpty()) {
                    customerEducationCertificate = customerCert.first()
                    if (customerEducationCertificate.docPath.isNotEmpty()){
                    val uri = Uri.fromFile(File(customerEducationCertificate.docPath))
                    val imageName = getFileName(uri, requireActivity())
                    if (com.deefrent.rnd.fieldapp.utils.Constants.pattern.containsMatchIn(
                            customerEducationCertificate.docPath
                        )
                    ) {
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
                }} else {
                    binding.tvAttachDoc.text =
                        resources.getString(R.string.attach_education_certificate)
                }
            }
        }
    }

    private fun showEditPhotoDialog(uri: String, docType: String) {
        cardBinding = EditPhotsDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        cardBinding.apply {
            Glide.with(requireActivity()).load(uri)
                .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                .into(userLogo)
            tvTitle.text = getString(R.string.edu_cert)
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