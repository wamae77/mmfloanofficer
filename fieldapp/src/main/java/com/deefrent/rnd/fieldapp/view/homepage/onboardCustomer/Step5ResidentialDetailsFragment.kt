package com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.deefrent.rnd.common.abstractions.BaseDaggerFragment
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.EditPhotsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentResidentialDetailsBinding
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.fromSummary
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.residenceDocCode
import com.deefrent.rnd.fieldapp.viewModels.DropdownItemsViewModel
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import dev.ronnie.github.imagepicker.ImagePicker
import dev.ronnie.github.imagepicker.ImageResult
import es.dmoral.toasty.Toasty
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class Step5ResidentialDetailsFragment : BaseDaggerFragment() {
    private lateinit var binding: FragmentResidentialDetailsBinding
    private lateinit var cardBinding: EditPhotsDialogBinding
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private var guarantor = arrayListOf<Guarantor>()
    private lateinit var collateral: List<Collateral>
    private var otherBorrowing = arrayListOf<OtherBorrowing>()
    private var household = arrayListOf<HouseholdMemberEntity>()
    private var customerDocs = arrayListOf<CustomerDocsEntity>()
    private var nationaid = ""
    private var filterCustomerCertCode: ArrayList<CustomerDocsEntity> = arrayListOf()
    private lateinit var residentialCertificate: CustomerDocsEntity
    private var imageUrl = ""
    private var docType = ""
    private var residentialDocImageName = ""
    private var residentialDocUri: Uri? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewmodel by lazy {
        ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(OnboardCustomerViewModel::class.java)
    }
    private var statusId = ""
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
                residentialDocImageName = generateUniqueDocName(
                    nationaid,
                    residenceDocCode
                )
                residentialDocUri = uri
                if (filterCustomerCertCode.isNotEmpty()) {
                    residentialCertificate.docPath = residentialDocImageName
                    Log.i("TAG", "imageCallBack: ${Gson().toJson(residentialCertificate)}")
                    customerDocs.mapInPlace {
                        /**IF DOCCODE FROM THE LIST is equal to the doccode for front id, and path at that items has changedd
                         *  we replace the element /value
                         * with the new  vale, else if the element has not been modified, we retain it*/
                            element ->
                        if (element.docCode == residenceDocCode) residentialCertificate else element
                    }
                } else {
                    val generatedUUID = UUID.randomUUID().toString()
                    val customerDocsEntity = CustomerDocsEntity(
                        0,
                        nationaid,
                        residenceDocCode,
                        generatedUUID,
                        residentialDocImageName
                    )
                    customerDocs.add(customerDocsEntity)
                }


                binding.tvAttachDoc.text =
                    "Proof of Residence - $residentialDocImageName"

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
            tvAttachDoc.setOnClickListener {
                if (tvAttachDoc.text.contains(getString(R.string.view_proof_of_residence))) {
                    Log.d("TAG", "imageUrl:$imageUrl, $docType")
                    showEditPhotoDialog(imageUrl, docType)
                } else {
                    showPickerOptionsDialog()
                }
            }
        }
        viewmodel.cIdNumber.observe(viewLifecycleOwner) { customerIDNumber ->
            nationaid = customerIDNumber
            getSavedItemsFromRoom(customerIDNumber)
        }
        handleBackButton()
        binding.apply {
            binding.ivBack.setOnClickListener {
                if (fromSummary == 3) {
                    findNavController().navigate(R.id.summaryFragment)
                } else {
                    findNavController().navigate(R.id.action_residentialDetailsFragment_to_collateralsFragment)
                }

            }
            dropdownItemsViewModel.getAllAccommodationStatus()
                .observe(viewLifecycleOwner) { accStat ->
                    populateAccommodationStatus(accStat)
                }
            btnContinue.setOnClickListener {
                val pAddress = etPAddress.text.toString()
                val statusName = etAccommodationStatus.text.toString()
                val livingSince = etHomeAddress.text.toString()
                if (pAddress.isEmpty()) {
                    tlPAdress.error = "Required"
                } else if (statusName.isEmpty()) {
                    toastyErrors("Select accommodation status")
                } else if (livingSince.isEmpty()) {
                    tiHomeAddress.error = "Required"
                } else {
                    tlPAdress.error = ""
                    tiHomeAddress.error = ""
                    viewmodel.customerEntityData.observe(viewLifecycleOwner) { customerDetailsEntity ->
                        customerDetailsEntity.apply {
                            lastStep = "ResidentialDetailsFragment"
                            isComplete = false
                            hasFinished = false
                            isProcessed = false
                            resPhysicalAddress = etPAddress.text.toString()
                            resAccommodationStatusId = statusId
                            resAccommodationStatus = etAccommodationStatus.text.toString()
                            resLivingSince = etHomeAddress.text.toString()
                            viewmodel.customerEntityData.postValue(customerDetailsEntity)
                            saveCustomerFullDatLocally(customerDetailsEntity)
                            if (residentialDocUri != null) {
                                saveImageToInternalAppStorage(
                                    residentialDocUri!!,
                                    requireContext(),
                                    residentialDocImageName
                                )
                            }
                        }
                    }
                    if (fromSummary == 3) {
                        findNavController().navigate(R.id.summaryFragment)
                    } else {
                        fromSummary = -1
                        findNavController().navigate(R.id.action_residentialDetails_to_guarantorsFragment)
                    }
                }
            }
            etHomeAddress.setOnClickListener { pickDate() }
        }
        return binding.root
    }

    private fun populateAccommodationStatus(idType: List<AccStatusEntity>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, idType)
        binding.etAccommodationStatus.setAdapter(typeAdapter)
        binding.etAccommodationStatus.keyListener = null
        binding.etAccommodationStatus.setOnItemClickListener { parent, _, position, _ ->
            val selected: AccStatusEntity = parent.adapter.getItem(position) as AccStatusEntity
            binding.etAccommodationStatus.setText(selected.name, false)
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
        val datePickerDialog = DatePickerDialog(
            requireContext(), dateListener, myCalendar[Calendar.YEAR],
            myCalendar[Calendar.MONTH],
            myCalendar[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.datePicker.maxDate = myCalendar.timeInMillis
        datePickerDialog.show()
    }

    private fun saveCustomerFullDatLocally(customerDetailsEntity: CustomerDetailsEntity) {
        viewmodel.insertCustomerFullDetails(
            customerDetailsEntity,
            guarantor,
            collateral,
            otherBorrowing,
            household,
            customerDocs
        )
    }

    private fun getSavedItemsFromRoom(parentNationalId: String) {
        viewmodel.fetchCustomerDetails(parentNationalId).observe(viewLifecycleOwner) {
            Log.d("TAG", "getSavedItemsFromRoom: $it")
            binding.apply {
                if (it.customerDetails.resPhysicalAddress.isNotEmpty()) {
                    etPAddress.setText(it.customerDetails.resPhysicalAddress)
                }
                if (it.customerDetails.resAccommodationStatus.isNotEmpty()) {
                    etAccommodationStatus.setText(it.customerDetails.resAccommodationStatus, false)
                }
                if (it.customerDetails.resLivingSince.isNotEmpty()) {
                    etHomeAddress.setText(it.customerDetails.resLivingSince)
                }

                if (it.customerDetails.resAccommodationStatusId.isNotEmpty()) {
                    statusId = (it.customerDetails.resAccommodationStatusId)
                }
                guarantor.clear()
                guarantor.addAll(it.guarantors)
                otherBorrowing.clear()
                otherBorrowing.addAll(it.otherBorrowing)
                household.clear()
                household.addAll(it.householdMember)
                customerDocs.clear()
                customerDocs.addAll(it.customerDocs)
                collateral = (it.collateral)
                Log.e("TAG", "customerDocsEntity Phase 1: ${Gson().toJson(it.customerDocs)}")
                filterCustomerCertCode.clear()
                val residentialList =
                    it.customerDocs.filter { customerDocsEntity -> customerDocsEntity.docCode == residenceDocCode }
                filterCustomerCertCode.addAll(residentialList)
                Log.e("TAG", "customerDocsEntity Phase 2: ${Gson().toJson(filterCustomerCertCode)}")
                if (it.customerDocs.isNotEmpty()) {
                    if (filterCustomerCertCode.isNotEmpty()) {
                        residentialCertificate = filterCustomerCertCode.first()
                        if (residentialCertificate.docPath.isEmpty()) {
                            val uri = Uri.fromFile(File(residentialCertificate.docPath))
                            val imageName = getFileName(uri, requireActivity())
                            if (Constants.pattern.containsMatchIn(residentialCertificate.docPath)) {
                                binding.tvAttachDoc.text =
                                    getString(R.string.view_proof_of_residence)
                                imageUrl = residentialCertificate.docPath
                                docType = residentialCertificate.docCode
                            } else {
                                Log.e("TAG", "uri: $imageName")
                                binding.tvAttachDoc.text =
                                    "Proof of Residence - $imageName"
                            }
                        } else {
                            binding.tvAttachDoc.text =
                                resources.getString(R.string.attach_proof_of_residence)
                        }
                    } else {
                        binding.tvAttachDoc.text =
                            resources.getString(R.string.attach_proof_of_residence)
                    }
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
            tvTitle.text = getString(R.string.proof_of_residence)
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
                    if (fromSummary == 3) {
                        findNavController().navigate(R.id.summaryFragment)
                    } else {
                        findNavController().navigate(R.id.action_residentialDetailsFragment_to_collateralsFragment)
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }


}