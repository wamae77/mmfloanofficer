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
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.deefrent.rnd.jiboostfieldapp.AppPreferences
import com.deefrent.rnd.common.abstractions.BaseDaggerFragment
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.data.AddCollateralAdapter
import com.deefrent.rnd.fieldapp.databinding.AddCollateralDialogBinding
import com.deefrent.rnd.fieldapp.databinding.EditPhotsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentCollateralsBinding
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.collateralDocCode
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.pattern
import com.deefrent.rnd.fieldapp.utils.callbacks.ColCallBack
import com.deefrent.rnd.fieldapp.viewModels.DropdownItemsViewModel
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import dev.ronnie.github.imagepicker.ImagePicker
import dev.ronnie.github.imagepicker.ImageResult
import es.dmoral.toasty.Toasty
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class Step4CollateralsFragment : BaseDaggerFragment(), ColCallBack {
    private lateinit var binding: FragmentCollateralsBinding
    private lateinit var cardEditBinding: EditPhotsDialogBinding
    private val items: ArrayList<Collateral> = ArrayList()
    private lateinit var gAdapter: AddCollateralAdapter
    private var guarantor = arrayListOf<Guarantor>()
    private var otherBorrowing = arrayListOf<OtherBorrowing>()
    private var household = arrayListOf<HouseholdMemberEntity>()
    private var customerDocs: ArrayList<CustomerDocsEntity> = arrayListOf()
    private var customerCollateralDoc: ArrayList<CustomerDocsEntity> = arrayListOf()
    private var isDeleteItems: Boolean = false
    private var nationalId = ""
    private lateinit var maximumCollateral: String
    private lateinit var collEntity: CustomerDocsEntity
    private var imageUrl = ""
    private var docType = ""
    private var collateralImageName = ""
    private var collateralUri: Uri? = null
    private var isImagePicked = false

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewmodel by lazy {
        ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(OnboardCustomerViewModel::class.java)
    }
    private lateinit var cardBinding: AddCollateralDialogBinding
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private var assetId = 0
    lateinit var imagePicker: ImagePicker
    private lateinit var listGeneratedUUID: String
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
                isImagePicked = true
                val uri = imageResult.value
                collateralImageName = getFileName(uri, requireActivity())
                collateralUri = uri
                cardBinding.tvAttachDoc.text =
                    "Collateral Photo - $collateralImageName"

            }
            is ImageResult.Failure -> {
                isImagePicked = false
                val errorString = imageResult.errorString
                Toasty.error(requireContext(), errorString, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCollateralsBinding.inflate(layoutInflater)

        initializeUI()
        handleBackButton()
        return binding.root
    }

    private fun initializeUI() {
        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_collaterals_to_businessAddressFragment)
        }
        binding.btnContinue.setOnClickListener { v ->
            viewmodel.customerEntityData.observe(viewLifecycleOwner) { customerDetailsEntity ->
                customerDetailsEntity.apply {
                    lastStep = "CollateralsFragment"
                    isComplete = false
                    isProcessed = false
                    hasFinished = false
                    isDeleteItems = true
                    viewmodel.collateralData.postValue(items)
                    Log.d("TAG", "initializeUI: ${customerDocs.size}")
                    viewmodel.customerEntityData.postValue(customerDetailsEntity)
                    saveCustomerFullDatLocally(customerDetailsEntity, items)
                }
            }
            Navigation.findNavController(v)
                .navigate(R.id.action_collaterals_to_residentialDetailsFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnAddCollateral.setOnClickListener {
                addCollateral()
            }
            rvAddCollateral.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            gAdapter = AddCollateralAdapter(items, this@Step4CollateralsFragment)
            rvAddCollateral.adapter = gAdapter
            viewmodel.cIdNumber.observe(viewLifecycleOwner) {
                nationalId = it
                getSavedItemsFromRoom(it)
            }
        }
    }

    private fun addCollateral() {
        cardBinding = AddCollateralDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        dropdownItemsViewModel.getAllAssetType().observe(viewLifecycleOwner) { aList ->
            Log.d("TAG", "addCollateral: $aList")
            populateAssetType(aList)
        }
        cardBinding.apply {
            tvAttachDoc.setOnClickListener {
                showPickerOptionsDialog()
            }
            btnContinue.setOnClickListener {
                val aName = etBAddress.text.toString()
                val assetType = spinnerIsource.text.toString()
                val aModel = etDSale.text.toString()
                val serialNumber = etYear.text.toString()
                val estValue = etGuarantorAddress.text.toString()
                when {
                    assetType.isEmpty() -> {
                        toastyErrors("Select Asset Name")
                    }
                    aName.isEmpty() -> {
                        tlBAddress.error = getString(R.string.required)
                    }
                    estValue.isEmpty() -> {
                        tiGuarantorAddress.error = getString(R.string.required)
                    }
                    /*tvAttachDoc.text.toString() == resources.getString(R.string.attach_collateral_photo) -> {
                        toastyErrors(getString(R.string.attach_collateral_photo))
                    }*/
                    else -> {
                        tlDSale.error = ""
                        tlBAddress.error = ""
                        tlYear.error = ""
                        tiGuarantorAddress.error = ""
                        //if (collateralImageName == "") {
                        val generatedUUID = UUID.randomUUID().toString()
                        //regenerate a new unique code so that the image is saved to internal storage with a unique name
                        collateralImageName = generateUniqueCollateralDocName(generatedUUID)
                        // }
                        val customerDocsEntity = CustomerDocsEntity(
                            0,
                            nationalId,
                            collateralDocCode,
                            generatedUUID,
                            collateralImageName
                        )
                        val coll = Collateral(
                            0,
                            nationalId,
                            assetId,
                            spinnerIsource.text.toString(),
                            estValue,
                            aModel,
                            aName,
                            serialNumber,
                            generatedUUID, false
                        )
                        isDeleteItems = false
                        items.add(coll)
                        if (isImagePicked) {
                            customerDocs.add(customerDocsEntity)
                            if (collateralUri != null) {
                                viewmodel.insertSingleCollateral(coll)
                                viewmodel.insertDocument(customerDocsEntity)
                                saveImageToInternalAppStorage(
                                    collateralUri!!,
                                    requireContext(),
                                    collateralImageName
                                )
                            }
                        }

                        //reset collateral name to avoid using the previous collateral image name
                        collateralImageName = ""
                        isImagePicked = false
                        if ("${items.size}" >= maximumCollateral) {
                            binding.btnAddCollateral.makeGone()
                        }
                        binding.rvAddCollateral.adapter?.notifyDataSetChanged()
                        dialog.dismiss()
                    }
                }
            }
        }
        dialog.setContentView(cardBinding.root)
        dialog.show()
    }

    private fun populateAssetType(assetType: List<AssetTypeEntity>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, assetType)
        cardBinding.spinnerIsource.setAdapter(typeAdapter)
        cardBinding.spinnerIsource.keyListener = null
        cardBinding.spinnerIsource.setOnItemClickListener { parent, _, position, _ ->
            val selected: AssetTypeEntity = parent.adapter.getItem(position) as AssetTypeEntity
            cardBinding.spinnerIsource.setText(selected.name, false)
            assetId = selected.id
            Log.d("TAG", "assetType: $assetType")
        }
    }

    private fun saveCustomerFullDatLocally(
        customerEntity: CustomerDetailsEntity,
        collateral: List<Collateral>
    ) {
        viewmodel.insertCustomerFullDetails(
            customerEntity,
            guarantor,
            collateral,
            otherBorrowing,
            household,
            customerDocs
        )
    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_collaterals_to_businessAddressFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    override fun onItemSelected(pos: Int, lists: Collateral) {
        cardBinding = AddCollateralDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        dropdownItemsViewModel.getAllAssetType().observe(viewLifecycleOwner) { aList ->
            populateAssetType(aList)
        }
        cardBinding.apply {
            listGeneratedUUID = lists.collateralGeneratedUID
            Log.d("TAG", "onItemSelectedCODE: ${lists.collateralGeneratedUID}")
            etBAddress.setText(lists.name)
            spinnerIsource.setText(lists.assetType, false)
            assetId = lists.assetTypeId
            spinnerIsource.setText(lists.assetType, false)
            etDSale.setText(lists.model)
            etYear.setText(lists.serialNumber)
            val value = FormatDigit.formatDigits(lists.estimateValue.trim())
            etGuarantorAddress.setText(value)
            btnContinue.makeGone()
            clButtons.makeVisible()
            btnCancel.setOnClickListener {
                if (isDeleteItems) {
                    viewmodel.deleteCollateral(lists.id, lists.collateralGeneratedUID)
                    items.removeAt(pos)
                    gAdapter.notifyItemRemoved(pos)
                } else {
                    isDeleteItems = false
                    items.removeAt(pos)
                    customerDocs.removeAt(pos)
                    gAdapter.notifyItemRemoved(pos)
                    if ("${items.size}" >= maximumCollateral) {
                        binding.btnAddCollateral.makeGone()
                    } else {
                        binding.btnAddCollateral.makeVisible()
                    }
                }
                deleteImageFromInternalStorage(
                    requireContext(),
                    "${lists.collateralGeneratedUID}.jpg"
                )
                dialog.dismiss()
            }
            btnUpdate.setOnClickListener {
                val aName = etBAddress.text.toString()
                val sssetType = spinnerIsource.text.toString()
                val aModel = etDSale.text.toString()
                val serialNumber = etYear.text.toString()
                val estValue = etGuarantorAddress.text.toString()
                if (sssetType.isEmpty()) {
                    toastyErrors("Select Asset Name")
                } else if (aName.isEmpty()) {
                    tlBAddress.error = getString(R.string.required)
                } else if (aModel.isEmpty()) {
                    tlDSale.error = getString(R.string.required)
                } else if (serialNumber.isEmpty()) {
                    tlDSale.error = " "
                    tlYear.error = getString(R.string.required)
                } else if (estValue.isEmpty()) {
                    tiGuarantorAddress.error = getString(R.string.required)
                } else {
                    tlDSale.error = ""
                    tlBAddress.error = ""
                    tlYear.error = ""
                    tiGuarantorAddress.error = ""
                    btnCancel.isEnabled = false
                    lists.serialNumber = serialNumber
                    lists.assetTypeId = assetId
                    lists.assetType = spinnerIsource.text.toString()
                    lists.model = aModel
                    lists.name = aName
                    lists.estimateValue = estValue
                    /**check if the collateral has an already coll generated code*/
                    if (lists.collateralGeneratedUID.isNotEmpty()) {
                        collateralImageName = "${lists.collateralGeneratedUID}.jpg"
                        if (customerCollateralDoc.isNotEmpty()) {
                            collEntity.docPath = collateralImageName
                            Log.i("TAG", "imageCallBack: ${Gson().toJson(collEntity)}")
                            Log.i("TAG", "onItemSelectedCODEIm: ${listGeneratedUUID}")
                            Log.i("TAG", "oncustomerDocsCODEIm: ${Gson().toJson(customerDocs)}")
                            customerDocs.mapInPlace {
                                /**IF DOCCODE FROM THE LIST is equal to the doccode for front id, and path at that items has changedd
                                 *  we replace the element /value
                                 * with the new  vale, else if the element has not been modified, we retain it*/
                                    element ->
                                if (element.docCode == collateralDocCode && element.docGeneratedUID == listGeneratedUUID) {
                                    Log.d("TAG", "imageCallBackinside: ${element.docGeneratedUID}")
                                    collEntity
                                } else {
                                    Log.d("TAG", "imageCallBackinside2: ${element}")
                                    element
                                }
                            }
                            if (collateralUri != null) {
                                saveImageToInternalAppStorage(
                                    collateralUri!!,
                                    requireContext(),
                                    collateralImageName
                                )
                            }
                            viewmodel.updateCollateral(lists, collEntity)
                        } else {
                            /**has a uid but there was no document attached,/the document list is empty
                             * so we need to attach a document and give it the current UUID*/
                            val customerDocsEntity = CustomerDocsEntity(
                                0,
                                nationalId,
                                collateralDocCode,
                                lists.collateralGeneratedUID,
                                collateralImageName
                            )
                            viewmodel.updateCollateral(lists, customerDocsEntity)
                            if (isImagePicked) {
                                if (collateralUri != null) {
                                    viewmodel.insertDocument(customerDocsEntity)
                                    customerDocs.add(customerDocsEntity)
                                    saveImageToInternalAppStorage(
                                        collateralUri!!,
                                        requireContext(),
                                        collateralImageName
                                    )
                                }
                            } else {
                                collateralImageName = ""
                                isImagePicked = false
                            }
                        }
                    } else {
                        /**collateral has no generated UUID so we need to generate a new UUID*/
                        val generatedUUID = UUID.randomUUID().toString()
                        lists.collateralGeneratedUID = generatedUUID
                        collateralImageName = generateUniqueCollateralDocName(generatedUUID)
                        val customerDocsEntity = CustomerDocsEntity(
                            0,
                            nationalId,
                            collateralDocCode,
                            generatedUUID,
                            collateralImageName
                        )
                        if (isImagePicked) {
                            if (collateralUri != null) {
                                viewmodel.insertDocument(customerDocsEntity)
                                customerDocs.add(customerDocsEntity)
                                saveImageToInternalAppStorage(
                                    collateralUri!!,
                                    requireContext(),
                                    collateralImageName
                                )
                            }
                        } else {
                            collateralImageName = ""
                            isImagePicked = false
                        }
                        /**it has no uid, so we call this fun to update the uid, on the available collateral list
                         * so incase i exit without saving the document to the room, on attaching a new document it will use this
                         * generated uid to populate the name of new document, so no dublicate images on app internal storage*/
                        //  viewmodel.updateCollateral(lists, customerDocsEntity)

                    }
                    gAdapter.notifyItemChanged(pos)
                    dialog.dismiss()
                }
            }
            customerCollateralDoc.clear()
            /**check to see if the collateralGeneratedCode has an empty string */
            if (lists.collateralGeneratedUID.isNotEmpty()) {
                val colCode =
                    customerDocs.filter { customerDocsEntity -> customerDocsEntity.docGeneratedUID == lists.collateralGeneratedUID }
                customerCollateralDoc.addAll(colCode)
                Log.e("TAG", "onItemSelectedColl: ${Gson().toJson(colCode)}")
                if (customerCollateralDoc.isNotEmpty()) {
                    collEntity = customerCollateralDoc.first()
                    //val uri = Uri.fromFile(File(collEntity.docPath))
                    val imageName = collEntity.docPath
                    if (pattern.containsMatchIn(collEntity.docPath)) {
                        cardBinding.tvAttachDoc.text =
                            getString(R.string.view_collateral_photo)
                        imageUrl = collEntity.docPath
                        docType = collEntity.docCode
                    } else {
                        Log.e("TAG", "uri: $imageName")
                        cardBinding.tvAttachDoc.text =
                            "Collateral Photo - $imageName"
                    }
                } else {
                    cardBinding.tvAttachDoc.text =
                        resources.getString(R.string.attach_collateral_photo)
                }
            } else {
                Log.e("TAG", "onItemSelected:ismpty ")

            }
            tvAttachDoc.setOnClickListener {
                if (tvAttachDoc.text.contains(getString(R.string.view_collateral_photo))) {
                    Log.d("TAG", "imageUrl:$imageUrl, $docType")
                    showEditPhotoDialog(imageUrl)
                } else {
                    showPickerOptionsDialog()
                }
            }
            dialog.setContentView(cardBinding.root)
            dialog.show()

        }
    }

    private fun getSavedItemsFromRoom(parentNationalId: String) {
        /**
         * GlobalScope run globally throughout the app lifecyle
         * tems has changed from index zero to the last position
         *gAdapter.notifyItemRangeChanged(0,items.size)
         * CoroutineScope->run on lifecycle of viewmodels*/
        viewmodel.fetchCustomerDetails(parentNationalId).observe(viewLifecycleOwner) {
            binding.apply {
                maximumCollateral = AppPreferences.maxCollateral.toString()
                isDeleteItems = true
                if (items.isNotEmpty()) {
                    it.collateral.forEach { collateral ->
                        val index = items.indexOfFirst { item -> item.id == collateral.id }
                        if (index >= 0) {
                            items.removeAt(index)
                            items.add(index, collateral)
                            gAdapter.notifyItemChanged(index)
                            val docIndex =
                                customerDocs.indexOfFirst { item -> item.id == collateral.id }
                            Log.d(
                                "TAG",
                                "getSavedItemsFromRoom: Coollllll${collateral.collateralGeneratedUID}"
                            )
                            val docsEntity =
                                it.customerDocs.filter { customerDocsEntity -> customerDocsEntity.docGeneratedUID == collateral.collateralGeneratedUID }
                            if (docIndex >= 0 && docsEntity.isNotEmpty()) {
                                customerDocs.removeAt(docIndex)
                                customerDocs.add(docIndex, docsEntity.first())
                            }
                        }

                    }
                } else {
                    items.clear()
                    items.addAll(it.collateral)
                    gAdapter.notifyDataSetChanged()
                    customerDocs.clear()
                    customerDocs.addAll(it.customerDocs)
                }
                if ("${items.size}" >= maximumCollateral) {
                    btnAddCollateral.makeGone()
                } else {
                    btnAddCollateral.makeVisible()
                }
                guarantor.clear()
                guarantor.addAll(it.guarantors)
                otherBorrowing.clear()
                otherBorrowing.addAll(it.otherBorrowing)
                household.clear()
                household.addAll(it.householdMember)
            }
            Log.d("TAG", "getSavedItemsFromRoomDebugColl: ${Gson().toJson(it)}")
            Log.d("TAG", "getSavedItemsFromRoomDebug4: ${Gson().toJson(it)}")
            Log.d("TAG", "getSavedItemsFromRoomDebugC4: ${Gson().toJson(it.collateral)}")
            Log.d("TAG", "getSavedItemsFromRoomDebugG4: ${Gson().toJson(it.guarantors)}")
            Log.d("TAG", "getSavedItemsFromRoomDebugO4: ${Gson().toJson(it.otherBorrowing)}")
        }

    }

    private fun showEditPhotoDialog(uri: String) {
        cardEditBinding = EditPhotsDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        cardEditBinding.apply {
            Glide.with(requireActivity()).load(uri)
                .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                .into(userLogo)
            tvTitle.text = "Collateral Photo"
            tvEdit.setOnClickListener {
                dialog.dismiss()
                Log.d("TAG", "showEditPhotoDialog: ${collEntity.docPath}")
                showPickerOptionsDialog()
            }
            cardEditBinding.userLogo.setOnClickListener {
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
        dialog.setContentView(cardEditBinding.root)
        dialog.show()

    }


}