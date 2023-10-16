package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.assessment

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.EditPhotsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentBusinesDetailsBinding
import com.deefrent.rnd.fieldapp.network.models.*
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.businessDocCode
import com.deefrent.rnd.fieldapp.viewModels.AssessmentDashboardViewModel
import com.deefrent.rnd.fieldapp.viewModels.DropdownItemsViewModel
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import dev.ronnie.github.imagepicker.ImagePicker
import dev.ronnie.github.imagepicker.ImageResult
import java.io.File
import java.util.*

class AssessBusinesDetailsFragment : Fragment() {
    private lateinit var binding: FragmentBusinesDetailsBinding
    private lateinit var cardBinding: EditPhotsDialogBinding
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private var guarantor = arrayListOf<AssessGuarantor>()
    private var collateral = arrayListOf<AssessCollateral>()
    private var household = arrayListOf<AssessHouseholdMemberEntity>()
    private var otherBorrowing = arrayListOf<AssessBorrowing>()
    private var customerDocs = arrayListOf<AssessCustomerDocsEntity>()
    private var filterCustomerCertCode = arrayListOf<AssessCustomerDocsEntity>()
    private lateinit var incorporationCertificate: AssessCustomerDocsEntity
    private var nationaid = ""
    private var imageUrl = ""
    private var docType = ""
    lateinit var imagePicker: ImagePicker
    private var incorporationCertificateImageName = ""
    private var incorporationCertificateUri: Uri? = null
    private val viewmodel by lazy {
        ViewModelProvider(
            requireActivity()
        ).get(AssessmentDashboardViewModel::class.java)
    }
    private var bussinessId = ""
    private var eSectorId = ""
    private var establishTypeId = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBusinesDetailsBinding.inflate(layoutInflater)
        binding.apply {
            rbMyself.isChecked = true
            viewmodel.parentId.observe(viewLifecycleOwner) { nationalId ->
                nationaid = nationalId
                getAssessmentFromRoom(nationalId)
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackButton()
        binding.tvAttachDoc.setOnClickListener {
            if (binding.tvAttachDoc.text.contains(getString(R.string.view_incor_certificate))) {
                Log.d("TAG", "imageUrl:$imageUrl, $docType")
                showEditPhotoDialog(imageUrl, docType)
            } else {
                showPickerOptionsDialog()
            }
        }

        binding.apply {
            binding.ivBack.setOnClickListener {
                findNavController().navigate(R.id.assessCustomerDetailsFragment)
            }
            dropdownItemsViewModel.getAllBusinessType().observe(viewLifecycleOwner) { bList ->
                populateBznessType(bList)

            }
            dropdownItemsViewModel.getAllEconomicSector().observe(viewLifecycleOwner) { eList ->
                populateEconomicSector(eList)
            }
            dropdownItemsViewModel.getAllEstablishType().observe(viewLifecycleOwner) { estList ->
                populateEstablishType(estList)
                Log.d("TAG", "onViewCreated:eee${estList} ")

            }
            btnContinue.setOnClickListener {
                if (rbMyself.isChecked) {
                    val industrysName = etIndustryName.text.toString()
                    val yrs = etyears.text.toString()
                    val bsType = etPAddress.text.toString()
                    val eSector = etAccommodationStatus.text.toString()
                    val esablish = etEstType.text.toString()
                    if (bsType.isEmpty()) {
                        toastyErrors("Select type of business")
                    } else if (eSector.isEmpty()) {
                        toastyErrors("Select economic sector")
                    } else if (industrysName.isEmpty()) {
                        tlIndustryName.error = "Required"
                    } else if (esablish.isEmpty()) {
                        tiEstType.error = "Required"
                    } else if (yrs.isEmpty()) {
                        tlYears.error = "Required"
                    } else {
                        tlIndustryName.error = ""
                        tiEstType.error = ""
                        tlYears.error = ""
                        viewmodel.assessCustomerEntity.observe(viewLifecycleOwner) { detailsEntity ->
                            detailsEntity.apply {
                                lastStep = "AssessBusinesDetailsFragment"
                                isComplete = false
                                hasFinished = false
                                isProcessed = false
                                //isBSButtonChecked=isBtnChecked
                                businessTypeId = bussinessId
                                businessTypeName = etPAddress.text.toString()
                                economicFactorId = eSectorId
                                economicFactorName = etAccommodationStatus.text.toString()
                                nameOfIndustry = etIndustryName.text.toString()
                                establishmentTypeId = establishTypeId
                                establishmentTypeName = etEstType.text.toString()
                                yearsInBusiness = etyears.text.toString()
                                viewmodel.assessCustomerEntity.postValue(detailsEntity)
                                saveAssessmentDataLocally(detailsEntity)
                                if (incorporationCertificateUri != null) {
                                    saveImageToInternalAppStorage(
                                        incorporationCertificateUri!!,
                                        requireContext(),
                                        incorporationCertificateImageName
                                    )
                                }
                            }
                        }
                        findNavController().navigate(R.id.action_assessBsDetailFragment_to_assessBsAddressFragment)

                    }
                } else {
                    findNavController().navigate(R.id.assessCollateralsFragment)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imagePicker = ImagePicker(fragment = this)
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
                incorporationCertificateImageName = generateUniqueDocName(
                    nationaid,
                    businessDocCode
                )
                incorporationCertificateUri = uri
                if (filterCustomerCertCode.isNotEmpty()) {
                    incorporationCertificate.docPath = incorporationCertificateImageName
                    Log.i("TAG", "imageCallBack: ${Gson().toJson(incorporationCertificate)}")
                    customerDocs.mapInPlace {
                        /**IF DOCCODE FROM THE LIST is equal to the doccode for front id, and path at that items has changedd
                         *  we replace the element /value
                         * with the new  vale, else if the element has not been modified, we retain it*/
                            element ->
                        if (element.docCode == businessDocCode) incorporationCertificate else element
                    }
                } else {
                    val generatedUUID = UUID.randomUUID().toString()
                    val customerDocsEntity = AssessCustomerDocsEntity(
                        0,
                        nationaid,
                        businessDocCode,
                        generatedUUID,
                        incorporationCertificateImageName
                    )
                    customerDocs.add(customerDocsEntity)
                }
                binding.tvAttachDoc.text =
                    "Certificate of Incorporation - $incorporationCertificateImageName"
            }
            is ImageResult.Failure -> {
                val errorString = imageResult.errorString
                Toast.makeText(requireContext(), errorString, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun populateBznessType(bsType: List<BusinessType>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, bsType)
        binding.etPAddress.setAdapter(typeAdapter)
        binding.etPAddress.keyListener = null
        binding.etPAddress.setOnItemClickListener { parent, _, position, _ ->
            val selected: BusinessType = parent.adapter.getItem(position) as BusinessType
            binding.etPAddress.setText(selected.name, false)
            bussinessId = selected.id.toString()
        }
    }

    private fun populateEconomicSector(eSector: List<EconomicSector>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, eSector)
        binding.etAccommodationStatus.setAdapter(typeAdapter)
        binding.etAccommodationStatus.keyListener = null
        binding.etAccommodationStatus.setOnItemClickListener { parent, _, position, _ ->
            val selected: EconomicSector = parent.adapter.getItem(position) as EconomicSector
            binding.etAccommodationStatus.setText(selected.name, false)
            eSectorId = selected.id.toString()
        }
    }

    private fun populateEstablishType(establishType: List<EstablishmentType>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, establishType)
        binding.etEstType.setAdapter(typeAdapter)
        binding.etEstType.keyListener = null
        binding.etEstType.setOnItemClickListener { parent, _, position, _ ->
            val selected: EstablishmentType = parent.adapter.getItem(position) as EstablishmentType
            binding.etEstType.setText(selected.name, false)
            establishTypeId = selected.id.toString()
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
                    //findNavController().navigateUp()
                    findNavController().navigate(R.id.assessCustomerDetailsFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

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

    private fun getAssessmentFromRoom(parentId: String) {
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
                bussinessId = it.assessCustomerEntity.businessTypeId.toString()
                eSectorId = it.assessCustomerEntity.economicFactorId.toString()
                establishTypeId = it.assessCustomerEntity.establishmentTypeId.toString()
                etPAddress.setText(it.assessCustomerEntity.businessTypeName, false)
                etAccommodationStatus.setText(it.assessCustomerEntity.economicFactorName, false)
                etIndustryName.setText(it.assessCustomerEntity.nameOfIndustry)
                etEstType.setText(it.assessCustomerEntity.establishmentTypeName, false)
                etyears.setText(it.assessCustomerEntity.yearsInBusiness.toString())
                filterCustomerCertCode.clear()
                val custDoc =
                    it.customerDocs.filter { customerDocsEntity -> customerDocsEntity.docCode == businessDocCode }
                Log.e("TAG", "customerDocsEntity Phase 2: ${Gson().toJson(filterCustomerCertCode)}")
                filterCustomerCertCode.addAll(custDoc)
                if (filterCustomerCertCode.isNotEmpty()) {
                    incorporationCertificate = filterCustomerCertCode.first()
                    if (incorporationCertificate.docPath.isNotEmpty()){
                    val uri = Uri.fromFile(File(incorporationCertificate.docPath))
                    val imageName = getFileName2(uri, requireActivity())
                    if (com.deefrent.rnd.fieldapp.utils.Constants.pattern.containsMatchIn(
                            incorporationCertificate.docPath
                        )
                    ) {
                        binding.tvAttachDoc.text = getString(R.string.view_incor_certificate)
                        imageUrl = incorporationCertificate.docPath
                        docType = incorporationCertificate.docCode
                    } else {
                        Log.e("TAG", "uri: $imageName")
                        binding.tvAttachDoc.text =
                            "Certificate of Incorporation - $imageName"
                    }
                } else {
                    binding.tvAttachDoc.text =
                        resources.getString(R.string.attach_certificate_of_incorporation)
                }} else {
                    binding.tvAttachDoc.text =
                        resources.getString(R.string.attach_certificate_of_incorporation)
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
            //  if (docType== educationDocCode){
            tvTitle.text = "Certificate of Incorporation"
            tvEdit.setOnClickListener {
                dialog.dismiss()
                Log.d("TAG", "showEditPhotoDialog: $docType")
                showPickerOptionsDialog()
            }

            //   }
            cardBinding.userLogo.setOnClickListener {
                val mBuilder: AlertDialog.Builder =
                    AlertDialog.Builder(context, R.style.WrapContentDialog)
                val mView: View = layoutInflater.inflate(R.layout.preview_image, null)
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