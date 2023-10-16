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
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.EditPhotsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentResidentialDetailsBinding
import com.deefrent.rnd.fieldapp.dtos.UpdateResidentialInfoDTO
import com.deefrent.rnd.fieldapp.models.customer.CustomerDoc
import com.deefrent.rnd.fieldapp.room.entities.AccStatusEntity
import com.deefrent.rnd.fieldapp.utils.*
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
import java.text.SimpleDateFormat
import java.util.*

class UpdateResidentialDetailsFragment : Fragment() {
    private lateinit var binding: FragmentResidentialDetailsBinding
    private lateinit var cardBinding: EditPhotsDialogBinding
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(CustomerInfoViewModel::class.java)
    }
    private var statusId = ""
    private var national_identity = ""
    lateinit var imagePicker: ImagePicker
    private var customerId = ""
    private var customerDocsList = arrayListOf<CustomerDoc>()

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
                binding.tvAttachDoc.text =
                    "Proof of Residence - ${getFileName(uri, requireActivity())}"
                val residenceDocList =
                    customerDocsList.filter { it.docCode == Constants.residenceDocCode }
                if (residenceDocList.isEmpty()) {
                    //create a new doc and add it to list
                    val generatedUUID = UUID.randomUUID().toString()
                    val customerDoc = CustomerDoc(
                        customerId, Constants.residenceDocCode,
                        path, null
                    )
                    customerDocsList.add(customerDoc)
                } else {
                    //replace the doc
                    val residenceDoc = residenceDocList[0]
                    residenceDoc.docPath = path
                    customerDocsList.mapInPlace {
                        /**IF DOCCODE FROM THE LIST is equal to the doccode for front id, and path at that items has changedd
                         *  we replace the element /value
                         * with the new  vale, else if the element has not been modified, we retain it*/
                            element ->
                        if (element.docCode == Constants.residenceDocCode) residenceDoc else element
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
        binding = FragmentResidentialDetailsBinding.inflate(layoutInflater)

        binding.apply {
            if (tvAttachDoc.text.contains(getString(R.string.view_proof_of_residence))) {
                tvAttachDoc.makeVisible()
            } else {
                tvAttachDoc.text = "Attach Proof of Residence"
                //tvAttachDoc.makeGone()
            }
            btnContinue.text = "Update"
            tvPbText.makeGone()
            pb.makeGone()
            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
            dropdownItemsViewModel.getAllAccommodationStatus()
                .observe(viewLifecycleOwner) { accStat ->
                    populateAccommodationStatus(accStat)
                }
            viewmodel.detailsData.observe(viewLifecycleOwner) {
                customerId = it.basicInfo.id
                national_identity = it.basicInfo.idNumber
                etPAddress.setText(it.residenceInfo.physicalAddress)
                etAccommodationStatus.setText(it.residenceInfo.accommodationStatus, false)
                statusId = it.residenceInfo.accommodationStatusId.toString()
                etHomeAddress.setText(it.residenceInfo.livingSince)
                binding.apply {
                    if (it.residenceDoc != null) {
                        if (it.residenceDoc.url.isNotEmpty()) {
                            tvAttachDoc.text = getString(R.string.view_proof_of_residence)
                            val customerDoc = CustomerDoc(
                                it.basicInfo.id, it.residenceDoc.code,
                                it.residenceDoc.url, it.residenceDoc.documentId
                            )
                            customerDocsList.add(customerDoc)
                        } else {
                            tvAttachDoc.text = getString(R.string.attach_proof_of_residence)
                        }
                    }

                    tvAttachDoc.setOnClickListener { v ->
                        if (tvAttachDoc.text.contains(getString(R.string.view_proof_of_residence))) {
                            showEditPhotoDialog(it.residenceDoc.url)
                        } else {
                            showPickerOptionsDialog()
                        }
                    }

                }

            }
            btnContinue.setOnClickListener {
                if (isNetworkAvailable(requireContext())) {
                    val pAddress = etPAddress.text.toString()
                    val livingSince = etHomeAddress.text.toString()
                    when {
                        pAddress.isEmpty() -> {
                            tlPAdress.error = "Required"
                        }
                        etAccommodationStatus.text.isEmpty() -> {
                            toastyErrors("Select accommodation status")
                        }
                        livingSince.isEmpty() -> {
                            tiHomeAddress.error = "Required"
                        }
                        else -> {
                            tlPAdress.error = ""
                            tiHomeAddress.error = ""
                            val updateResidentialInfoDTO = UpdateResidentialInfoDTO()
                            updateResidentialInfoDTO.id_number = national_identity
                            updateResidentialInfoDTO.living_since = livingSince
                            updateResidentialInfoDTO.res_accommodation_status_id = statusId
                            updateResidentialInfoDTO.postal_address = pAddress
                            btnContinue.isEnabled = false
                            progressbar.mainPBar.makeVisible()
                            viewmodel.updateResidentialDetails(updateResidentialInfoDTO)
                        }
                    }
                } else {
                    onNoNetworkDialog(requireContext())
                }
            }
            viewmodel.statusRCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            btnContinue.isEnabled = true
                            progressbar.mainPBar.makeGone()
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
                            onInfoSuccessDialog("Customer residential details updated successfully")
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
            etHomeAddress.setOnClickListener { pickDate() }
        }
        return binding.root
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
                    onInfoSuccessDialog("Customer residential details updated successfully")
                    viewmodel.stopObserving()
                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun populateAccommodationStatus(idType: List<AccStatusEntity>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, idType)
        binding.etAccommodationStatus.setAdapter(typeAdapter)
        binding.etAccommodationStatus.keyListener = null
        binding.etAccommodationStatus.setOnItemClickListener { parent, _, position, _ ->
            val selected: AccStatusEntity = parent.adapter.getItem(position) as AccStatusEntity
            binding.etAccommodationStatus.setText(selected.name)
            statusId = selected.id.toString()
        }
    }

    private fun pickDate() {
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
                val preferredFormat = "dd-MM-yyyy"
                val date =
                    SimpleDateFormat(preferredFormat, Locale.US).format(myCalendar.time)
                binding.etHomeAddress.setText(date)

            }
        val dialog = DatePickerDialog(
            requireContext(), dateListener, myCalendar[Calendar.YEAR],
            myCalendar[Calendar.MONTH],
            myCalendar[Calendar.DAY_OF_MONTH]
        )
        dialog.datePicker.maxDate = myCalendar.timeInMillis
        dialog.show()
    }

    private fun showEditPhotoDialog(uri: String) {
        cardBinding = EditPhotsDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        cardBinding.apply {
            Glide.with(requireActivity()).load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                .into(userLogo)
            //tvEdit.makeGone()
            //  if (docType== educationDocCode){
            tvTitle.text = getString(R.string.proof_of_residence)
            tvEdit.setOnClickListener {
                dialog.dismiss()
                showPickerOptionsDialog()
            }

            //   }
            cardBinding.userLogo.setOnClickListener {
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
        dialog.setContentView(cardBinding.root)
        dialog.show()

    }


}