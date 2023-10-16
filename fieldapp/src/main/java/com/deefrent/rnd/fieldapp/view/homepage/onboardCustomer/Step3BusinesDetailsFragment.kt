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
import com.deefrent.rnd.fieldapp.databinding.FragmentBusinesDetailsBinding
import com.deefrent.rnd.fieldapp.network.models.*
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.room.entities.OtherBorrowing
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.businessDocCode
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

class Step3BusinesDetailsFragment : BaseDaggerFragment() {
    private var nationaid = ""
    private lateinit var binding: FragmentBusinesDetailsBinding
    private lateinit var cardBinding: EditPhotsDialogBinding
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private var guarantor = arrayListOf<Guarantor>()
    private var collateral = arrayListOf<Collateral>()
    private var otherBorrowing = arrayListOf<OtherBorrowing>()
    private var household = arrayListOf<HouseholdMemberEntity>()
    private var customerDocs: ArrayList<CustomerDocsEntity> = arrayListOf()
    private var isBtnChecked = false
    lateinit var imagePicker: ImagePicker
    private var filterCustomerCertCode = arrayListOf<CustomerDocsEntity>()
    private lateinit var incorporationCertificate: CustomerDocsEntity
    private var imageUrl = ""
    private var docType = ""
    private var incorporationCertificateImageName = ""
    private var incorporationCertificateUri: Uri? = null


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewmodel by lazy {
        ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(OnboardCustomerViewModel::class.java)
    }
    private var bussinessId = ""
    private var eSectorId = ""
    private var establishTypeId = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBusinesDetailsBinding.inflate(layoutInflater)
        viewmodel.cIdNumber.observe(viewLifecycleOwner) { customerIDNumber ->
            nationaid = customerIDNumber
            getSavedItemsFromRoom(customerIDNumber)
        }

