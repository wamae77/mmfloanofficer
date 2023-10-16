package com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.deefrent.rnd.jiboostfieldapp.AppPreferences
import com.deefrent.rnd.common.abstractions.BaseDaggerFragment
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.adapters.AddGuarantorAdapter
import com.deefrent.rnd.fieldapp.databinding.AddGuarantorsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.EditPhotsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentGuarantorsBinding
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.guarantorAFFIDAVITDocCode
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.guarantorIDDocCode
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.pattern
import com.deefrent.rnd.fieldapp.utils.callbacks.GuaEntityCallBack
import com.deefrent.rnd.fieldapp.viewModels.DropdownItemsViewModel
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import dev.ronnie.github.imagepicker.ImagePicker
import dev.ronnie.github.imagepicker.ImageResult
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class Step6GuarantorsFragment : BaseDaggerFragment(), GuaEntityCallBack {
    private lateinit var binding: FragmentGuarantorsBinding
    private lateinit var cardBinding: EditPhotsDialogBinding
    private lateinit var listGeneratedUUID: String
    private lateinit var addGuarantorDialog: AddGuarantorsDialogBinding
    private lateinit var gAdapter: AddGuarantorAdapter
    private var isDeleteItems: Boolean = false
    private lateinit var collateral: List<Collateral>
    private var otherBorrowing = arrayListOf<OtherBorrowing>()
    private var household = arrayListOf<HouseholdMemberEntity>()
    private var customerDocs = arrayListOf<CustomerDocsEntity>()
    private var phoneNumber = ""
    private var maxGuarantor by Delegates.notNull<Int>()
    private var rshipId = 0
    private var nationalId = ""
    lateinit var imagePicker: ImagePicker
    private val items: ArrayList<Guarantor> = ArrayList()
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private lateinit var guaEntity: CustomerDocsEntity
    private var guarantorDocLis: ArrayList<CustomerDocsEntity> = arrayListOf()
    private lateinit var guaAffidavitEntity: CustomerDocsEntity
    private var frontIdImageName: String = ""
    private var affidavitImageName: String = ""
    private var frontIdUri: Uri? = null
    private var affidavitUri: Uri? = null
    private lateinit var generatedUUID: String
    private var isFrontIDImagePicked = false
    private var isAffidavitImagePicked = false
    private var guarantorAffidavitList: ArrayList<CustomerDocsEntity> = arrayListOf()
    private var guarantorFrontIDList: ArrayList<CustomerDocsEntity> = arrayListOf()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewmodel by lazy {
        ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(OnboardCustomerViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePicker = ImagePicker(fragment = this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGuarantorsBinding.inflate(layoutInflater)
        initializeUI()
        handleBackButton()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnAddGuarantor.setOnClickListener {
                addGuarantors()
            }
            binding.apply {
                rvTempGuarantor.layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL, false
                )
                gAdapter = AddGuarantorAdapter(items, this@Step6GuarantorsFragment)
                rvTempGuarantor.adapter = gAdapter
                viewmodel.cIdNumber.observe(viewLifecycleOwner) {
                    nationalId = it
                    getSavedItemsFromRoom(it)
                }

            }
        }
    }


    private fun initializeUI() {
        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_guarantorsFragment_to_residentialDetailsFragment)
        }
        binding.btnContinue.setOnClickListener { v ->
            /*  if (items.size < minGuarantor) {
                  toastyErrors("Add guarantors to continue")
              } else {*/
            isDeleteItems = true
            viewmodel.customerEntityData.observe(viewLifecycleOwner) { customerDetailsEntity ->
                customerDetailsEntity.apply {
                    lastStep = "GuarantorsFragment"
                    isComplete = false
                    hasFinished = false
                    isProcessed = false
                    saveCustomerFullDatLocally(customerDetailsEntity, items)

                }
            }

            Navigation.findNavController(v)
                .navigate(R.id.action_guarantors_to_otherBorrowingsFragment)
            // }
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
                if (type == "guarantorID") {
                    isFrontIDImagePicked = true
                    frontIdImageName = getFileName(uri, requireActivity())
                    frontIdUri = uri
                    addGuarantorDialog.tvAttachGuarantorFrontID.text =
                        "Guarantor Front ID - $frontIdImageName"
                } else {
                    isAffidavitImagePicked = true
                    affidavitImageName = getFileName(uri, requireActivity())
                    affidavitUri = uri
                    addGuarantorDialog.tvAttachGuarantorAffidavit.text =
                        "Guarantor Affidavit - $affidavitImageName"
                }
            }
            is ImageResult.Failure -> {
                isFrontIDImagePicked = false
                isAffidavitImagePicked = false
                val errorString = imageResult.errorString
                Toast.makeText(requireContext(), errorString, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun addGuarantors() {
        addGuarantorDialog = AddGuarantorsDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        dropdownItemsViewModel.getAllRshipType().observe(viewLifecycleOwner) { rList ->
            populateRship(rList)
        }
        addGuarantorDialog.apply {
            tvAttachGuarantorAffidavit.setOnClickListener {
                showPickerOptionsDialog("guarantorAffidavit")
            }
            tvAttachGuarantorFrontID.setOnClickListener {
                showPickerOptionsDialog("guarantorID")
            }

            etDSale.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    tlDSale.error = null
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    tlDSale.error = null
                }

                override fun afterTextChanged(p0: Editable?) {
                    tlDSale.error = null
                }
            })
            etYear.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    tlYear.error = null
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    tlYear.error = null
                }

                override fun afterTextChanged(p0: Editable?) {
                    tlYear.error = null
                }
            })
            etBAddress.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    tlBAddress.error = null
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    tlBAddress.error = null
                }

                override fun afterTextChanged(p0: Editable?) {
                    tlBAddress.error = null
                }
            })
            etGuarantorAddress.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    tiGuarantorAddress.error = null
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    tiGuarantorAddress.error = null
                }

                override fun afterTextChanged(p0: Editable?) {
                    tiGuarantorAddress.error = null
                }
            })
            btnContinue.setOnClickListener {
                val validMsg = FieldValidators.VALIDINPUT
                phoneNumber =
                    FieldValidators().formatPhoneNumber(addGuarantorDialog.etDSale.text.toString())
                val validPhone = FieldValidators().validPhoneNUmber(phoneNumber)
                val gName = etBAddress.text.toString()
                val gIdNumber = etYear.text.toString()
                val rship = spinnerIsource.text.toString()
                val gResAddress = etGuarantorAddress.text.toString()
                if (rship.isEmpty()) {
                    toastyErrors("Select relationship")
                } else if (gName.isEmpty()) {
                    tlBAddress.error = getString(R.string.required)
                } else if (!validPhone.contentEquals(validMsg)) {
                    tlDSale.error = validPhone
                } else if (gIdNumber.length < 11) {
                    tlYear.error = "Invalid ID number"
                } else if (gResAddress.isEmpty()) {
                    tiGuarantorAddress.error = getString(R.string.required)
                } else {
                    tlDSale.error = ""
                    tlBAddress.error = ""
                    tlYear.error = ""
                    tiGuarantorAddress.error = ""
                    isDeleteItems = false
                    generatedUUID = UUID.randomUUID().toString()
                    //regenerate a new unique code so that the image is saved to internal storage with a unique name
                    frontIdImageName =
                        generateUniqueGuarantorDocName(generatedUUID, guarantorIDDocCode)
                    affidavitImageName =
                        generateUniqueGuarantorDocName(generatedUUID, guarantorAFFIDAVITDocCode)
                    val frontDocs = CustomerDocsEntity(
                        0,
                        nationalId,
                        guarantorIDDocCode,
                        generatedUUID,
                        frontIdImageName
                    )
                    val affidavitDocsEntity = CustomerDocsEntity(
                        0,
                        nationalId,
                        guarantorAFFIDAVITDocCode,
                        generatedUUID,
                        affidavitImageName
                    )
                    val gua = Guarantor(
                        0,
                        etYear.text.toString(),
                        nationalId,
                        etBAddress.text.toString(),
                        phoneNumber,
                        rshipId,
                        spinnerIsource.text.toString(),
                        etGuarantorAddress.text.toString(),
                        generatedUUID
                    )
                    items.add(gua)
                    viewmodel.insertGuarantor(gua)
                    if (isFrontIDImagePicked) {
                        if (frontIdUri != null) {

                            viewmodel.insertDocument(frontDocs)
                            customerDocs.add(frontDocs)
                            saveImageToInternalAppStorage(
                                frontIdUri!!,
                                requireContext(),
                                frontIdImageName
                            )
                        }
                        //reset collateral name to avoid using the previous collateral image name
                        frontIdImageName = ""
                        isFrontIDImagePicked = false
                    }
                    if (isAffidavitImagePicked) {
                        if (affidavitUri != null) {
                            viewmodel.insertDocument(affidavitDocsEntity)
                            customerDocs.add(affidavitDocsEntity)
                            saveImageToInternalAppStorage(
                                affidavitUri!!,
                                requireContext(),
                                affidavitImageName
                            )
                        }
                        //reset collateral name to avoid using the previous collateral image name
                        affidavitImageName = ""
                        isAffidavitImagePicked = false
                    }

                    Log.d("TAG", "addGuarantors: $generatedUUID")
                    Log.d("TAG", "addGuarantorsDOC: ${Gson().toJson(customerDocs)}")
                    if (items.size >= maxGuarantor) {
                        binding.btnAddGuarantor.makeGone()
                    }
                    gAdapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
            }
        }
        dialog.setContentView(addGuarantorDialog.root)
        //  dialog.setCancelable(false)
        dialog.show()
    }

    private fun populateRship(rship: List<RshipTypeEntity>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, rship)
        addGuarantorDialog.spinnerIsource.setAdapter(typeAdapter)
        addGuarantorDialog.spinnerIsource.keyListener = null
        addGuarantorDialog.spinnerIsource.setOnItemClickListener { parent, _, position, _ ->
            val selected: RshipTypeEntity = parent.adapter.getItem(position) as RshipTypeEntity
            addGuarantorDialog.spinnerIsource.setText(selected.name, false)
            rshipId = selected.id
        }
    }

    private fun saveCustomerFullDatLocally(
        customerDetailsEntity: CustomerDetailsEntity,
        gua: List<Guarantor>
    ) {
        viewmodel.insertCustomerFullDetails(
            customerDetailsEntity,
            gua,
            collateral,
            otherBorrowing,
            household, customerDocs
        )
    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_guarantorsFragment_to_residentialDetailsFragment)

                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    override fun onItemSelected(pos: Int, lists: Guarantor) {
        addGuarantorDialog = AddGuarantorsDialogBinding.inflate(layoutInflater)
        //no need to generate a new UID on picking image if it already existsF
        //generatedUUID = lists.guarantorGeneratedUID
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        dropdownItemsViewModel.getAllRshipType().observe(viewLifecycleOwner) { rList ->
            populateRship(rList)
        }
        addGuarantorDialog.apply {
            listGeneratedUUID = lists.guarantorGeneratedUID
            tvAttachGuarantorAffidavit.setOnClickListener {
                if (tvAttachGuarantorAffidavit.text.contains(getString(R.string.view_guarantor_affidavit))) {
                    showEditPhotoDialog(guaAffidavitEntity)
                } else {
                    showPickerOptionsDialog("guarantorAffidavit")
                }
            }
            tvAttachGuarantorFrontID.setOnClickListener {
                if (tvAttachGuarantorFrontID.text.contains(getString(R.string.view_guarantor_front_national_id))) {
                    showEditPhotoDialog(guaEntity)
                } else {
                    showPickerOptionsDialog("guarantorID")
                }
            }

            btnContinue.makeGone()
            clButtons.makeVisible()
            rshipId = lists.relationshipId
            spinnerIsource.setText(lists.relationship, false)
            etBAddress.setText(lists.name)
            etYear.setText(lists.idNumber)
            etGuarantorAddress.setText(lists.residenceAddress)
            etDSale.setText(lists.phone)
            etDSale.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    tlDSale.error = null
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    tlDSale.error = null
                }

                override fun afterTextChanged(p0: Editable?) {
                    tlDSale.error = null
                }
            })
            etYear.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    tlYear.error = null
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    tlYear.error = null
                }

                override fun afterTextChanged(p0: Editable?) {
                    tlYear.error = null
                }
            })
            etBAddress.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    tlBAddress.error = null
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    tlBAddress.error = null
                }

                override fun afterTextChanged(p0: Editable?) {
                    tlBAddress.error = null
                }
            })
            etGuarantorAddress.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    tiGuarantorAddress.error = null
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    tiGuarantorAddress.error = null
                }

                override fun afterTextChanged(p0: Editable?) {
                    tiGuarantorAddress.error = null
                }
            })
            Log.d("TAG", "onItemSelected: ${lists.guarantorGeneratedUID}")
            btnCancel.setOnClickListener {
                if (isDeleteItems) {
                    viewmodel.deleteGuarantor(lists.id, nationalId, lists.guarantorGeneratedUID)
                } else {
                    isDeleteItems = false
                    items.removeAt(pos)
                    customerDocs.removeAt(pos)
                    gAdapter.notifyItemRemoved(pos)
                    if (items.size >= maxGuarantor) {
                        binding.btnAddGuarantor.makeGone()
                    } else {
                        binding.btnAddGuarantor.makeVisible()
                    }
                    Log.e("TAG", "isDeletitems: ${items.size}")

                }
                val frontId =
                    generateUniqueGuarantorDocName(lists.guarantorGeneratedUID, guarantorIDDocCode)
                val affidavitId = generateUniqueGuarantorDocName(
                    lists.guarantorGeneratedUID,
                    guarantorAFFIDAVITDocCode
                )
                Log.e("TAG", "onItemSelectedoo: $frontId")
                Log.e("TAG", "onItemSelectedaffidavitId: $affidavitId")
                deleteImageFromInternalStorage(requireContext(), frontId)
                deleteImageFromInternalStorage(requireContext(), affidavitId)
                dialog.dismiss()
            }
            btnUpdate.setOnClickListener {
                val validMsg = FieldValidators.VALIDINPUT
                phoneNumber =
                    FieldValidators().formatPhoneNumber(addGuarantorDialog.etDSale.text.toString())
                val validPhone = FieldValidators().validPhoneNUmber(phoneNumber)
                val gName = etBAddress.text.toString()
                val gIdNumber = etYear.text.toString()
                val rship = spinnerIsource.text.toString()
                val gResAddress = etGuarantorAddress.text.toString()
                if (rship.isEmpty()) {
                    toastyErrors("Select relationship")
                } else if (gName.isEmpty()) {
                    tlBAddress.error = getString(R.string.required)
                } else if (!validPhone.contentEquals(validMsg)) {
                    tlDSale.error = validPhone
                } else if (gIdNumber.length < 11) {
                    tlYear.error = "Invalid ID number"
                } else if (gResAddress.isEmpty()) {
                    tiGuarantorAddress.error = getString(R.string.required)
                } else {
                    tlDSale.error = ""
                    tlBAddress.error = ""
                    tlYear.error = ""
                    tiGuarantorAddress.error = ""
                    lists.relationship = spinnerIsource.text.toString()
                    lists.relationshipId = rshipId
                    lists.name = gName
                    lists.phone = phoneNumber
                    lists.idNumber = gIdNumber
                    lists.residenceAddress = gResAddress
                    if (lists.guarantorGeneratedUID.isNotEmpty()) {
                        if (guarantorDocLis.isNotEmpty()) {
                            affidavitImageName =
                                if (affidavitImageName.lowercase().contains("guarantor")) {
                                    affidavitImageName
                                } else {
                                    generateUniqueGuarantorDocName(
                                        lists.guarantorGeneratedUID,
                                        guarantorAFFIDAVITDocCode
                                    )
                                }
                            frontIdImageName =
                                if (frontIdImageName.lowercase().contains("guarantor")) {
                                    frontIdImageName
                                } else {
                                    generateUniqueGuarantorDocName(
                                        lists.guarantorGeneratedUID,
                                        guarantorIDDocCode
                                    )
                                }
                            guaAffidavitEntity.docPath = affidavitImageName
                            guaEntity.docPath = frontIdImageName
                            Log.e("TAG", "onItemSelectedupdate:$affidavitImageName ")
                            Log.e("TAG", "onItemSelectedUpdate12:$frontIdImageName ")

                            //collateralImageName = "${lists.collateralGeneratedUID}.jpg"
                            //collEntity.docPath = collateralImageName
                            guarantorDocLis.mapInPlace {
                                /**IF DOCCODE FROM THE LIST is equal to the doccode for front id, and path at that items has changedd
                                 *  we replace the element /value
                                 * with the new  vale, else if the element has not been modified, we retain it*/
                                    element ->
                                if (element.docCode == guarantorAFFIDAVITDocCode) {
                                    guaAffidavitEntity
                                } else {
                                    element
                                }

                                if (element.docCode == guarantorIDDocCode) {
                                    guaEntity
                                } else {
                                    element
                                }

                            }
                            if (affidavitUri != null) {
                                saveImageToInternalAppStorage(
                                    affidavitUri!!,
                                    requireContext(),
                                    affidavitImageName
                                )
                            }
                            if (frontIdUri != null) {
                                saveImageToInternalAppStorage(
                                    frontIdUri!!,
                                    requireContext(),
                                    frontIdImageName
                                )
                            }
                            viewmodel.updateGuarantor(lists, guarantorDocLis)
                        } else {
                            /**we have the uid, so we dont want it to generate another uid*/
                            //regenerate a new unique code so that the image is saved to internal storage with a unique name
                            frontIdImageName =
                                generateUniqueGuarantorDocName(
                                    lists.guarantorGeneratedUID,
                                    guarantorIDDocCode
                                )
                            affidavitImageName =
                                generateUniqueGuarantorDocName(
                                    lists.guarantorGeneratedUID,
                                    guarantorAFFIDAVITDocCode
                                )
                            val frontDocs = CustomerDocsEntity(
                                0,
                                nationalId,
                                guarantorIDDocCode,
                                lists.guarantorGeneratedUID,
                                frontIdImageName
                            )
                            val affidavitDocsEntity = CustomerDocsEntity(
                                0,
                                nationalId,
                                guarantorAFFIDAVITDocCode,
                                lists.guarantorGeneratedUID,
                                affidavitImageName
                            )
                            if (isFrontIDImagePicked) {
                                if (frontIdUri != null) {
                                    viewmodel.insertDocument(frontDocs)
                                    customerDocs.add(frontDocs)
                                    saveImageToInternalAppStorage(
                                        frontIdUri!!,
                                        requireContext(),
                                        frontIdImageName
                                    )
                                }
                                //reset collateral name to avoid using the previous collateral image name
                                frontIdImageName = ""
                                isFrontIDImagePicked = false
                            }
                            if (isAffidavitImagePicked) {
                                if (affidavitUri != null) {
                                    viewmodel.insertDocument(affidavitDocsEntity)
                                    customerDocs.add(affidavitDocsEntity)
                                    saveImageToInternalAppStorage(
                                        affidavitUri!!,
                                        requireContext(),
                                        affidavitImageName
                                    )
                                }
                                //reset collateral name to avoid using the previous collateral image name
                                affidavitImageName = ""
                                isAffidavitImagePicked = false
                            }
                        }
                    } else {
                        val generatedUUID = UUID.randomUUID().toString()
                        lists.guarantorGeneratedUID = generatedUUID
                        //regenerate a new unique code so that the image is saved to internal storage with a unique name
                        frontIdImageName =
                            generateUniqueGuarantorDocName(generatedUUID, guarantorIDDocCode)
                        affidavitImageName =
                            generateUniqueGuarantorDocName(generatedUUID, guarantorAFFIDAVITDocCode)
                        val frontDocs = CustomerDocsEntity(
                            0,
                            nationalId,
                            guarantorIDDocCode,
                            generatedUUID,
                            frontIdImageName
                        )
                        val affidavitDocsEntity = CustomerDocsEntity(
                            0,
                            nationalId,
                            guarantorAFFIDAVITDocCode,
                            generatedUUID,
                            affidavitImageName
                        )
                        viewmodel.updateGuarantor(lists, customerDocs)
                        if (isFrontIDImagePicked) {
                            if (frontIdUri != null) {
                                viewmodel.insertDocument(frontDocs)
                                customerDocs.add(frontDocs)
                                guarantorDocLis.add(frontDocs)
                                saveImageToInternalAppStorage(
                                    frontIdUri!!,
                                    requireContext(),
                                    frontIdImageName
                                )
                            }
                            //reset collateral name to avoid using the previous collateral image name
                            //  frontIdImageName = ""
                            isFrontIDImagePicked = false
                        }
                        if (isAffidavitImagePicked) {
                            if (affidavitUri != null) {
                                /**you click update, but fail to save to room
                                 * so it will update generate a uid, so that next time instead of creating a new uid,
                                 * it will use this*/
                                viewmodel.insertDocument(affidavitDocsEntity)
                                guarantorDocLis.add(frontDocs)
                                customerDocs.add(affidavitDocsEntity)
                                saveImageToInternalAppStorage(
                                    affidavitUri!!,
                                    requireContext(),
                                    affidavitImageName
                                )
                            }
                            //reset collateral name to avoid using the previous collateral image name
                            //
                        } else {
                            affidavitImageName = ""
                            isAffidavitImagePicked = false
                        }
                        Log.e(
                            "TAG",
                            "onItemSelectedCHECKING IF LIST${Gson().toJson(guarantorDocLis)}: ",
                        )
                    }

                    gAdapter.notifyItemChanged(pos)
                    if (items.size >= maxGuarantor) {
                        binding.btnAddGuarantor.makeGone()
                    }
                    dialog.dismiss()
                }
            }
            /**filter document for only this guarantor bt generatedUID*/
            if (lists.guarantorGeneratedUID.isNotEmpty()) {
                guarantorDocLis.clear()
                val docList =
                    customerDocs.filter { customerDocsEntity -> customerDocsEntity.docGeneratedUID == lists.guarantorGeneratedUID }
                guarantorDocLis.addAll(docList)
                val affidavitList =
                    guarantorDocLis.filter { customerDocsEntity -> customerDocsEntity.docCode == guarantorAFFIDAVITDocCode }
                guarantorAffidavitList.addAll(affidavitList)
                val frontIDList =
                    guarantorDocLis.filter { customerDocsEntity -> customerDocsEntity.docCode == guarantorIDDocCode }
                guarantorFrontIDList.addAll(frontIDList)
                if (affidavitList.isNotEmpty()) {
                    Log.d("TAG", "onItemSelectedlist: ${Gson().toJson(affidavitList)}")
                    guaAffidavitEntity = affidavitList.first()
                    Log.d("TAG", "onItemSelected.guaEntity1: ${Gson().toJson(guaAffidavitEntity)}")
                    //val uri = Uri.fromFile(File(guaAvidafitEntity.docPath))
                    if (guaAffidavitEntity.docPath.isNotEmpty()) {
                        val imageName = guaAffidavitEntity.docPath
                        if (pattern.containsMatchIn(guaAffidavitEntity.docPath)) {
                            addGuarantorDialog.tvAttachGuarantorAffidavit.text =
                                getString(R.string.view_guarantor_affidavit)
                        } else {
                            Log.e("TAG", "uri: $imageName")
                            tvAttachGuarantorAffidavit.text =
                                "Guarantor Affidavit - $imageName"
                        }
                        val newImageName = if (imageName.lowercase().contains("guarantor")) {
                            imageName
                        } else {
                            generateUniqueGuarantorDocName(
                                lists.guarantorGeneratedUID,
                                guarantorAFFIDAVITDocCode
                            )
                        }
                        affidavitImageName = newImageName
                    } else {
                        tvAttachGuarantorAffidavit.text =
                            resources.getString(R.string.attach_guarantor_affidavit)
                    }
                } else {
                    tvAttachGuarantorAffidavit.text =
                        resources.getString(R.string.attach_guarantor_affidavit)
                }
                if (frontIDList.isNotEmpty()) {
                    guaEntity = frontIDList.first()
                    Log.d("TAG", "onItemSelected.guaEntity: ${Gson().toJson(guaEntity)}")
                    if (guaEntity.docPath.isNotEmpty()) {
                        val uri = Uri.fromFile(File(guaEntity.docPath))
                        val imageName = getFileName(uri, requireActivity())
                        if (pattern.containsMatchIn(guaEntity.docPath)) {
                            addGuarantorDialog.tvAttachGuarantorFrontID.text =
                                getString(R.string.view_guarantor_front_national_id)
                        } else {
                            Log.e("TAG", "uri: $imageName")
                            tvAttachGuarantorFrontID.text =
                                "Guarantor Front ID - $imageName"
                        }
                        val newImageName = if (imageName.lowercase().contains("guarantor")) {
                            imageName
                        } else {
                            generateUniqueGuarantorDocName(
                                lists.guarantorGeneratedUID,
                                guarantorIDDocCode
                            )
                        }
                        frontIdImageName = newImageName


                    } else {
                        tvAttachGuarantorFrontID.text =
                            resources.getString(R.string.attach_guarantor_front_national_id)
                    }
                } else {
                    tvAttachGuarantorFrontID.text =
                        resources.getString(R.string.attach_guarantor_front_national_id)
                }
            } else {
                tvAttachGuarantorFrontID.text =
                    resources.getString(R.string.attach_guarantor_front_national_id)
            }


        }
        dialog.setContentView(addGuarantorDialog.root)
        dialog.show()
    }

    private fun getSavedItemsFromRoom(parentNationalId: String) {
        /**
         * GlobalScope run globally throughout the app lifecyle
         * CoroutineScope->run on lifecycle of viewmodels*/
        viewmodel.fetchCustomerDetails(parentNationalId).observe(viewLifecycleOwner) {
            Log.d("TAG", "getSavedItemsFromRoomDebugGoll: ${Gson().toJson(it.guarantors)}")
            binding.apply {
                note.makeGone()
                maxGuarantor = AppPreferences.maxGuarantor.toString().toInt()
                isDeleteItems = true
                items.clear()
                items.addAll(it.guarantors)
                if (items.size >= maxGuarantor) {
                    btnAddGuarantor.makeGone()
                } else {
                    btnAddGuarantor.makeVisible()
                }
                gAdapter.notifyDataSetChanged()
                otherBorrowing.clear()
                otherBorrowing.addAll(it.otherBorrowing)
                household.clear()
                household.addAll(it.householdMember)
                customerDocs.clear()
                customerDocs.addAll(it.customerDocs)
                collateral = (it.collateral)

            }
        }

    }

    private fun showEditPhotoDialog(customerDocsEntity: CustomerDocsEntity) {
        cardBinding = EditPhotsDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        cardBinding.apply {
            if (customerDocsEntity.docCode == guarantorAFFIDAVITDocCode) {
                Log.d("TAG", "showEditPhotoDialog1: ${customerDocsEntity.docCode}")
                Log.d("TAG", "showEditPhotoDialog12: ${customerDocsEntity.docPath}")
                tvTitle.text = "Guarantor Affidavit Photo"
                Glide.with(requireActivity()).load(customerDocsEntity.docPath)
                    .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                    .into(userLogo)
                tvEdit.setOnClickListener {
                    dialog.dismiss()
                    showPickerOptionsDialog("guarantorAffidavit")
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

            } else {
                tvTitle.text = "Guarantor Front ID"
                Log.d("TAG", "showEditPhotoDialog2: ${customerDocsEntity.docCode}")
                Log.d("TAG", "showEditPhotoDialog22: ${customerDocsEntity.docPath}")
                Glide.with(requireActivity()).load(customerDocsEntity.docPath)
                    .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                    .into(userLogo)
                tvEdit.setOnClickListener {
                    dialog.dismiss()
                    showPickerOptionsDialog("guarantorID")
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

            }

            ImageCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.setContentView(cardBinding.root)
        dialog.show()

    }


}
