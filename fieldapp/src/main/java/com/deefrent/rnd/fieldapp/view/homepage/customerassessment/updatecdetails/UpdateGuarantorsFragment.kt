package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.updatecdetails

import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.adapters.GuarantorAdapter
import com.deefrent.rnd.fieldapp.databinding.AddGuarantorsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.EditPhotsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentGuarantorsBinding
import com.deefrent.rnd.fieldapp.dtos.AddGuarantorsDTO
import com.deefrent.rnd.fieldapp.dtos.RemoveGuarantorDTO
import com.deefrent.rnd.fieldapp.dtos.UpdateGuarantorsDTO
import com.deefrent.rnd.fieldapp.models.customer.CustomerGuarantorDoc
import com.deefrent.rnd.fieldapp.network.models.GuarantorInfo
import com.deefrent.rnd.fieldapp.room.entities.RshipTypeEntity
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.callbacks.GuarantorCallBack
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.viewModels.DropdownItemsViewModel
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.ronnie.github.imagepicker.ImagePicker
import dev.ronnie.github.imagepicker.ImageResult
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*
import kotlin.collections.ArrayList

class UpdateGuarantorsFragment : Fragment(), GuarantorCallBack {
    private lateinit var binding: FragmentGuarantorsBinding
    private lateinit var cardBinding: EditPhotsDialogBinding
    private lateinit var imagePicker: ImagePicker
    private lateinit var guarantorAdapter: GuarantorAdapter
    private lateinit var addGuarantorDialog: AddGuarantorsDialogBinding
    private var national_identity = ""
    private var rshipId = ""
    private var frontIdPath = ""
    private var affidavitPath = ""
    private val arrayList: ArrayList<GuarantorInfo> = ArrayList()
    private var customerDocsList = arrayListOf<CustomerGuarantorDoc>()
    private var customerId = ""
    private var channelGeneratedCode = ""
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(CustomerInfoViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePicker = ImagePicker(fragment = this)

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
                    frontIdPath = if (from == "Camera") {
                        getCameraPath(uri, requireActivity())
                    } else {
                        FileUtil.getPath(uri, requireContext())
                    }
                    addGuarantorDialog.tvAttachGuarantorFrontID.text =
                        "Guarantor Front ID - ${getFileName(uri, requireActivity())}"
                    val guarantorFrontIDDocList =
                        customerDocsList.filter { it.docCode == Constants.guarantorIDDocCode }
                    if (guarantorFrontIDDocList.isEmpty()) {
                        //create a new doc and add it to list
                        val generatedUUID = UUID.randomUUID().toString()
                        val customerDoc = CustomerGuarantorDoc(
                            customerId, Constants.guarantorIDDocCode,
                            frontIdPath, channelGeneratedCode, null
                        )
                        customerDocsList.add(customerDoc)
                    } else {
                        //replace the doc
                        val guarantorFrontIDDoc = guarantorFrontIDDocList[0]
                        guarantorFrontIDDoc.docPath = frontIdPath
                        customerDocsList.mapInPlace {
                            /**IF DOCCODE FROM THE LIST is equal to the doccode for front id, and path at that items has changedd
                             *  we replace the element /value
                             * with the new  vale, else if the element has not been modified, we retain it*/
                                element ->
                            if (element.docCode == Constants.guarantorIDDocCode) guarantorFrontIDDoc else element
                        }
                    }
                } else {
                    affidavitPath = if (from == "Camera") {
                        getCameraPath(uri, requireActivity())
                    } else {
                        FileUtil.getPath(uri, requireContext())
                    }
                    addGuarantorDialog.tvAttachGuarantorAffidavit.text =
                        "Guarantor Affidavit - ${getFileName(uri, requireActivity())}"
                    val guarantorAffidavitDocList =
                        customerDocsList.filter { it.docCode == Constants.guarantorAFFIDAVITDocCode }
                    if (guarantorAffidavitDocList.isEmpty()) {
                        //create a new doc and add it to list
                        val generatedUUID = UUID.randomUUID().toString()
                        val customerDoc = CustomerGuarantorDoc(
                            customerId, Constants.guarantorAFFIDAVITDocCode,
                            affidavitPath, channelGeneratedCode, null
                        )
                        customerDocsList.add(customerDoc)
                    } else {
                        //replace the doc
                        val guarantorAffidavitDoc = guarantorAffidavitDocList[0]
                        guarantorAffidavitDoc.docPath = affidavitPath
                        customerDocsList.mapInPlace {
                            /**IF DOCCODE FROM THE LIST is equal to the doccode for front id, and path at that items has changedd
                             *  we replace the element /value
                             * with the new  vale, else if the element has not been modified, we retain it*/
                                element ->
                            if (element.docCode == Constants.guarantorAFFIDAVITDocCode) guarantorAffidavitDoc else element
                        }
                    }
                }
            }
            is ImageResult.Failure -> {
                val errorString = imageResult.errorString
                Toast.makeText(requireContext(), errorString, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGuarantorsBinding.inflate(layoutInflater)
        initializeUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            tvPbText.makeGone()
            btnContinue.makeGone()
            note.makeGone()
            pb.makeGone()
            btnAddGuarantor.makeGone()
            btnAddGuarantor.setOnClickListener { addGuarantors() }
            binding.apply {
                rvTempGuarantor.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                guarantorAdapter = GuarantorAdapter(arrayList, this@UpdateGuarantorsFragment)
                rvTempGuarantor.adapter = guarantorAdapter
                viewmodel.detailsData.observe(viewLifecycleOwner) {
                    customerId = it.basicInfo.id
                    national_identity = it.basicInfo.idNumber
                    if (it.guarantorInfo.isNotEmpty()) {
                        arrayList.clear()
                        arrayList.addAll(it.guarantorInfo)
                        guarantorAdapter.notifyDataSetChanged()
                        if (it.guarantorInfo.size < 2) {
                            btnAddGuarantor.makeVisible()
                        } else {
                            btnAddGuarantor.makeGone()
                        }
                    } else {
                        note.makeVisible()
                        note.text = getString(R.string.this_customer_has_no_guarantors)
                        btnAddGuarantor.makeVisible()
                    }
                }
            }
        }
    }

    private fun initializeUI() {
        binding.ivBack.setOnClickListener { v ->
            Navigation.findNavController(v)
                .navigateUp()
        }
        binding.btnContinue.setOnClickListener { v ->
            if (arrayList.isEmpty()) {
                toastyErrors("Add guarantors to continue")
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
            tvAttachGuarantorFrontID.makeGone()
            tvAttachGuarantorAffidavit.makeGone()
            btnContinue.setOnClickListener {
                if (isNetworkAvailable(requireContext())) {
                    val validMsg = FieldValidators.VALIDINPUT
                    val phoneNumber =
                        FieldValidators().formatPhoneNumber(addGuarantorDialog.etDSale.text.toString())
                    val validPhone = FieldValidators().validPhoneNUmber(phoneNumber)
                    val gName = etBAddress.text.toString()
                    val gIdNumber = etYear.text.toString()
                    val gResAddress = etGuarantorAddress.text.toString()
                    if (spinnerIsource.text.isEmpty()) {
                        toastyErrors("Select relationship")
                    } else if (gName.isEmpty()) {
                        tlBAddress.error = getString(R.string.required)
                    } else if (!validPhone.contentEquals(validMsg)) {
                        tlDSale.error = validPhone
                    } else if (gIdNumber.isEmpty()) {
                        tlDSale.error = " "
                        tlYear.error = getString(R.string.required)
                    } else if (gResAddress.isEmpty()) {
                        tiGuarantorAddress.error = getString(R.string.required)
                    } else {
                        tlDSale.error = ""
                        tlBAddress.error = ""
                        tlYear.error = ""
                        tiGuarantorAddress.error = ""
                        // guarantorAdapter.notifyItemInserted(0)
                        val addGuarantorsDTO = AddGuarantorsDTO()
                        addGuarantorsDTO.relationshipId = rshipId
                        addGuarantorsDTO.name = gName
                        addGuarantorsDTO.idNumber = gIdNumber
                        addGuarantorsDTO.phone = phoneNumber
                        addGuarantorsDTO.address = gResAddress
                        addGuarantorsDTO.customerIdNumber = national_identity
                        btnContinue.isEnabled = false
                        viewmodel.addGuarantorDetails(addGuarantorsDTO)
                    }
                } else {
                    onNoNetworkDialog(requireContext())
                }
            }
            viewmodel.statusAGCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            toastySuccess("Guarantor details added successfully")
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
            viewmodel.responseAddGStatus.observe(viewLifecycleOwner) {
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
            rshipId = selected.id.toString()
        }
    }

    override fun onItemSelected(pos: Int, items: GuarantorInfo) {
        addGuarantorDialog = AddGuarantorsDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        dropdownItemsViewModel.getAllRshipType().observe(viewLifecycleOwner) { rList ->
            populateRship(rList)
        }
        channelGeneratedCode = items.channelGeneratedCode
        addGuarantorDialog.apply {
            if (items.affidavitDoc != null) {
                if (items.affidavitDoc.url.isNotEmpty()) {
                    tvAttachGuarantorAffidavit.text = (getString(R.string.view_guarantor_affidavit))
                    val customerDoc = CustomerGuarantorDoc(
                        customerId,
                        items.affidavitDoc.code,
                        items.affidavitDoc.url,
                        items.channelGeneratedCode,
                        items.affidavitDoc.documentId
                    )
                    customerDocsList.clear()
                    customerDocsList.add(customerDoc)
                } else {
                    tvAttachGuarantorAffidavit.text =
                        (getString(R.string.attach_guarantor_affidavit))
                }
            }
            if (items.nationalIdDoc != null) {
                if (items.nationalIdDoc.url != null) {
                    tvAttachGuarantorFrontID.text =
                        (getString(R.string.view_guarantor_front_national_id))
                    val customerDoc = CustomerGuarantorDoc(
                        customerId,
                        items.nationalIdDoc.code,
                        items.nationalIdDoc.url,
                        items.channelGeneratedCode,
                        items.nationalIdDoc.documentId
                    )
                    customerDocsList.clear()
                    customerDocsList.add(customerDoc)
                } else {
                    tvAttachGuarantorFrontID.text =
                        (getString(R.string.attach_guarantor_front_national_id))
                }
            }
            /*if (tvAttachGuarantorAffidavit.text.contains(getString(R.string.view_guarantor_affidavit))) {
                tvAttachGuarantorAffidavit.makeVisible()
            } else {
                //tvAttachGuarantorAffidavit.makeGone()
               showPickerOptionsDialog("guarantorAffidavit")
            }*/

            tvAttachGuarantorAffidavit.setOnClickListener {
                if (tvAttachGuarantorAffidavit.text.contains(getString(R.string.view_guarantor_affidavit))) {
                    showEditPhotoDialog(items.affidavitDoc.url, items.affidavitDoc.code)
                } else {
                    showPickerOptionsDialog("guarantorAffidavit")
                }
            }
            tvAttachGuarantorFrontID.setOnClickListener {
                if (tvAttachGuarantorFrontID.text.contains(getString(R.string.view_guarantor_front_national_id))) {
                    showEditPhotoDialog(
                        items.nationalIdDoc.url, items.nationalIdDoc.code
                    )
                } else {
                    showPickerOptionsDialog("guarantorID")
                }
            }
            if (tvAttachGuarantorFrontID.text.contains(getString(R.string.view_guarantor_front_national_id))) {
                tvAttachGuarantorFrontID.makeVisible()
            } else {
                //tvAttachGuarantorFrontID.makeGone()
            }
            etBAddress.setText(items.name)
            etYear.setText(items.idNumber)
            etGuarantorAddress.setText(items.address)
            etDSale.setText(items.phone)
            rshipId = items.relationshipId.toString()
            spinnerIsource.setText(items.relationshipName, false)
            btnContinue.makeGone()
            clButtons.makeVisible()
            btnUpdate.setOnClickListener {
                if (isNetworkAvailable(requireContext())) {
                    val validMsg = FieldValidators.VALIDINPUT
                    val phoneNumber =
                        FieldValidators().formatPhoneNumber(addGuarantorDialog.etDSale.text.toString())
                    val validPhone = FieldValidators().validPhoneNUmber(phoneNumber)
                    val gName = etBAddress.text.toString()
                    val gIdNumber = etYear.text.toString()
                    val gResAddress = etGuarantorAddress.text.toString()
                    if (spinnerIsource.text.isEmpty()) {
                        toastyErrors("Select relationship")
                    } else if (gName.isEmpty()) {
                        tlBAddress.error = getString(R.string.required)
                    } else if (!validPhone.contentEquals(validMsg)) {
                        tlDSale.error = validPhone
                    } else if (gIdNumber.isEmpty()) {
                        tlDSale.error = " "
                        tlYear.error = getString(R.string.required)
                    } else if (gResAddress.isEmpty()) {
                        tiGuarantorAddress.error = getString(R.string.required)
                    } else {
                        tlDSale.error = ""
                        tlBAddress.error = ""
                        tlYear.error = ""
                        tiGuarantorAddress.error = ""
                        val updateGuarantorsDTO = UpdateGuarantorsDTO()
                        updateGuarantorsDTO.address = gResAddress
                        updateGuarantorsDTO.name = gName
                        updateGuarantorsDTO.relationshipId = rshipId.toString()
                        updateGuarantorsDTO.phone = phoneNumber
                        updateGuarantorsDTO.guarantorId = items.guarantorId.toString()
                        updateGuarantorsDTO.idNumber = gIdNumber
                        btnUpdate.isEnabled = false
                        btnCancel.isEnabled = false
                        viewmodel.updateGuarantorDetails(updateGuarantorsDTO)
                    }
                } else {
                    onNoNetworkDialog(requireContext())
                }
            }
            btnCancel.setOnClickListener {
                if (isNetworkAvailable(requireContext())) {
                    val removeGuarantorDTO = RemoveGuarantorDTO()
                    removeGuarantorDTO.guarantorId = items.guarantorId
                    btnUpdate.isEnabled = false
                    btnCancel.isEnabled = false
                    viewmodel.deleteGuarantorDetails(removeGuarantorDTO)
                } else {
                    onNoNetworkDialog(requireContext())
                }
            }
            viewmodel.statusGCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            //toastySuccess("Guarantor details updated successfully")
                            if (customerDocsList.isNotEmpty()) {
                                initiateDocumentsUpload()
                            }
                        }
                        0 -> {
                            onInfoDialog(viewmodel.statusMessage.value)
                            btnUpdate.isEnabled = true
                            viewmodel.stopObserving()
                        }
                        else -> {
                            onInfoDialog(getString(R.string.error_occurred))
                            btnUpdate.isEnabled = true
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
                            toastySuccess("Guarantor details Updated successfully")
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
            viewmodel.statusRGCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            arrayList.removeAt(pos)
                            guarantorAdapter.notifyItemRemoved(pos)
                            dialog.dismiss()
                            toastySuccess(" ${items.name} details deleted successfully")
                            btnUpdate.isEnabled = false
                            btnCancel.isEnabled = true
                            viewmodel.stopObserving()
                        }
                        0 -> {
                            onInfoDialog(viewmodel.statusMessage.value)
                            btnUpdate.isEnabled = true
                            btnCancel.isEnabled = true
                            viewmodel.stopObserving()
                        }
                        else -> {
                            onInfoDialog(getString(R.string.error_occurred))
                            btnUpdate.isEnabled = true
                            btnCancel.isEnabled = true
                            viewmodel.stopObserving()
                        }
                    }
                }
            }
            viewmodel.responseRemGStatus.observe(viewLifecycleOwner) {
                Log.d("TAG", "responseRemGStatus: $it")
                if (null != it) {
                    when (it) {
                        GeneralResponseStatus.LOADING -> {
                            btnUpdate.isEnabled = false
                            btnCancel.isEnabled = true
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
            viewmodel.responseUpGStatus.observe(viewLifecycleOwner) {
                Log.d("TAG", "responseUpGStatus: $it")
                if (null != it) {
                    when (it) {
                        GeneralResponseStatus.LOADING -> {
                            btnUpdate.isEnabled = false
                            btnCancel.isEnabled = true
                            progressbar.mainPBar.makeVisible()
                            viewmodel.stopObserving()
                        }
                        GeneralResponseStatus.DONE -> {
                            //dialog.dismiss()
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
            viewmodel.responseUpGDocStatus.observe(viewLifecycleOwner) {
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
            dialog.setContentView(addGuarantorDialog.root)
            dialog.show()
        }
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
                        val success = viewmodel.uploadCustomerGuarantorDocs(
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
                    onInfoSuccessDialog("Customer guarantor details updated successfully")
                    viewmodel.stopObserving()
                }
            }
        }

    }

    private fun showEditPhotoDialog(url: String, docCode: String) {
        cardBinding = EditPhotsDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        cardBinding.apply {
            if (docCode == Constants.guarantorAFFIDAVITDocCode) {
                Glide.with(requireActivity()).load(url)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                    .into(userLogo)
                tvTitle.text = "Affidavit Document"
                //tvEdit.makeGone()
                tvEdit.setOnClickListener {
                    dialog.dismiss()
                    showPickerOptionsDialog("guarantorAffidavit")
                }
            } else {
                Glide.with(requireActivity()).load(url)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                    .into(userLogo)
                tvTitle.text = "Front ID Document"
                //tvEdit.makeGone()
                tvEdit.setOnClickListener {
                    dialog.dismiss()
                    showPickerOptionsDialog("guarantorID")
                }
            }
            cardBinding.userLogo.setOnClickListener {
                val mBuilder: AlertDialog.Builder =
                    AlertDialog.Builder(context, R.style.WrapContentDialog)
                val mView: View =
                    layoutInflater.inflate(R.layout.preview_image, null)
                val ivImagePreview = mView.findViewById<PhotoView>(R.id.iv_preview_image)
                Glide.with(requireActivity()).load(url)
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
        dialog.setContentView(cardBinding.root)
        dialog.show()

    }

}