        binding.apply {
            rbMyself.isChecked = true
            isBtnChecked = true
            rbMyself.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    isBtnChecked = true
                    clOption.makeVisible()
                    rbOthers.isChecked = false
                }
            }
            rbOthers.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    isBtnChecked = false
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
        binding.apply {
            tvAttachDoc.setOnClickListener {
                if (tvAttachDoc.text.contains(getString(R.string.view_incor_certificate))) {
                    Log.d("TAG", "imageUrl:$imageUrl, $docType")
                    showEditPhotoDialog(imageUrl, docType)
                } else {
                    showPickerOptionsDialog()
                }
            }
        }
        binding.apply {
            binding.ivBack.setOnClickListener {
                if (fromSummary == 1) {
                    findNavController().navigate(R.id.summaryFragment)
                } else {
                    fromSummary = -1
                    val directions =
                        Step3BusinesDetailsFragmentDirections.actionBusinesDetailsFragmentToCustomerDetailsFragment(
                            4
                        )
                    findNavController().navigate(directions)
                }

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
                    val industryName = etIndustryName.text.toString()
                    val yrs = etyears.text.toString()
                    val typeOfBz = etPAddress.text.toString()
                    val eSector = etAccommodationStatus.text.toString()
                    val establishTypeName = etEstType.text.toString()
                    if (typeOfBz.isEmpty()) {
                        toastyErrors("Select type of business")
                    } else if (eSector.isEmpty()) {
                        toastyErrors("Select economic sector")
                    } else if (industryName.isEmpty()) {
                        tlIndustryName.error = "Required"
                    } else if (establishTypeName.isEmpty()) {
                        tiEstType.error = "Required"
                    } else if (yrs.isEmpty()) {
                        tlYears.error = "Required"
                    } else {
                        tlIndustryName.error = ""
                        tiEstType.error = ""
                        tlYears.error = ""
                        Log.d("TAG", "isBtnCheckedggg:$isBtnChecked ")
                        viewmodel.customerEntityData.observe(viewLifecycleOwner) { customerDetailsEntity ->
                            customerDetailsEntity.apply {
                                lastStep = "BusinessDetailsFragment"
                                isComplete = false
                                isProcessed = false
                                hasFinished = false
                                bsTypeOfBusinessId = bussinessId
                                bsTypeOfBusiness = etPAddress.text.toString()
                                bsEconomicFactorId = eSectorId
                                bsEconomicFactor = etAccommodationStatus.text.toString()
                                bsNameOfIndustry = etIndustryName.text.toString()
                                bsEstablishmentTypeId = establishTypeId
                                bsEstablishmentType = etEstType.text.toString()
                                bsYearsInBusiness = etyears.text.toString()
                                viewmodel.customerEntityData.postValue(customerDetailsEntity)
                                saveCustomerFullDatLocally(customerDetailsEntity)
                                if (incorporationCertificateUri != null) {
                                    saveImageToInternalAppStorage(
                                        incorporationCertificateUri!!,
                                        requireContext(),
                                        incorporationCertificateImageName
                                    )
                                }
                            }
                        }
                        if (fromSummary == 1) {
                            findNavController().navigate(R.id.summaryFragment)
                        } else {
                            findNavController().navigate(R.id.action_businesDetailsFragment_to_businessAddressFragment)
                        }
                    }
                } else {
                    if (fromSummary == 1) {
                        findNavController().navigate(R.id.summaryFragment)
                    } else {
                        fromSummary = -1
                        findNavController().navigate(R.id.action_businesDetailsFragment_to_collateralsFragment)
                    }
                }
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
                    val customerDocsEntity = CustomerDocsEntity(
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
                Toasty.error(requireContext(), errorString, Toast.LENGTH_LONG).show()
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
            try {
                Log.d("TAG", "getSavedItemsFromRoomDebug3: ${Gson().toJson(it.customerDetails)}")
                Log.d(
                    "TAG",
                    "getSavedItemsFromRoomDebugC3: ${Gson().toJson(it.customerDetails.maximumColateral)}"
                )
                Log.d(
                    "TAG",
                    "getSavedItemsFromRoomDebugG3: ${Gson().toJson(it.customerDetails.maximumGuarantor)}"
                )
                Log.d("TAG", "getSavedItemsFromRoomDebugO3: ${Gson().toJson(it.otherBorrowing)}")
                binding.apply {
                    if (it.customerDetails.bsTypeOfBusiness.isNotEmpty()) {
                        etPAddress.setText(it.customerDetails.bsTypeOfBusiness, false)
                    }
                    if (it.customerDetails.bsEconomicFactor.isNotEmpty()) {
                        etAccommodationStatus.setText(it.customerDetails.bsEconomicFactor, false)
                    }
                    if (it.customerDetails.bsEstablishmentType.isNotEmpty()) {
                        etEstType.setText(it.customerDetails.bsEstablishmentType, false)
                    }
                    if (it.customerDetails.bsNameOfIndustry.isNotEmpty()) {
                        etIndustryName.setText(it.customerDetails.bsNameOfIndustry)
                    }
                    if (it.customerDetails.bsYearsInBusiness.isNotEmpty()) {
                        etyears.setText(it.customerDetails.bsYearsInBusiness)
                    }
                    if (it.customerDetails.bsEconomicFactorId.isNotEmpty()) {
                        eSectorId = (it.customerDetails.bsEconomicFactorId)
                    }
                    if (it.customerDetails.bsTypeOfBusinessId.isNotEmpty()) {
                        bussinessId = (it.customerDetails.bsTypeOfBusinessId)
                    }
                    if (it.customerDetails.bsEstablishmentTypeId.isNotEmpty()) {
                        establishTypeId = (it.customerDetails.bsEstablishmentTypeId)
                    }
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
                    Log.e("TAG", "customerDocsEntity Phase 1: ${Gson().toJson(it.customerDocs)}")
                    filterCustomerCertCode.clear()
                    val custDoc =
                        it.customerDocs.filter { customerDocsEntity -> customerDocsEntity.docCode == businessDocCode && customerDocsEntity.parentNationalIdentity == parentNationalId }
                    Log.e(
                        "TAG",
                        "customerDocsEntity Phase 2: ${Gson().toJson(filterCustomerCertCode)}"
                    )
                    filterCustomerCertCode.addAll(custDoc)
                    if (filterCustomerCertCode.isNotEmpty()) {
                        incorporationCertificate = filterCustomerCertCode.first()
                        if (incorporationCertificate.docPath.isNotEmpty()) {
                            val uri = Uri.fromFile(File(incorporationCertificate.docPath))
                            val imageName = getFileName2(uri, requireActivity())
                            if (Constants.pattern.containsMatchIn(incorporationCertificate.docPath)) {
                                binding.tvAttachDoc.text =
                                    getString(R.string.view_incor_certificate)
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
                        }
                    } else {
                        binding.tvAttachDoc.text =
                            resources.getString(R.string.attach_certificate_of_incorporation)
                    }
                }
            } catch (e: Exception) {
                Log.d("", "${e.message}")
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

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    if (fromSummary == 1) {
                        findNavController().navigate(R.id.summaryFragment)
                    } else {
                        fromSummary = -1
                        val directions =
                            Step3BusinesDetailsFragmentDirections.actionBusinesDetailsFragmentToCustomerDetailsFragment(
                                4
                            )
                        findNavController().navigate(directions)
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }


}