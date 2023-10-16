package com.deefrent.rnd.fieldapp.ui.onboardAccount

import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import dev.ronnie.github.imagepicker.ImagePicker
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.FileOutputStream
import com.deefrent.rnd.fieldapp.databinding.FragmentCaptureIdPhotoBinding
import com.deefrent.rnd.fieldapp.view.homepage.LoginSessionSharedViewModel
import com.deefrent.rnd.fieldapp.viewModels.RoomDBViewModel
import java.io.File
import java.io.ByteArrayOutputStream
import java.io.IOException

class CaptureIDPhotoFragment : Fragment() {
    private lateinit var roomDBViewModel: RoomDBViewModel
    private var _binding: FragmentCaptureIdPhotoBinding? = null
    private val binding get() = _binding!!
    lateinit var imagePicker: ImagePicker
    private var userType = ""
    private var frontIDCapture = ""
    private var backIDCapture = ""
    private var frontIDPath = ""
    private var backIDPath = ""
    private val sharedViewModel: OnboardAccountSharedViewModel by activityViewModels()
    private val loginSessionSharedViewModel: LoginSessionSharedViewModel by activityViewModels()
    private var IsFromIncompleteDialog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userType = arguments?.getString("userType").toString()
        Log.d("userType", "onCreate: $userType")
        arguments?.let {
        }
        imagePicker = ImagePicker(fragment = this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        roomDBViewModel = ViewModelProvider(this).get(RoomDBViewModel::class.java)
        _binding = FragmentCaptureIdPhotoBinding.inflate(inflater, container, false)
        val view = binding.root
        observeLoginSharedViewModel()
        checkStoragePermission()
        binding.btnContinue.setOnClickListener { v ->
            if (isValid()) {
                saveDataLocally()
                val bundle = bundleOf("userType" to userType)

            }
        }
      /*  binding.btnCameraFrontID.setOnClickListener {
            if (checkStoragePermission()) {
                imagePicker.takeFromCamera(object : ImageResult {
                    override fun onFailure(reason: String) {
                        Toast.makeText(requireContext(), reason, Toast.LENGTH_LONG).show()
                    }

                    override fun onSuccess(uri: Uri) {
                        binding.ivFrontID.setImageURI(uri)
                        frontIDCapture = encodeToBase64(uri)
                        sharedViewModel.setFrontIdCapture(frontIDCapture)
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
                        //new code
                        val bytes = ByteArrayOutputStream()
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
                        frontIDPath = path
                        sharedViewModel.setFrontIdPath(path)
                        sharedViewModel.setFrontIdCaptureFile(convertPathToFile(path))
                        sharedViewModel.setFrontIdCaptureUri(uri)
                    }
                })
            }
        }
        binding.btnGalleryFrontID.setOnClickListener {
            if (checkStoragePermission()) {
                imagePicker.pickFromStorage(object : ImageResult {
                    override fun onFailure(reason: String) {
                        Toast.makeText(requireContext(), reason, Toast.LENGTH_LONG).show()
                    }

                    override fun onSuccess(uri: Uri) {
                        binding.ivFrontID.setImageURI(uri)
                        frontIDCapture = encodeToBase64(uri)
                        sharedViewModel.setFrontIdCapture(frontIDCapture)
                        val path = FileUtil.getPath(uri, requireContext())
                        frontIDPath = path
                        sharedViewModel.setFrontIdPath(path)
                        val file = path?.let { it1 -> convertPathToFile(it1) }
                        if (file != null) {
                            sharedViewModel.setFrontIdCaptureFile(file)
                            sharedViewModel.setFrontIdCaptureUri(uri)
                        }
                    }
                })
            }
        }
        binding.btnCameraBackID.setOnClickListener {
            if (checkStoragePermission()) {
                imagePicker.takeFromCamera(object : ImageResult {
                    override fun onFailure(reason: String) {
                        Toast.makeText(requireContext(), reason, Toast.LENGTH_LONG).show()
                    }

                    override fun onSuccess(uri: Uri) {
                        binding.ivBackID.setImageURI(uri)
                        backIDCapture = encodeToBase64(uri)
                        sharedViewModel.setBackIdCapture(backIDCapture)
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
                        //new code
                        val bytes = ByteArrayOutputStream()
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
                        backIDPath = path
                        sharedViewModel.setBackIdPath(path)
                        sharedViewModel.setBackIdCaptureFile(convertPathToFile(path))
                        sharedViewModel.setBackIdCaptureUri(uri)
                    }
                })
            }
        }
        binding.btnGalleryBackID.setOnClickListener {
            if (checkStoragePermission()) {
                imagePicker.pickFromStorage(object : ImageResult {
                    override fun onFailure(reason: String) {
                        Toast.makeText(requireContext(), reason, Toast.LENGTH_LONG).show()
                    }

                    override fun onSuccess(uri: Uri) {
                        binding.ivBackID.setImageURI(uri)
                        backIDCapture = encodeToBase64(uri)
                        sharedViewModel.setBackIdCapture(backIDCapture)
                        val path = FileUtil.getPath(uri, requireContext())
                        backIDPath = path
                        sharedViewModel.setBackIdPath(path)
                        val file = path?.let { it1 -> convertPathToFile(it1) }
                        if (file != null) {
                            sharedViewModel.setBackIdCaptureFile(file)
                            sharedViewModel.setBackIdCaptureUri(uri)
                        }
                    }
                })
            }
        }*/
        return view
    }

