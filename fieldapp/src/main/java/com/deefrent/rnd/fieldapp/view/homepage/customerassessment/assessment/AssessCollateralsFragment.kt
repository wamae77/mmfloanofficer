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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.data.adapters.AssessCollateralAdapter
import com.deefrent.rnd.fieldapp.databinding.AddCollateralDialogBinding
import com.deefrent.rnd.fieldapp.databinding.EditPhotsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentCollateralsBinding
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.collateralDocCode
import com.deefrent.rnd.fieldapp.utils.callbacks.AssessColCallBack
import com.deefrent.rnd.fieldapp.viewModels.AssessmentDashboardViewModel
import com.deefrent.rnd.fieldapp.viewModels.DropdownItemsViewModel
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import dev.ronnie.github.imagepicker.ImagePicker
import dev.ronnie.github.imagepicker.ImageResult
import es.dmoral.toasty.Toasty
import java.util.*
import kotlin.properties.Delegates

class AssessCollateralsFragment : Fragment(), AssessColCallBack {
    private lateinit var binding: FragmentCollateralsBinding
    private lateinit var cardEditBinding: EditPhotsDialogBinding
    private lateinit var collateralAdapter: AssessCollateralAdapter
    private var guarantor = arrayListOf<AssessGuarantor>()
    private var coll: ArrayList<AssessCollateral> = ArrayList()
    private var household = arrayListOf<AssessHouseholdMemberEntity>()
    private var otherBorrowing = arrayListOf<AssessBorrowing>()
    private var customerDocs = arrayListOf<AssessCustomerDocsEntity>()
    private var isDeleteItems: Boolean = false
    private var maximumCollateral by Delegates.notNull<Int>()
    private lateinit var collEntity: AssessCustomerDocsEntity
    private var imageUrl = ""
    private var docType = ""
    private lateinit var generatedUUID: String
    private var collateralImageName = ""
    private var collateralUri: Uri? = null
    private var isImagePicked = false
    private lateinit var listGeneratedUUID: String
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(AssessmentDashboardViewModel::class.java)
    }
    private lateinit var cardBinding: AddCollateralDialogBinding
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private var national_identity = ""
    private var assetId = ""
    lateinit var imagePicker: ImagePicker
    private var customerCollateralDoc: ArrayList<AssessCustomerDocsEntity> = arrayListOf()
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

        return binding.root
    }


    private fun initializeUI() {
        binding.ivBack.setOnClickListener { v ->
            findNavController().navigate(R.id.assessBusinessAddressFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackButton()
        binding.apply {
            binding.note.makeVisible()
            btnAddCollateral.setOnClickListener { addCollateral() }
            rvAddCollateral.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            collateralAdapter =
                AssessCollateralAdapter(coll, requireContext(), this@AssessCollateralsFragment)
            rvAddCollateral.adapter = collateralAdapter
            viewmodel.parentId.observe(viewLifecycleOwner) {
                national_identity = it
                getSavedItemsFromRoom(it)
            }
            btnContinue.setOnClickListener {
                if (coll.isEmpty()) {
                    toastyErrors("Add Collateral to Continue")
                } else {
                    viewmodel.assessCustomerEntity.observe(viewLifecycleOwner) { detailsEntity ->
                        detailsEntity.apply {
                            lastStep = "AssessCollateralsFragment"
                            isComplete = false
                            hasFinished = false
                            isProcessed = false
                            viewmodel.assessCustomerEntity.postValue(detailsEntity)
                            saveAssessmentDataLocally(detailsEntity, coll)
                        }
                    }
                    findNavController().navigate(R.id.action_assessCollateralsFragment_to_assessresidential)
                }
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
                    aModel.isEmpty() -> {
                        tlDSale.error = getString(R.string.required)
                    }
                    serialNumber.isEmpty() -> {
                        tlDSale.error = " "
                        tlYear.error = getString(R.string.required)
                    }
                    estValue.isEmpty() -> {
                        tiGuarantorAddress.error = getString(R.string.required)
                    }
                    tvAttachDoc.text.toString() == resources.getString(R.string.attach_collateral_photo) -> {
                        toastyErrors(getString(R.string.attach_collateral_photo))
                    }
                    else -> {
                        tlDSale.error = ""
                        tlBAddress.error = ""
                        tlYear.error = ""
                        tiGuarantorAddress.error = ""
                        generatedUUID = UUID.randomUUID().toString()
                        collateralImageName = generateUniqueCollateralDocName(generatedUUID)
                        val customerDocsEntity = AssessCustomerDocsEntity(
                            0,
                            national_identity, collateralDocCode,
                            generatedUUID,
                            collateralImageName
                        )
                        val colla = AssessCollateral(
                            0, national_identity,
                            assetId,
                            spinnerIsource.text.toString(),
                            etGuarantorAddress.text.toString(),
                            etDSale.text.toString(),
                            etBAddress.text.toString(),
                            etYear.text.toString(),
                            generatedUUID,
                        )
                        coll.add( colla)
                        isDeleteItems = false
                        if (isImagePicked) {
                            customerDocs.add(customerDocsEntity)
                            if (collateralUri != null) {
                                viewmodel.insertSingleCollateral(colla)
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
                        if (coll.size >= maximumCollateral) {
                            binding.btnAddCollateral.makeGone()
                        }
                        collateralAdapter.notifyItemInserted(0)
                        collateralAdapter.notifyDataSetChanged()
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
            assetId = selected.id.toString()
        }
    }

    private fun saveAssessmentDataLocally(
        assessCustomerEntity: AssessCustomerEntity,
        coll: List<AssessCollateral>
    ) {
        viewmodel.insertAssessmentData(
            assessCustomerEntity, customerDocs,
            coll,
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
                    findNavController().navigate(R.id.assessBusinessAddressFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    override fun onItemSelected(pos: Int, lists: AssessCollateral) {
        cardBinding = AddCollateralDialogBinding.inflate(layoutInflater)
        //no need to generate a new UID on picking image if it already exists
        generatedUUID = lists.collateralGeneratedUID
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        dropdownItemsViewModel.getAllAssetType().observe(viewLifecycleOwner) { aList ->
            Log.d("TAG", "addCollateral: $aList")
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
                    coll.removeAt(pos)
                    collateralAdapter.notifyItemRemoved(pos)
                } else {
                    isDeleteItems = false
                    coll.removeAt(pos)
                    customerDocs.removeAt(pos)
                    collateralAdapter.notifyItemRemoved(pos)
                    if (coll.size >= maximumCollateral) {
                        binding.btnAddCollateral.makeGone()
                    } else {
                        binding.btnAddCollateral.makeVisible()
                    }
                }
                deleteImageFromInternalStorage(requireContext(), "${lists.collateralGeneratedUID}.jpg")
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
                    if(lists.collateralGeneratedUID.isNotEmpty()) {
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
                        }else{
                            /**has a uid but there was no document attached,/the document list is empty
                             * so we need to attach a document and give it the current UUID*/
                            val customerDocsEntity = AssessCustomerDocsEntity(
                                0,
                                national_identity,
                                collateralDocCode,
                                lists.collateralGeneratedUID,
                                collateralImageName)
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
                            }else{
                                collateralImageName = ""
                                isImagePicked = false
                            }
                        }
                    }else {
                        /**collateral has no generated UUID so we need to generate a new UUID*/
                        val generatedUUID = UUID.randomUUID().toString()
                        lists.collateralGeneratedUID=generatedUUID
                        collateralImageName = generateUniqueCollateralDocName(generatedUUID)
                        val customerDocsEntity = AssessCustomerDocsEntity(
                            0,
                            national_identity,
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
                        }else{
                            collateralImageName = ""
                            isImagePicked = false
                        }
                        /**it has no uid, so we call this fun to update the uid, on the available collateral list
                         * so incase i exit without saving the document to the room, on attaching a new document it will use this
                         * generated uid to populate the name of new document, so no dublicate images on app internal storage*/
                      //  viewmodel.updateCollateral(lists, customerDocsEntity)

                    }
                    collateralAdapter.notifyItemChanged(pos)
                    dialog.dismiss()
                }
            }
            customerCollateralDoc.clear()
            /**check to see if the collateralGeneratedCode has an empty string */
            if (lists.collateralGeneratedUID.isNotEmpty()){
                val colCode =customerDocs.filter { customerDocsEntity -> customerDocsEntity.docGeneratedUID == lists.collateralGeneratedUID }
                customerCollateralDoc.addAll(colCode)
                Log.e("TAG", "onItemSelectedColl: ${Gson().toJson(colCode)}")
                if (customerCollateralDoc.isNotEmpty()) {
                    collEntity = customerCollateralDoc.first()
                    //val uri = Uri.fromFile(File(collEntity.docPath))
                    if (collEntity.docPath.isNotEmpty()){
                    val imageName = collEntity.docPath
                    if (Constants.pattern.containsMatchIn(collEntity.docPath)) {
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
                    cardBinding.tvAttachDoc.text =
                        resources.getString(R.string.attach_collateral_photo)
                }
            }else{
                Log.e("TAG", "onItemSelected:ismpty ", )

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

    private fun getSavedItemsFromRoom(parentId: String) {
        viewmodel.fetchCustomerDetails(parentId).observe(viewLifecycleOwner) {
            binding.apply {
                binding.note.text = "Note: You must have a minimum of ${it.assessCustomerEntity.minimumCollateral} collaterals"
                maximumCollateral=if (it.assessCustomerEntity.maximumColateral.isNotEmpty()) it.assessCustomerEntity.maximumColateral.toInt() else 3
                isDeleteItems = true
                if (coll.isNotEmpty()) {
                    it.assessCollateral.forEach { collateral ->
                        val index = coll.indexOfFirst { item -> item.id == collateral.id }
                        if (index >= 0) {
                            coll.removeAt(index)
                            coll.add(index, collateral)
                            collateralAdapter.notifyItemChanged(index)
                            val docIndex =
                                customerDocs.indexOfFirst { item -> item.id == collateral.id }
                            val docsEntity =
                                it.customerDocs.filter { customerDocsEntity -> customerDocsEntity.docGeneratedUID == collateral.collateralGeneratedUID }
                            if (docIndex >= 0 && docsEntity.isNotEmpty()) {
                                customerDocs.removeAt(docIndex)
                                customerDocs.add(docIndex, docsEntity.first())
                            }
                        }

                    }
                } else {
                    coll.clear()
                    coll.addAll(it.assessCollateral)
                    collateralAdapter.notifyDataSetChanged()
                    customerDocs.clear()
                    customerDocs.addAll(it.customerDocs)
                }
                if (coll.size >= maximumCollateral) {
                    btnAddCollateral.makeGone()
                } else {
                    btnAddCollateral.makeVisible()
                }
                guarantor.clear()
                guarantor.addAll(it.assessGua)
                otherBorrowing.clear()
                otherBorrowing.addAll(it.assessBorrow)
                household.clear()
                household.addAll(it.householdMember)
            }
            Log.d("TAG", "getSavedItemsFromRoomDebugColl: ${Gson().toJson(it)}")
            Log.d("TAG", "getSavedItemsFromRoomDebug4: ${Gson().toJson(it.assessCollateral)}")
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