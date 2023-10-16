package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.assessment

import android.app.AlertDialog
import android.app.DatePickerDialog
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
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.EditPhotsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentResidentialDetailsBinding
import com.deefrent.rnd.fieldapp.room.entities.*
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.utils.Constants.Companion.residenceDocCode
import com.deefrent.rnd.fieldapp.viewModels.AssessmentDashboardViewModel
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
import kotlin.collections.ArrayList

class AssessResidentialDetailsFragment : Fragment() {
    private lateinit var binding: FragmentResidentialDetailsBinding
    private lateinit var cardBinding: EditPhotsDialogBinding
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private var guarantor = arrayListOf<AssessGuarantor>()
    private lateinit var collateral: List<AssessCollateral>
    private var otherBorrowing = arrayListOf<AssessBorrowing>()
    private var household = arrayListOf<AssessHouseholdMemberEntity>()
    private var customerDocs: ArrayList<AssessCustomerDocsEntity> = arrayListOf()
    private var nationaid = ""
    private var filterCustomerCertCode: ArrayList<AssessCustomerDocsEntity> = arrayListOf()
    private lateinit var residentialCertificate: AssessCustomerDocsEntity
    private var imageUrl = ""
    private var docType = ""
    private var residentialDocImageName = ""
    private var residentialDocUri: Uri? = null

    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(AssessmentDashboardViewModel::class.java)
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
                        if (element.docCode == Constants.residenceDocCode) residentialCertificate else element
                    }
                } else {
                    val generatedUUID = UUID.randomUUID().toString()
                    val customerDocsEntity = AssessCustomerDocsEntity(
                        0,
                        nationaid,
                        Constants.residenceDocCode,
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
            binding.ivBack.setOnClickListener {
                findNavController().navigate(R.id.assessCollateralsFragment)
            }
            dropdownItemsViewModel.getAllAccommodationStatus()
                .observe(viewLifecycleOwner) { accStat ->
                    populateAccommodationStatus(accStat)
                }
            btnContinue.setOnClickListener {
                val pAddress = etPAddress.text.toString()
                val accStatus = etAccommodationStatus.text.toString()
                val liveSince = etHomeAddress.text.toString()
                when {
                    pAddress.isEmpty() -> {
                        tlPAdress.error = "Required"
                    }
                    accStatus.isEmpty() -> {
                        toastyErrors("Select accommodation status")
                    }
                    liveSince.isEmpty() -> {
                        tiHomeAddress.error = "Required"
                    }
                    /*tvAttachDoc.text.toString() == resources.getString(R.string.attach_proof_of_residence) -> {
                        toastyErrors(getString(R.string.attach_proof_of_residence))
                    }*/
                    else -> {
                        tlPAdress.error = ""
                        tiHomeAddress.error = ""
                        viewmodel.assessCustomerEntity.observe(viewLifecycleOwner) { detailsEntity ->
                            detailsEntity.apply {
                                lastStep = "AssessResidentialDetailsFragment"
                                isComplete = false
                                hasFinished = false
                                isProcessed = false
                                resPhysicalAddress = etPAddress.text.toString()
                                resAccomadationStatus = statusId
                                resAccomodation = etAccommodationStatus.text.toString()
                                resLivingSince = etHomeAddress.text.toString()
                                viewmodel.assessCustomerEntity.postValue(detailsEntity)
                                saveAssessmentDataLocally(detailsEntity)
                                if (residentialDocUri != null) {
                                    saveImageToInternalAppStorage(
                                        residentialDocUri!!,
                                        requireContext(),
                                        residentialDocImageName
                                    )
                                }
                            }
                        }
                        findNavController().navigate(R.id.action_assessResidentialFragment_to_assessGuarantorsFragment)

                    }
                }
            }
            etHomeAddress.setOnClickListener { pickDate() }
        }
        handleBackButton()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        viewmodel.parentId.observe(viewLifecycleOwner) { customerIDNumber ->
            nationaid = customerIDNumber
            getSavedItemsFromRoom(customerIDNumber)
        }
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
        DatePickerDialog(
            requireContext(), dateListener, myCalendar[Calendar.YEAR],
            myCalendar[Calendar.MONTH],
            myCalendar[Calendar.DAY_OF_MONTH]
        ).show()
    }

    private fun saveAssessmentDataLocally(assessCustomerEntity: AssessCustomerEntity) {
        viewmodel.insertAssessmentData(
            assessCustomerEntity, customerDocs,
            collateral,
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
                    findNavController().navigate(R.id.assessCollateralsFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun getSavedItemsFromRoom(parentId: String) {
        binding.apply {
            viewmodel.fetchCustomerDetails(parentId).observe(viewLifecycleOwner) {
                Log.e("TAG", "fetchCustomerDetails: ${Gson().toJson(it)}")
                etPAddress.setText(it.assessCustomerEntity.resPhysicalAddress)
                etAccommodationStatus.setText(it.assessCustomerEntity.resAccomodation, false)
                statusId = it.assessCustomerEntity.resAccomadationStatus
                etHomeAddress.setText(it.assessCustomerEntity.resLivingSince)
                guarantor.clear()
                guarantor.addAll(it.assessGua)
                otherBorrowing.clear()
                otherBorrowing.addAll(it.assessBorrow)
                household.clear()
                household.addAll(it.householdMember)
                customerDocs.clear()
                customerDocs.addAll(it.customerDocs)
                collateral = (it.assessCollateral)
                Log.e("TAG", "customerDocsEntity Phase 1: ${Gson().toJson(it.customerDocs)}")
                filterCustomerCertCode.clear()
                val residentialList =
                    it.customerDocs.filter { customerDocsEntity -> customerDocsEntity.docCode == Constants.residenceDocCode }
                filterCustomerCertCode.addAll(residentialList)
                Log.e("TAG", "customerDocsEntity Phase 2: ${Gson().toJson(filterCustomerCertCode)}")
                if (it.customerDocs.isNotEmpty()) {
                    if (filterCustomerCertCode.isNotEmpty()) {
                        residentialCertificate = filterCustomerCertCode.first()
                        if (residentialCertificate.docPath.isNotEmpty()){
                        val uri = Uri.fromFile(File(residentialCertificate.docPath))
                        val imageName = getFileName(uri, requireActivity())
                        if (Constants.pattern.containsMatchIn(residentialCertificate.docPath)) {
                            binding.tvAttachDoc.text = getString(R.string.view_proof_of_residence)
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
                    } } else {
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
}