    private fun observeLoginSharedViewModel() {
        loginSessionSharedViewModel.apply {
            isFromIncompleteDialog.observe(viewLifecycleOwner,
                { isFromIncompleteDialog ->
                    if (isFromIncompleteDialog) {
                        IsFromIncompleteDialog = true
                        sharedViewModel.apply {
                            frontIdPath.observe(viewLifecycleOwner,
                                { frontIdPath ->
                                    frontIDPath = frontIdPath
                                    val frontIDFile = File(frontIdPath)
                                    if (frontIDFile.exists()) {
                                        binding.ivFrontID.setImageURI(Uri.fromFile(frontIDFile))
                                    }
                                })
                            backIdPath.observe(viewLifecycleOwner,
                                { backIdPath ->
                                    backIDPath = backIdPath
                                    val backIDFile = File(backIdPath)
                                    if (backIDFile.exists()) {
                                        binding.ivBackID.setImageURI(Uri.fromFile(backIDFile))
                                    }
                                })
                        }
                    }
                })
        }
    }

    private fun saveDataLocally() {
        var RoomDBId = 0
        var FrontIDPath = ""
        var BackIDPath = ""
        var lastStep1 = ""
        val lastStep2 = this::class.java.simpleName
        sharedViewModel.apply {
            roomDBId.observe(viewLifecycleOwner,
                { roomDBId ->
                    RoomDBId = roomDBId
                })
            frontIdPath.observe(viewLifecycleOwner,
                { frontIdPath ->
                    FrontIDPath = frontIdPath
                })
            backIdPath.observe(viewLifecycleOwner,
                { backIdPath ->
                    BackIDPath = backIdPath
                })
            lastStep.observe(viewLifecycleOwner,
                { lastStep ->
                    lastStep1 = if(IsFromIncompleteDialog){
                        lastStep
                    }else{
                        lastStep2
                    }

                })
        }
        sharedViewModel.setLastStep(this::class.java.simpleName)
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(roomDBViewModel.updateCustomerIDDetails(
            FrontIDPath, BackIDPath, lastStep1, RoomDBId
        )
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                compositeDisposable.dispose()
            })
    }

    fun saveImgToInternalStorage(bitmapImg: Bitmap, imageName: String?, directoryName: String) {
        val contextWrapper =
            ContextWrapper(requireContext())
        val directory: File = contextWrapper.getDir(directoryName, Context.MODE_PRIVATE)
        val path = File(directory, imageName!!)
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(path)
            bitmapImg.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fileOutputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }

    fun encodeToBase64(imageUri: Uri): String {
        val input = requireActivity().contentResolver.openInputStream(imageUri)

        // Encode image to base64 string
        val baos = ByteArrayOutputStream()
        BitmapFactory.decodeStream(input, null, null)
            ?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    private fun isValid(): Boolean {
        val isValid: Boolean
        if (frontIDPath.isNullOrEmpty() || backIDPath.isNullOrEmpty()) {
            isValid = false
            Toasty.error(requireContext(), "Please select images to upload", Toasty.LENGTH_LONG)
                .show()
        } else {
            isValid = true
        }
        return isValid
    }

    private fun checkStoragePermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE_STORAGE_PERMISSION
            )
            false
        } else {
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "CaptureIDPhotoFragment"
        private const val REQUEST_CODE_STORAGE_PERMISSION = 1
        private const val REQUEST_CODE_SELECT_IMAGE = 2

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CaptureIDPhotoFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}