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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.EditPhotsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentBusinesDetailsBinding
import com.deefrent.rnd.fieldapp.dtos.UpdateBusinessDetailsDTO
import com.deefrent.rnd.fieldapp.models.customer.CustomerDoc
import com.deefrent.rnd.fieldapp.network.models.*
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
import java.util.*

class UpdateBusinesDetailsFragment : Fragment() {
    private lateinit var binding: FragmentBusinesDetailsBinding
    private lateinit var cardBinding: EditPhotsDialogBinding
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(CustomerInfoViewModel::class.java)
    }
    private var bussinessId = ""
    private var eSectorId = ""
    private var national_identity = ""
    private var establishTypeId = ""
    private var customerId = ""
    private var customerDocsList = arrayListOf<CustomerDoc>()
    lateinit var imagePicker: ImagePicker
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
                    "Certificate of Incorporation - ${getFileName(uri, requireActivity())}"
                val businessDocList =
                    customerDocsList.filter { it.docCode == Constants.businessDocCode }
                if (businessDocList.isEmpty()) {
                    //create a new doc and add it to list
                    val generatedUUID = UUID.randomUUID().toString()
                    val customerDoc = CustomerDoc(
                        customerId, Constants.businessDocCode,
                        path, null
                    )
                    customerDocsList.add(customerDoc)
                } else {
                    //replace the doc
                    val businessDoc = businessDocList[0]
                    businessDoc.docPath = path
                    customerDocsList.mapInPlace {
                        /**IF DOCCODE FROM THE LIST is equal to the doccode for front id, and path at that items has changedd
                         *  we replace the element /value
                         * with the new  vale, else if the element has not been modified, we retain it*/
                            element ->
                        if (element.docCode == Constants.businessDocCode) businessDoc else element
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
        binding = FragmentBusinesDetailsBinding.inflate(layoutInflater)
        binding.apply {
            btnContinue.text = "Update"
            tvPbText.makeGone()
            pb.makeGone()
            rbMyself.isChecked = true
            rbMyself.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    clOption.makeVisible()
                    rbOthers.isChecked = false
                    btnContinue.makeVisible()
                }
            }
            rbOthers.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    rbMyself.isChecked = false
                    clOption.makeGone()
                    btnContinue.makeGone()
                }

            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            binding.ivBack.setOnClickListener { v ->
                Navigation.findNavController(v)
                    .navigateUp()
            }
            if (tvAttachDoc.text.contains(getString(R.string.view_incor_certificate))) {
                tvAttachDoc.makeVisible()
            } else {
                //tvAttachDoc.makeGone()
                // showPickerOptionsDialog()
            }
            viewmodel.detailsData.observe(viewLifecycleOwner) {
                customerId = it.basicInfo.id
                national_identity = it.basicInfo.idNumber
                bussinessId = it.businessDetails.typeOfBusinessId.toString()
                eSectorId = it.businessDetails.economicSectorId.toString()
                establishTypeId = it.businessDetails.establishmentTypeId.toString()
                etPAddress.setText(it.businessDetails.typeOfBusiness, false)
                etAccommodationStatus.setText(it.businessDetails.economicSector, false)
                etIndustryName.setText(it.businessDetails.nameOfIndustry)
                etEstType.setText(it.businessDetails.establishmentType, false)
                etyears.setText(it.businessDetails.yearsInBusiness.toString())
                if (it.businessDoc != null) {
                    if (it.businessDoc.url.isNotEmpty()) {
                        tvAttachDoc.makeVisible()
                        tvAttachDoc.text = getString(R.string.view_incor_certificate)
                        val customerDoc = CustomerDoc(
                            it.basicInfo.id, it.businessDoc.code,
                            it.businessDoc.url, it.businessDoc.documentId
                        )
                        customerDocsList.add(customerDoc)
                    } else {
                        //tvAttachDoc.makeGone()
                        tvAttachDoc.text = getString(R.string.attach_certificate_of_incorporation)
                    }
                }

                tvAttachDoc.setOnClickListener { v ->
                    if (tvAttachDoc.text.contains(getString(R.string.view_incor_certificate))) {
                        showEditPhotoDialog(it.businessDoc.url)
                    } else {
                        //tvAttachDoc.makeGone()
                        showPickerOptionsDialog()
                    }
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
                if (isNetworkAvailable(requireContext())) {
                    if (rbMyself.isChecked) {
                        val industryName = etIndustryName.text.toString()
                        val yrs = etyears.text.toString()
                        if (etPAddress.text.isEmpty()) {
                            toastyErrors("Select type of business")
                        } else if (etAccommodationStatus.text.isEmpty()) {
                            toastyErrors("Select economic sector")
                        } else if (industryName.isEmpty()) {
                            tlIndustryName.error = "Required"
                        } else if (etEstType.text.isEmpty()) {
                            toastyErrors("Select type of establishment")
                        } else if (yrs.isEmpty()) {
                            tlYears.error = "Required"
                        } else {
                            tlIndustryName.error = ""
                            tiEstType.error = ""
                            tlYears.error = ""
                            btnContinue.isEnabled = false
                            progressbar.mainPBar.makeVisible()
                            val updateBusinessDetailsDTO = UpdateBusinessDetailsDTO()
                            updateBusinessDetailsDTO.business_type_id = bussinessId
                            updateBusinessDetailsDTO.economic_sector_id = eSectorId
                            updateBusinessDetailsDTO.name_of_industry = industryName
                            updateBusinessDetailsDTO.establishment_type_id = establishTypeId
                            updateBusinessDetailsDTO.how_many_years_in_business = yrs
                            updateBusinessDetailsDTO.id_number = national_identity
                            viewmodel.updateBusinessDetails(updateBusinessDetailsDTO)

                        }
                    } else {
                        btnContinue.isEnabled = false
                    }
                } else {
                    onNoNetworkDialog(requireContext())
                }
            }
            viewmodel.statusBDCode.observe(viewLifecycleOwner) {
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
                            onInfoSuccessDialog("Customer Business details updated successfully")
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
                    onInfoSuccessDialog("Customer Business details updated successfully")
                    viewmodel.stopObserving()
                }
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
            tvTitle.text = getString(R.string.certificate_of_incorporation)
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