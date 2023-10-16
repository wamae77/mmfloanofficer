package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.updatecdetails

import android.app.AlertDialog
import android.app.DatePickerDialog
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
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.EditPhotsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentCustomerDetails2Binding
import com.deefrent.rnd.fieldapp.dtos.UpdateBasicInfoDTO
import com.deefrent.rnd.fieldapp.models.customer.CustomerDoc
import com.deefrent.rnd.fieldapp.network.models.Gender
import com.deefrent.rnd.fieldapp.utils.*
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
import java.text.SimpleDateFormat
import java.util.*

class UpdateCustomerDetailsFragment : Fragment() {
    private lateinit var binding: FragmentCustomerDetails2Binding
    private lateinit var cardBinding: EditPhotsDialogBinding
    private lateinit var imagePicker: ImagePicker
    private var frontIDUri: Uri? = null
    private var passportPhotoUri: Uri? = null
    private var customerDocsList = arrayListOf<CustomerDoc>()
    private var editFrontID = false
    private var editPassport = false
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(CustomerInfoViewModel::class.java)
    }

    private var genderId = ""
    private var customerphone = ""
    private var nationalid = ""
    private var customerId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePicker = ImagePicker(fragment = this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCustomerDetails2Binding.inflate(layoutInflater)
        initializeUI()
        binding.apply {
            btnContinue.text = "Update"
            tvPbText.makeGone()
            pb.makeGone()
        }

        binding.rbMyself.isChecked = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            rbMyself.isChecked = true
            if (tvAttachFrontIDDoc.text.contains("View Front ID Photo")) {
                tvAttachFrontIDDoc.makeVisible()
            } else {
                //tvAttachFrontIDDoc.makeGone()
                //showPickerOptionsDialog("CustomerID")
            }

            if (tvAttachPassport.text.contains("View Passport Photo")) {
                tvAttachPassport.makeVisible()
            } else {
                //tvAttachPassport.makeGone()
                //showPickerOptionsDialog("PassportPhoto")
            }

            /*tvAttachFrontIDDoc.setOnClickListener {
                if(tvAttachFrontIDDoc.text=="Attach Customer Front ID"){
                    showPickerOptionsDialog("Camera")
                }
            }

            tvAttachPassport.setOnClickListener {
                if(tvAttachPassport.text=="Attach Passport Size Photo"){
                    showPickerOptionsDialog("PassportSize")
                }
            }*/

            viewmodel.detailsData.observe(viewLifecycleOwner) {
                customerId = it.basicInfo.id
                customerphone = it.basicInfo.phone
                etFname.setText(it.basicInfo.firstName)
                etSurname.setText(it.basicInfo.lastName)
                etIdNo.setText(it.basicInfo.idNumber)
                etDob.setText(it.basicInfo.dob)
                binding.spinnerGender.setText(it.basicInfo.gender, false)
                genderId = it.basicInfo.genderId
                // genderValue=it.basicInfo.gender
                etEmail.setText(it.basicInfo.email)
                if (it.basicInfo.spouseName.isNotEmpty() || it.basicInfo.spousePhone.isNotEmpty()) {
                    rbMyself.isChecked = true
                    rbOthers.isChecked = false
                    etSpName.setText(it.basicInfo.spouseName)
                    etSpousePhone.setText(it.basicInfo.spousePhone)
                } else {
                    rbMyself.isChecked = false
                    rbOthers.isChecked = true
                }
                Log.d("TAG", "onViewCreated DOC: ${it.basicInfo.passportDoc}")
                Log.d("TAG", "onViewCreated DOC: ${it.basicInfo.frontIdDoc}")
                if (it.basicInfo.passportDoc != null) {
                    if (it.basicInfo.passportDoc.url.isNotEmpty()) {
                        val customerDoc = CustomerDoc(
                            it.basicInfo.id, it.basicInfo.passportDoc.code,
                             it.basicInfo.passportDoc.url, it.basicInfo.passportDoc.documentId
                        )
                        customerDocsList.add(customerDoc)
                        tvAttachPassport.text = "View Passport Photo"
                    } else {
                        //tvAttachPassport.makeGone()
                        tvAttachPassport.text = "Attach Customer Front ID"
                    }
                }
                if (it.basicInfo.frontIdDoc != null) {
                    if (it.basicInfo.frontIdDoc.url.isNotEmpty()) {
                        val customerDoc = CustomerDoc(
                            it.basicInfo.id, it.basicInfo.frontIdDoc.code,
                             it.basicInfo.frontIdDoc.url, it.basicInfo.frontIdDoc.documentId
                        )
                        customerDocsList.add(customerDoc)
                        tvAttachFrontIDDoc.text = "View Front ID Photo"
                    } else {
                        //tvAttachFrontIDDoc.makeGone()
                        tvAttachFrontIDDoc.text = "Attach Customer Front ID"
                    }
                }

                tvAttachFrontIDDoc.setOnClickListener { v ->
                    if (tvAttachFrontIDDoc.text.contains("View Front ID Photo")) {
                        showEditPhotoDialog(
                            it.basicInfo.frontIdDoc.url,
                            it.basicInfo.frontIdDoc.code
                        )
                    } else {
                        //tvAttachFrontIDDoc.makeGone()
                        showPickerOptionsDialog("Camera")
                    }
                }
                tvAttachPassport.setOnClickListener { v ->
                    if (tvAttachPassport.text.contains("View Passport Photo")) {
                        showEditPhotoDialog(
                            it.basicInfo.passportDoc.url,
                            it.basicInfo.passportDoc.code
                        )
                    } else {
                        //tvAttachPassport.makeGone()
                        showPickerOptionsDialog("PassportSize")
                    }
                }
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
            btnContinue.setOnClickListener {
                if (isNetworkAvailable(requireContext())) {
                    val updateBasicInfoDTO = UpdateBasicInfoDTO()
                    if (rbMyself.isChecked) {
                        if (validateYesFields()) {
                            updateBasicInfoDTO.customerId = customerId
                            updateBasicInfoDTO.id_number = nationalid
                            updateBasicInfoDTO.first_name = etFname.text.toString().trim()
                            updateBasicInfoDTO.last_name = etSurname.text.toString().trim()
                            updateBasicInfoDTO.phone = customerphone
                            updateBasicInfoDTO.email = etEmail.text.toString().trim()
                            updateBasicInfoDTO.dob = etDob.text.toString().trim()
                            updateBasicInfoDTO.gender_id = genderId.toString()
                            updateBasicInfoDTO.spouse_name = etSpName.text.toString().trim()
                            updateBasicInfoDTO.spouse_phone = etSpousePhone.text.toString().trim()
                            btnContinue.isEnabled = false
                            progressbar.mainPBar.makeVisible()
                            viewmodel.updateCustomerBasicDetails(updateBasicInfoDTO)
                        }
                    } else {
                        if (validateNoFields()) {
                            updateBasicInfoDTO.customerId = customerId
                            updateBasicInfoDTO.id_number = nationalid
                            updateBasicInfoDTO.first_name = etFname.text.toString().trim()
                            updateBasicInfoDTO.last_name = etSurname.text.toString().trim()
                            updateBasicInfoDTO.phone = customerphone
                            updateBasicInfoDTO.email = etEmail.text.toString().trim()
                            updateBasicInfoDTO.dob = etDob.text.toString().trim()
                            updateBasicInfoDTO.gender_id = genderId.toString()
                            btnContinue.isEnabled = false
                            progressbar.mainPBar.makeVisible()
                            viewmodel.updateCustomerBasicDetails(updateBasicInfoDTO)
                        }
                    }
                } else {
                    onNoNetworkDialog(requireContext())
                }
            }
            viewmodel.status.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            btnContinue.isEnabled = true
                            progressbar.mainPBar.makeGone()
                            Constants.lookupId = etIdNo.text.toString()
                            if (customerDocsList.isNotEmpty()) {
                                initiateDocumentsUpload()
                            }
                            viewmodel.stopObserving()
                        }
                        0 -> {
                            onInfoDialog(viewmodel.statusMessage.value)
                            btnContinue.isEnabled = true
                            progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                        else -> {
                            onInfoDialog(getString(R.string.error_occurred))
                            btnContinue.isEnabled = true
                            progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                    }
                }
            }

            viewmodel.responseGStatus.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        GeneralResponseStatus.LOADING -> {
                            binding.progressbar.mainPBar.makeVisible()
                            binding.progressbar.tvWait.text = "Uploading documents..."
                        }
                        GeneralResponseStatus.DONE -> {
                            binding.progressbar.mainPBar.makeGone()
                        }
                        GeneralResponseStatus.ERROR -> {
                            binding.progressbar.mainPBar.makeGone()
                        }
                    }
                }
            }
            viewmodel.statusDocCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            //Log.e("TAG", "initializeUI: ${Gson().toJson(customerDetailEntity)}")
                            onInfoSuccessDialog("Customer basic information updated successfully")
                            viewmodel.stopObserving()
                        }
                        0 -> {
                            viewmodel.stopObserving()
                            onInfoDialog(viewmodel.statusMessage.value)
                        }
                        else -> {
                            findNavController().navigate(R.id.dashboardFragment)
                            toastyErrors("Error Occurred while uploading the documents...\nkindly try again later...")
                            viewmodel.stopObserving()

                        }
                    }
                }
            }

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
                        /*val channelGeneratedCode =
                            RequestBody.create(
                                MultipartBody.FORM,
                                customerDoc.docGeneratedUID
                            )*/
                        Log.d("TAG", "initiateDocumentsUpload:${customerDoc.docCode} ")
                        val success = viewmodel.uploadCustomerDocs(
                            customerId, docTypeCode,
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
                    onInfoSuccessDialog("Customer basic information updated successfully")
                    viewmodel.stopObserving()
                }
            }
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
        myCalendar.add(Calendar.YEAR, -18)
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
            genderId = selected.id.toString()
        }

    }

    private fun initializeUI() {
        binding.ivBack.setOnClickListener { v ->
            Navigation.findNavController(v)
                .navigateUp()
        }
    }

    private fun validateYesFields(): Boolean {
        var isValid = false
        binding.apply {
            val fName = etFname.text.toString().trim()
            val lName = etSurname.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val id = etIdNo.text.toString().trim()
            val datob = etDob.text.toString().trim()
            val sName = etSpName.text.toString().trim()
            val validMsg = FieldValidators.VALIDINPUT
            val phoneNumber =
                FieldValidators().formatPhoneNumber(binding.etSpousePhone.text.toString())
            val validPhone = FieldValidators().validPhoneNUmber(phoneNumber)
            when {
                fName.isEmpty() -> {
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
                spinnerGender.text.isEmpty() -> {
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
                    tlSpousePhone.error = validPhone
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
                    binding.btnContinue.isEnabled = false
                    nationalid = id
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
            val id = etIdNo.text.toString().trim()
            val datob = etDob.text.toString().trim()
            when {
                fName.isEmpty() -> {
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
                spinnerGender.text.isEmpty() -> {
                    tlDob.error = ""
                    toastyErrors(getString(R.string.select_gender))
                    isValid = false
                }
                else -> {
                    isValid = true
                    tlFname.error = ""
                    tlSname.error = ""
                    tlIdNo.error = ""
                    tlEmail.error = ""
                    tlDob.error = ""
                    binding.btnContinue.isEnabled = false
                    nationalid = id
                }
            }
        }
        return isValid

    }

    private fun showEditPhotoDialog(url: String, docCode: String) {
        Log.d("TAG", "showEditPhotoDialog: $url")
        cardBinding = EditPhotsDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        cardBinding.apply {
            if (docCode == Constants.profilePicCode) {
                Glide.with(requireActivity()).load(url)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                    .into(userLogo)
                tvTitle.text = "Passport Size Photo"
                //tvEdit.makeGone()
                tvEdit.setOnClickListener {
                    dialog.dismiss()
                    showPickerOptionsDialog("PassportSize")
                    editPassport = true
                }
            } else {
                Glide.with(requireActivity()).load(url)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                    .into(userLogo)
                tvTitle.text = "Customer Front ID"
                //tvEdit.makeGone()
                tvEdit.setOnClickListener {
                    dialog.dismiss()
                    showPickerOptionsDialog("Camera")
                    editFrontID = true
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
                val path: String = if (from == "Camera") {
                    getCameraPath(uri, requireActivity())
                } else {
                    FileUtil.getPath(uri, requireContext())
                }
                if (type == "PassportSize") {
                    frontIDUri = uri
                    binding.tvAttachPassport.text =
                        "Passport Size Photo - ${getFileName(uri, requireActivity())}"
                    val passportList =
                        customerDocsList.filter { it.docCode == Constants.profilePicCode }
                    if (passportList.isEmpty()) {
                        //create a new doc and add it to list
                        val generatedUUID = UUID.randomUUID().toString()
                        val customerDoc = CustomerDoc(
                            customerId, Constants.profilePicCode,
                             path, null
                        )
                        customerDocsList.add(customerDoc)
                    } else {
                        //replace the doc
                        val passportDoc = passportList[0]
                        passportDoc.docPath = path
                        customerDocsList.mapInPlace {
                            /**IF DOCCODE FROM THE LIST is equal to the doccode for front id, and path at that items has changedd
                             *  we replace the element /value
                             * with the new  vale, else if the element has not been modified, we retain it*/
                                element ->
                            if (element.docCode == Constants.profilePicCode) passportDoc else element
                        }
                    }
                } else {
                    passportPhotoUri = uri
                    binding.tvAttachFrontIDDoc.text =
                        "Customer Front ID - ${getFileName(uri, requireActivity())}"
                    val frontIDList =
                        customerDocsList.filter { it.docCode == Constants.frontIDCode }
                    if (frontIDList.isEmpty()) {
                        //create a new doc and add it to list
                        val generatedUUID = UUID.randomUUID().toString()
                        val customerDoc = CustomerDoc(
                            customerId, Constants.frontIDCode,
                             path, null
                        )
                        customerDocsList.add(customerDoc)
                    } else {
                        //replace the doc
                        val frontIDDoc = frontIDList[0]
                        frontIDDoc.docPath = path
                        customerDocsList.mapInPlace {
                            /**IF DOCCODE FROM THE LIST is equal to the doccode for front id, and path at that items has changedd
                             *  we replace the element /value
                             * with the new  vale, else if the element has not been modified, we retain it*/
                                element ->
                            if (element.docCode == Constants.frontIDCode) frontIDDoc else element
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

    private fun selectFromCamera(type: String) {
        imagePicker.takeFromCamera { imageResult ->
            imageCallBack(imageResult, type, "Camera")
        }
    }

    private fun selectFromGallery(type: String) {
        imagePicker.pickFromStorage { imageResult ->
            imageCallBack(
                imageResult, type, "Gallery"
            )
        }
    }

}