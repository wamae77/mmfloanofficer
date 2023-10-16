package com.deefrent.rnd.fieldapp.view.homepage.customerassessment.customerprofile

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentAddDocumentBinding
import com.deefrent.rnd.fieldapp.models.customerDocuments.CustomerDocumentType
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.view.homepage.customerassessment.CustomerAssessmentHomeViewModel
import com.deefrent.rnd.fieldapp.viewModels.DocumentViewModel
import com.github.chrisbanes.photoview.PhotoView
import dev.ronnie.github.imagepicker.ImagePicker
import dev.ronnie.github.imagepicker.ImageResult
import es.dmoral.toasty.Toasty
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.fragment_add_document.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AddDocumentFragment : Fragment() {
    private lateinit var binding: FragmentAddDocumentBinding
    private lateinit var customerId: String
    private lateinit var selectedDocumentCode: String
    lateinit var imagePicker: ImagePicker
    private var documentFile: File? = null
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[CustomerAssessmentHomeViewModel::class.java]
    }
    private val documentViewModel by lazy {
        ViewModelProvider(requireActivity())[DocumentViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        imagePicker = ImagePicker(fragment = this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddDocumentBinding.inflate(layoutInflater)
        viewModel.iDLookUpData.observe(viewLifecycleOwner) {
            val idNumber = it?.idNumber?.replace("(?<=.{2}).(?=.{3})".toRegex(), "*")
            val customerName = "${it?.firstName} ${it?.lastName}"
            binding.tvAccName.text = String.format(
                getString(R.string.acc), "$customerName -" +
                        "\n$idNumber"
            )
            customerId = it.id
        }
        binding.apply {
            ivBack.setOnClickListener { requireActivity().onBackPressed() }
            acDocumentType.keyListener = null
            tiDocumentType.editText?.addTextChangedListener(CustomTextWatcher(binding.tiDocumentType))
            tiDescription.editText?.addTextChangedListener(CustomTextWatcher(binding.tiDescription))
        }
        if (isNetworkAvailable(requireContext())) {
            getDocumentTypes()
        } else {
            onNoNetworkDialog(requireContext())
        }
        binding.tvSelectImage.setOnClickListener {
            showPickerOptionsDialog()
        }
        binding.btnUploadDocument.setOnClickListener {
            validateData()
        }
        return binding.root
    }

    private fun validateData() {
        val documentType = binding.acDocumentType.text.toString()
        val description = binding.etDescription.text.toString()
        if (documentType.isEmpty()) {
            binding.tiDocumentType.error = "Select Document Type"
        } else if (documentFile == null) {
            toastyErrors("Please attach a document")
        } else if (description.isEmpty()) {
            binding.tiDescription.error = "Enter a description"
        } else {
            uploadCustomerDocument()
        }
    }

    private fun uploadCustomerDocument() {
        binding.progressBar.mainPBar.makeVisible()
        binding.progressBar.tvWait.text = "Uploading Document..."
        binding.btnUploadDocument.makeGone()
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            val compressedImage =
                documentFile?.let { Compressor.compress(requireContext(), it) }
            val file = compressedImage?.let {
                RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    it
                )
            }?.let {
                MultipartBody.Part.createFormData(
                    "file",
                    compressedImage.name, it
                )
            }
            val customerID = RequestBody.create(
                MultipartBody.FORM,
                customerId
            )
            Log.d("TAG", "onBoardDocuments: $customerId")
            val docTypeCode =
                RequestBody.create(MultipartBody.FORM, selectedDocumentCode)
            Log.d("TAG", "onBoardDocuments: $selectedDocumentCode")
            file?.let {
                documentViewModel.uploadCustomerDoc(customerID, docTypeCode, it)
                    .observe(viewLifecycleOwner) { uploadCustomerDocumentResponse ->
                        binding.apply {
                            btnUploadDocument.makeVisible()
                            progressBar.mainPBar.makeGone()
                        }
                        if (uploadCustomerDocumentResponse != null) {
                            if (uploadCustomerDocumentResponse.status == 1) {
                                requireActivity().onBackPressed()
                                toastySuccess("Document uploaded successfully")
                            } else {
                                showErrorDialog(uploadCustomerDocumentResponse.message)
                            }
                        } else {
                            showErrorDialog("An unexpected error occurred while uploading document. Please try again")
                        }
                    }
            }
        }
    }

    private fun getDocumentTypes() {
        binding.progressBar.tvWait.text = "Fetching document types..."
        binding.progressBar.mainPBar.makeVisible()
        documentViewModel.getDocumentTypes().observe(viewLifecycleOwner) { documentTypesResponse ->
            binding.progressBar.mainPBar.makeGone()
            if (documentTypesResponse != null) {
                if (documentTypesResponse.data !== null && documentTypesResponse.data.isNotEmpty()) {
                    populateDocumentTypes(documentTypesResponse.data)
                } else {
                    showInfoDialog(documentTypesResponse.message, "No Documents Types")
                }
            } else {
                showErrorDialog("An unexpected error occurred while fetching document types. Please try again")
            }

        }
    }

    private fun populateDocumentTypes(documentTypesList: List<CustomerDocumentType>) {
        binding.acDocumentType.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                documentTypesList
            )
        )
        binding.acDocumentType.keyListener = null
        binding.acDocumentType.setOnItemClickListener { parent, _, position, _ ->
            val selected: CustomerDocumentType =
                parent.adapter.getItem(position) as CustomerDocumentType
            binding.acDocumentType.setText(selected.name, false)
            selectedDocumentCode = selected.code
        }
        binding.imageView.setOnClickListener {
            zoomImage()
        }
    }

    private fun zoomImage() {
        val image = (binding.imageView.drawable as BitmapDrawable).bitmap
        val mBuilder: AlertDialog.Builder =
            AlertDialog.Builder(context, R.style.WrapContentDialog)
        val mView: View =
            layoutInflater.inflate(R.layout.preview_image, null)
        val ivImagePreview = mView.findViewById<PhotoView>(R.id.iv_preview_image)
        ivImagePreview.setImageBitmap(image)
        mBuilder.setView(mView)
        val mDialog: AlertDialog = mBuilder.create()
        mDialog.show()
    }

    private fun showErrorDialog(contentMessage: String) {
        val dialog = SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Request Failed!")
            .setContentText(contentMessage)
            .setConfirmText("OK")
            .showCancelButton(false)
            .setConfirmClickListener { sDialog ->
                sDialog.cancel()
            }
        dialog.setCancelable(true)
        dialog.show()
    }

    private fun showInfoDialog(contentMessage: String, title: String) {
        val dialog = SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText(title)
            .setContentText(contentMessage)
            .setConfirmText("OK")
            .showCancelButton(false)
            .setConfirmClickListener { sDialog ->
                sDialog.cancel()
            }
        dialog.setCancelable(true)
        dialog.show()
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
                //Log.d("TAG", "imageCallBack: $absolutePath")
                Log.d("TAG", "imageCallBack: $uri")
                binding.imageView.setImageURI(uri)
                if (from.equals("Camera", true)) {
                    binding.progressBar.apply {
                        tvWait.text = "Processing Document..."
                        mainPBar.makeVisible()
                    }
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        documentFile = getFileFromCameraUri(uri)
                        withContext(Dispatchers.Main) { binding.progressBar.mainPBar.makeGone() }
                        Log.d("TAG", "imageCallBack: $documentFile")
                    }
                } else {
                    documentFile = getFileFromGalleryUri(uri)!!
                    Log.d("TAG", "imageCallBack: $documentFile")
                }
            }
            is ImageResult.Failure -> {
                val errorString = imageResult.errorString
                Toasty.error(requireContext(), errorString, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getFileFromGalleryUri(uri: Uri): File? {
        val path = FileUtil.getPath(uri, requireContext())
        return path?.let { it1 -> convertPathToFile(it1) }
    }

    private fun getFileFromCameraUri(uri: Uri): File {
        val bytes = ByteArrayOutputStream()
        val bitmap = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getBitmap(
                requireActivity().contentResolver,
                uri
            )
        } else {
            val source =
                ImageDecoder.createSource(requireActivity().contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
        val filename = generateImageName()
        val destination =
            File(
                "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)}/Camera",
                filename
            )
        val fo: FileOutputStream
        try {
            fo = FileOutputStream(destination)
            fo.write(bytes.toByteArray())
            fo.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val path = destination.absolutePath
        return convertPathToFile(path)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddDocumentFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}