package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.updatecdetails

import android.app.AlertDialog
import android.content.Context
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
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.EditPhotsDialogBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentCustomerAdditionalDetailsBinding
import com.deefrent.rnd.fieldapp.dtos.UpdateAdditionalInfoDTO
import com.deefrent.rnd.fieldapp.network.models.EducationLevel
import com.deefrent.rnd.fieldapp.room.database.FieldAppDatabase
import com.deefrent.rnd.fieldapp.room.entities.EmploymentEntity
import com.deefrent.rnd.fieldapp.room.entities.IdentifyEntity
import com.deefrent.rnd.fieldapp.room.repos.DropdownItemRepository
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.viewModels.DropdownItemsViewModel
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.ronnie.github.imagepicker.ImagePicker
import dev.ronnie.github.imagepicker.ImageResult
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class UpdateAdditionalDetailsFragment : Fragment() {
    private lateinit var binding:FragmentCustomerAdditionalDetailsBinding
    private lateinit var cardBinding: EditPhotsDialogBinding
    private val dropdownItemsViewModel: DropdownItemsViewModel by activityViewModels()
    private lateinit var repo:DropdownItemRepository
    private val viewmodel by lazy {
        ViewModelProvider(requireActivity()).get(CustomerInfoViewModel::class.java)
    }
    private var eduId=""
    private var national_idenity=""
    private var identifierId=""
    private var empId=""
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
                    "Education Certificate - ${getFileName(uri, requireActivity())}"

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
        binding= FragmentCustomerAdditionalDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnContinue.text = "Update"
            tvPbText.makeGone()
            pb.makeGone()
            if (tvAttachDoc.text.contains(getString(R.string.view_edu_certificate))) {
                tvAttachDoc.makeVisible()
            } else {
                tvAttachDoc.makeGone()
            }
            viewmodel.detailsData.observe(viewLifecycleOwner) {
                national_idenity = it.basicInfo.idNumber
                eduId = it.basicInfo.educationLevelId.toString()
                identifierId = it.basicInfo.identifierId.toString()
                empId = it.basicInfo.empStatusId.toString()
                spinnerIsource.setText(it.basicInfo.educationLevel.trim(), false)
                spinnerBsType.setText(it.basicInfo.identifier.trim(), false)
                etBAddress.setText(it.basicInfo.numberOfChildren.trim())
                etDSale.setText(it.basicInfo.numberOfDependants.toString().trim())
                spinnerStatus.setText(it.basicInfo.empStatus.toString().trim(), false)
                if (it.educationDoc!=null){
                if (it.educationDoc.url.isNotEmpty()) {
                    tvAttachDoc.text = getString(R.string.view_edu_certificate)
                } else {
                    tvAttachDoc.text = getString(R.string.attach_education_certificate)
                }
            }

                tvAttachDoc.setOnClickListener {v->
                    if (tvAttachDoc.text.contains(getString(R.string.view_edu_certificate))) {
                        showEditPhotoDialog(it.educationDoc.url)
                    } else {
                        showPickerOptionsDialog()
                    }
                }
            }

            binding.ivBack.setOnClickListener { v ->
                Navigation.findNavController(v)
                    .navigateUp()
            }
            btnContinue.setOnClickListener {
                if (isNetworkAvailable(requireContext())){
                    val noOfChildren = etBAddress.text.toString()
                    val noOfDependant = etDSale.text.toString()
                    when {
                        spinnerIsource.text.isEmpty() -> {
                            toastyErrors("Education level required")
                        }
                        spinnerBsType.text.isEmpty() -> {
                            toastyErrors("Select how you heard about us")
                        }
                        noOfChildren.isEmpty() -> {
                            tlBAddress.error = getString(R.string.required)
                        }
                        noOfDependant.isEmpty() -> {
                            tlDSale.error = getString(R.string.required)
                        }
                        spinnerStatus.text.isEmpty() -> {
                            toastyErrors("Select employment status")
                        }
                        else -> {
                            val updateAdditionalInfoDTO=UpdateAdditionalInfoDTO()
                            updateAdditionalInfoDTO.education_level_id=eduId
                            updateAdditionalInfoDTO.how_client_knew_mmf_id=identifierId
                            updateAdditionalInfoDTO.number_of_children=noOfChildren
                            updateAdditionalInfoDTO.number_of_dependants=noOfDependant
                            updateAdditionalInfoDTO.employment_status_id=empId
                            updateAdditionalInfoDTO.id_number=national_idenity
                            btnContinue.isEnabled=false
                            progressbar.mainPBar.makeVisible()
                            viewmodel.updateAdditionalDetails(updateAdditionalInfoDTO)

                        }
                    }
                }else{
                    onNoNetworkDialog(requireContext())
                }
            }
            viewmodel.statusACode.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {
                        1 -> {
                            onInfoSuccessDialog("Customer additional information updated successfully")
                            btnContinue.isEnabled=false
                            progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                        0 -> {
                            onInfoDialog(viewmodel.statusMessage.value)
                            btnContinue.isEnabled=true
                            progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                        else -> {
                            onInfoDialog(getString(R.string.error_occurred))
                            btnContinue.isEnabled=true
                            progressbar.mainPBar.makeGone()
                            viewmodel.stopObserving()
                        }
                    }
                }
            }

            dropdownItemsViewModel.getAllEduLevel().observe(viewLifecycleOwner){eduList->
                populateEduLevel(eduList)

            }
            dropdownItemsViewModel.getAllIdentifiers().observe(viewLifecycleOwner){identifierList->
                populateIdentifies(identifierList)
            }
            GlobalScope.launch(Dispatchers.IO) {
                val empList= repo.getEmploymentStatus()
                withContext(Dispatchers.Main) {
                    Log.d("TAG", "onViewCreated:empList${empList} ")
                    populateEmpStatus(empList)
                }
            }
        }
    }
    private fun populateEduLevel(eduList: List<EducationLevel>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, eduList)
        binding.spinnerIsource.setAdapter(typeAdapter)
        binding.spinnerIsource.keyListener = null
        binding.spinnerIsource.setOnItemClickListener { parent, _, position, _ ->
            val selected: EducationLevel = parent.adapter.getItem(position) as EducationLevel
            binding.spinnerIsource.setText(selected.name,false)
            eduId= selected.id.toString()
        }
    }
    private fun populateIdentifies(identifyList: List<IdentifyEntity>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, identifyList)
        binding.spinnerBsType.setAdapter(typeAdapter)
        binding.spinnerBsType.keyListener = null
        binding.spinnerBsType.setOnItemClickListener { parent, _, position, _ ->
            val selected: IdentifyEntity = parent.adapter.getItem(position) as IdentifyEntity
            binding.spinnerBsType.setText(selected.name,false)
            identifierId= selected.id.toString()
        }
    }
    private fun populateEmpStatus(empStatusList: List<EmploymentEntity>) {
        val typeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, empStatusList)
        binding.spinnerStatus.setAdapter(typeAdapter)
        binding.spinnerStatus.keyListener = null
        binding.spinnerStatus.setOnItemClickListener { parent, _, position, _ ->
            val selected: EmploymentEntity = parent.adapter.getItem(position) as EmploymentEntity
            binding.spinnerStatus.setText(selected.name,false)
            empId= selected.id.toString()
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val dropdownItemDao= FieldAppDatabase.getFieldAppDatabase(BaseApp.applicationContext()).dropdownItemDao()
        repo= DropdownItemRepository(dropdownItemDao)
    }

    private fun showEditPhotoDialog(uri:String) {
        cardBinding = EditPhotsDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        cardBinding.apply {
            Glide.with(requireActivity()).load(uri)
                .placeholder(ShimmerPlaceHolder.getShimmerPlaceHolder())
                .into(userLogo)
            //  if (docType== educationDocCode){
            tvTitle.text=getString(R.string.edu_cert)
            tvEdit.makeGone()
            /*tvEdit.setOnClickListener {
                dialog.dismiss()
                showPickerOptionsDialog()
            }*/

            //   }
            cardBinding.userLogo.setOnClickListener {
                val mBuilder: AlertDialog.Builder =
                    AlertDialog.Builder(context, R.style.WrapContentDialog)
                val mView: View =layoutInflater.inflate(R.layout.preview_image, null)
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