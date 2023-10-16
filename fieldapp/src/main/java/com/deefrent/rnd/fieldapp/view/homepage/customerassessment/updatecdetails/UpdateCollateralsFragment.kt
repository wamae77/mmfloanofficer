package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.updatecdetails

import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.adapters.UpdateCollateralAdapter
import com.deefrent.rnd.fieldapp.databinding.AddCollateralDialogBinding
import com.deefrent.rnd.fieldapp.databinding.EditPhotsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentCollateralsBinding
import com.deefrent.rnd.fieldapp.dtos.AddCollateralDTO
import com.deefrent.rnd.fieldapp.dtos.DeleteCollateralDTO
import com.deefrent.rnd.fieldapp.dtos.UpdateCollateralDTO
import com.deefrent.rnd.fieldapp.models.customer.CustomerCollateralDoc
import com.deefrent.rnd.fieldapp.network.models.CollateralInfo
import com.deefrent.rnd.fieldapp.room.entities.AssetTypeEntity
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.callbacks.CollateralCallBack
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.viewModels.DropdownItemsViewModel
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.ronnie.github.imagepicker.ImagePicker
import dev.ronnie.github.imagepicker.ImageResult
import es.dmoral.toasty.Toasty
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*
import kotlin.collections.ArrayList

class UpdateCollateralsFragment : Fragment(), CollateralCallBack {
    private lateinit var binding: FragmentCollateralsBinding
    private lateinit var cardEditBinding: EditPhotsDialogBinding
    private lateinit var collateralAdapter: UpdateCollateralAdapter
    private val arrayList: ArrayList<CollateralInfo> = ArrayList()
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(CustomerInfoViewModel::class.java)
    }
    private lateinit var cardBinding: AddCollateralDialogBinding
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private var national_identity = ""
    private var assetId = ""
    private lateinit var imagePicker: ImagePicker
    private var customerDocsList = arrayListOf<CustomerCollateralDoc>()
    private var customerId = ""
    private var channelGeneratedCode = ""
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
                val path: String = if (from == "Camera") {
                    getCameraPath(uri, requireActivity())
                } else {
                    FileUtil.getPath(uri, requireContext())
                }
                cardBinding.tvAttachDoc.text =
                    "Collateral Photo - ${getFileName(uri, requireActivity())}"
                val collateralDocList =
                    customerDocsList.filter { it.docCode == Constants.collateralDocCode }
                if (collateralDocList.isEmpty()) {
                    //create a new doc and add it to list
                    val generatedUUID = UUID.randomUUID().toString()
                    val customerDoc = CustomerCollateralDoc(
                        customerId, Constants.collateralDocCode,
                        path, channelGeneratedCode, null
                    )
                    customerDocsList.add(customerDoc)
                } else {
                    //replace the doc
                    val collateralDoc = collateralDocList[0]
                    collateralDoc.docPath = path
                    customerDocsList.mapInPlace {
                        /**IF DOCCODE FROM THE LIST is equal to the doccode for front id, and path at that items has changedd
                         *  we replace the element /value
                         * with the new  vale, else if the element has not been modified, we retain it*/
                            element ->
                        if (element.docCode == Constants.residenceDocCode) collateralDoc else element
                    }
                }
            }
            is ImageResult.Failure -> {
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
            Navigation.findNavController(v)
                .navigateUp()
        }
        binding.btnContinue.makeGone()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            tvPbText.makeGone()
            note.makeGone()
            pb.makeGone()
            btnAddCollateral.makeGone()
            btnAddCollateral.setText("Add Collateral")
            /*if (items.isEmpty()){
                note.makeVisible()
                rvAddCollateral.makeGone()
            }else{
                note.makeGone()
                rvAddCollateral.makeVisible()
            }*/
            btnAddCollateral.setOnClickListener { addCollateral() }
            rvAddCollateral.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            collateralAdapter =
                UpdateCollateralAdapter(arrayList, requireContext(), this@UpdateCollateralsFragment)
            rvAddCollateral.adapter = collateralAdapter
            viewmodel.detailsData.observe(viewLifecycleOwner) {
                customerId = it.basicInfo.id
                national_identity = it.basicInfo.idNumber
                if (it.collateralInfo.isNotEmpty()) {
                    btnContinue.isEnabled = true
                    arrayList.clear()
                    arrayList.addAll(it.collateralInfo)
                    collateralAdapter.notifyDataSetChanged()
                    if (it.collateralInfo.size > 3) {
                        btnAddCollateral.makeGone()
                    } else {
                        btnAddCollateral.makeVisible()
                    }
                } else {
                    note.makeVisible()
                    btnContinue.isEnabled = false
                    note.text = getString(R.string.this_customer_has_no_collateral)
                    btnAddCollateral.makeVisible()
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
            tvAttachDoc.makeGone()
            btnContinue.setOnClickListener {
                if (isNetworkAvailable(requireContext())) {
                    val aName = etBAddress.text.toString()
                    val aModel = etDSale.text.toString()
                    val serialNumber = etYear.text.toString()
                    val estValue = etGuarantorAddress.text.toString()
                    if (spinnerIsource.text.isEmpty()) {
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
                        val addCollateralDTO = AddCollateralDTO()
                        addCollateralDTO.assetTypeId = assetId.toString()
                        addCollateralDTO.customerIdNumber = national_identity
                        addCollateralDTO.estimatedValue = etGuarantorAddress.text.toString()
                        addCollateralDTO.model = etDSale.text.toString()
                        addCollateralDTO.serialNumber = etYear.text.toString()
                        addCollateralDTO.name = etBAddress.text.toString()
                        btnContinue.isEnabled = false
                        viewmodel.addCollateralDetails(addCollateralDTO)
                    }
                } else {
                    onNoNetworkDialog(requireContext())
                }
            }
            viewmodel.status.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            toastySuccess("Collateral details added successfully")
                            viewmodel.stopObserving()
                        }
                        0 -> {
                            onInfoDialog(viewmodel.statusMessage.value)
                            viewmodel.stopObserving()
                        }
                        else -> {
                            onInfoDialog(getString(R.string.error_occurred))
                            viewmodel.stopObserving()
                        }
                    }
                }
            }
            viewmodel.responseAddCoStatus.observe(viewLifecycleOwner) {
                Log.d("TAG", "responseAddGStatus: $it")
                if (null != it) {
                    when (it) {
                        GeneralResponseStatus.LOADING -> {
                            btnContinue.isEnabled = false
                            progressbar.mainPBar.makeVisible()
                            viewmodel.stopObserving()
                        }
                        GeneralResponseStatus.DONE -> {
                            dialog.dismiss()
                            btnContinue.isEnabled = true
                            progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                        GeneralResponseStatus.ERROR -> {
                            btnContinue.isEnabled = true
                            progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
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

    override fun onItemSelected(pos: Int, items: CollateralInfo) {
        cardBinding = AddCollateralDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        dropdownItemsViewModel.getAllAssetType().observe(viewLifecycleOwner) { aList ->
            Log.d("TAG", "addCollateral: $aList")
            populateAssetType(aList)
        }
        cardBinding.apply {
            if (tvAttachDoc.text.contains(getString(R.string.view_collateral_photo))) {
                tvAttachDoc.makeVisible()
            } else {
                //tvAttachDoc.makeGone()
            }
            channelGeneratedCode = items.channelGeneratedCode
            if (items.document != null) {
                if (items.document.url.isNotEmpty()) {
                    tvAttachDoc.text = getString(R.string.view_collateral_photo)
                    val customerDoc = CustomerCollateralDoc(
                        customerId, items.document.code,
                        items.document.url, items.channelGeneratedCode, items.document.documentId
                    )
                    customerDocsList.clear()
                    customerDocsList.add(customerDoc)
                } else {
                    tvAttachDoc.text = getString(R.string.attach_collateral_photo)
                }
            }
            tvAttachDoc.setOnClickListener { v ->
                if (tvAttachDoc.text.contains(getString(R.string.view_collateral_photo))) {
                    showEditPhotoDialog(items.document.url)
                } else {
                    showPickerOptionsDialog()
                }
            }
            etBAddress.setText(items.name)
            spinnerIsource.setText(items.assetTypeName, false)
            assetId = items.assetTypeId
            etDSale.setText(items.model)
            etYear.setText(items.serialNumber)
            val value = FormatDigit.formatDigits(items.estimatedValue.toString().trim())
            etGuarantorAddress.setText(value)
            btnContinue.makeGone()
            clButtons.makeVisible()
            btnCancel.setOnClickListener {
                if (isNetworkAvailable(requireContext())) {
                    val deleteCollateralDTO = DeleteCollateralDTO()
                    deleteCollateralDTO.collateralId = items.collateralId
                    btnUpdate.isEnabled = false
                    btnCancel.isEnabled = false
                    viewmodel.deleteCollateralDetails(deleteCollateralDTO)
                } else {
                    toastyErrors("Check your internet connection and try again")
                }
            }

            btnUpdate.setOnClickListener {
                if (isNetworkAvailable(requireContext())) {
                    val aName = etBAddress.text.toString()
                    val aModel = etDSale.text.toString()
                    val serialNumber = etYear.text.toString()
                    val estValue = etGuarantorAddress.text.toString()
                    if (spinnerIsource.text.isEmpty()) {
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
                        val updateCollateralDTO = UpdateCollateralDTO()
                        updateCollateralDTO.assetTypeId = assetId.toString()
                        updateCollateralDTO.collateralId = items.collateralId
                        updateCollateralDTO.estimatedValue = etGuarantorAddress.text.toString()
                        updateCollateralDTO.serialNumber = etYear.text.toString()
                        updateCollateralDTO.model = etDSale.text.toString()
                        updateCollateralDTO.name = etBAddress.text.toString()
                        viewmodel.updateCollateralDetails(updateCollateralDTO)
                    }
                } else {
                    onNoNetworkDialog(requireContext())
                }
            }
            /**update*/
            viewmodel.statusCoCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            //toastySuccess("Collateral details Updated successfully")
                            if (customerDocsList.isNotEmpty()) {
                                initiateDocumentsUpload()
                            }
                            viewmodel.stopObserving()
                        }
                        0 -> {
                            onInfoDialog(viewmodel.statusMessage.value)
                            viewmodel.stopObserving()
                        }
                        else -> {
                            onInfoDialog(getString(R.string.error_occurred))
                            viewmodel.stopObserving()
                        }
                    }
                }
            }
            viewmodel.statusDocCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            //Log.e("TAG", "initializeUI: ${Gson().toJson(customerDetailEntity)}")
                            toastySuccess("Collateral details Updated successfully")
                            viewmodel.stopObserving()
                        }
                        0 -> {
                            viewmodel.stopObserving()
                            onInfoDialog(viewmodel.statusMessage.value)
                        }
                        else -> {
                            findNavController().navigateUp()
                            toastyErrors("Error Occurred while uploading the documents...\nkindly try again later...")
                            viewmodel.stopObserving()

                        }
                    }
                }
            }
            viewmodel.responseUpCoStatus.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        GeneralResponseStatus.LOADING -> {
                            btnUpdate.isEnabled = false
                            progressbar.mainPBar.makeVisible()
                            viewmodel.stopObserving()
                        }
                        GeneralResponseStatus.DONE -> {
                            btnUpdate.isEnabled = true
                            progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                        GeneralResponseStatus.ERROR -> {
                            btnUpdate.isEnabled = true
                            progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                    }
                }
            }
            viewmodel.responseUpCoDocStatus.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        GeneralResponseStatus.LOADING -> {
                            btnUpdate.isEnabled = false
                            progressbar.mainPBar.makeVisible()
                            progressbar.tvWait.text = "Uploading documents..."
                            viewmodel.stopObserving()
                        }
                        GeneralResponseStatus.DONE -> {
                            dialog.dismiss()
                            btnUpdate.isEnabled = true
                            progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                        GeneralResponseStatus.ERROR -> {
                            btnUpdate.isEnabled = true
                            progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                    }
                }
            }
            /**delete*/
            viewmodel.statusRGCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            arrayList.removeAt(pos)
                            collateralAdapter.notifyItemRemoved(pos)
                            toastySuccess("Collateral details has been deleted successfully")
                            viewmodel.stopObserving()
                        }
                        0 -> {
                            onInfoDialog(viewmodel.statusMessage.value)
                            viewmodel.stopObserving()
                        }
                        else -> {
                            onInfoDialog(getString(R.string.error_occurred))
                            viewmodel.stopObserving()
                        }
                    }
                }
            }
            viewmodel.responseDelCoStatus.observe(viewLifecycleOwner) {
                Log.d("TAG", "responseAddGStatus: $it")
                if (null != it) {
                    when (it) {
                        GeneralResponseStatus.LOADING -> {
                            btnUpdate.isEnabled = false
                            btnCancel.isEnabled = false
                            progressbar.mainPBar.makeVisible()
                            viewmodel.stopObserving()
                        }
                        GeneralResponseStatus.DONE -> {
                            dialog.dismiss()
                            btnUpdate.isEnabled = true
                            btnCancel.isEnabled = true
                            progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                        GeneralResponseStatus.ERROR -> {
                            btnUpdate.isEnabled = true
                            btnCancel.isEnabled = true
                            progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                    }
                }
            }
        }
        dialog.setContentView(cardBinding.root)
        dialog.show()
    }

    private fun initiateDocumentsUpload() {
        /**run within the lifecycle of the view, if its destoryed it stop uploading */
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {

            var count = 0

            /**incase of an error occured while uplading image to stop uploading*/
            val filteredList = customerDocsList.filter { customerDoc ->
                customerDoc.docPath.isNotEmpty() && !Constants.pattern.containsMatchIn(
                    customerDoc.docPath
                )
            }
            if (filteredList.isNotEmpty()) {
                Log.e("TAG", "initiateDocumentsUploadfilteredList: $filteredList")
                val lastIndex = filteredList.size.minus(1)
                while (count < filteredList.size) {
                    val customerDoc = filteredList[count]
                    if (customerDoc.docPath.isNotEmpty() && !Constants.pattern.containsMatchIn(
                            customerDoc.docPath
                        )
                    ) {
                        val contextWrapper = ContextWrapper(requireContext())
                        // return a directory in internal storage
                        val directory =
                            contextWrapper.getDir(Constants.IMAGES_DIR, Context.MODE_PRIVATE)
                        val location = customerDoc.docPath
                        val compressedImages =
                            Compressor.compress(requireContext(), convertPathToFile(location))
                        val file = MultipartBody.Part.createFormData(
                            "file",
                            compressedImages.name, RequestBody.create(
                                "multipart/form-data".toMediaTypeOrNull(),
                                compressedImages
                            )
                        )
                        val customerId = RequestBody.create(
                            MultipartBody.FORM,
                            customerDoc.customerID
                        )
                        val docTypeCode =
                            RequestBody.create(MultipartBody.FORM, customerDoc.docCode)
                        val channelGeneratedCode =
                            RequestBody.create(
                                MultipartBody.FORM,
                                customerDoc.channelGeneratedCode
                            )
                        Log.d("TAG", "initiateDocumentsUpload:${customerDoc.channelGeneratedCode} ")
                        val success = viewmodel.uploadCustomerCollateralDocs(
                            customerId, docTypeCode, channelGeneratedCode,
                            file, count == lastIndex
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
                withContext(Dispatchers.Main) {
                    onInfoSuccessDialog("Customer collateral details updated successfully")
                    viewmodel.stopObserving()
                }
            }
        }

    }

    private fun showEditPhotoDialog(uri: String) {
        cardEditBinding = EditPhotsDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        cardEditBinding.apply {
            Glide.with(requireActivity()).load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                .into(userLogo)
            //  if (docType== educationDocCode){
            tvTitle.text = "Collateral Photo"
            //tvEdit.makeGone()
            tvEdit.setOnClickListener {
                dialog.dismiss()
                showPickerOptionsDialog()
            }

            //   }
            cardEditBinding.userLogo.setOnClickListener {
                val mBuilder: AlertDialog.Builder =
                    AlertDialog.Builder(context, R.style.WrapContentDialog)
                val mView: View = layoutInflater.inflate(R.layout.preview_image, null)
                val ivImagePreview = mView.findViewById<PhotoView>(R.id.iv_preview_image)
                Glide.with(requireActivity()).load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
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