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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.deefrent.rnd.fieldapp.databinding.FragmentAgentIdPhotoBinding
import com.deefrent.rnd.fieldapp.view.homepage.LoginSessionSharedViewModel
import com.deefrent.rnd.fieldapp.viewModels.RoomDBViewModel
import dev.ronnie.github.imagepicker.ImagePicker
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AgentIDPhotoFragment : Fragment() {
    private lateinit var roomDBViewModel: RoomDBViewModel
    private val onboardMerchantSharedViewModel: OnboardMerchantSharedViewModel by activityViewModels()
    private var _binding: FragmentAgentIdPhotoBinding? = null
    private val binding get() = _binding!!
    lateinit var imagePicker: ImagePicker
    private var userType = ""
    private var frontIDCapture = ""
    private var backIDCapture = ""
    private var frontIDPath = ""
    private var backIDPath = ""
    private val loginSessionSharedViewModel: LoginSessionSharedViewModel by activityViewModels()
    private var IsFromIncompleteDialog = false

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
        roomDBViewModel = ViewModelProvider(this).get(RoomDBViewModel::class.java)
        _binding = FragmentAgentIdPhotoBinding.inflate(inflater, container, false)
        val view = binding.root
        observeLoginSharedViewModel()
        checkStoragePermission()
       /* binding.btnCameraFrontID.setOnClickListener {
            if (checkStoragePermission()) {
                imagePicker.takeFromCamera(object : ImageResult {
                    override fun onFailure(reason: String) {
                        Toast.makeText(requireContext(), reason, Toast.LENGTH_LONG).show()
                    }

                    override fun onSuccess(uri: Uri) {
                        Log.d(TAG, "onSuccess: $uri")
                        binding.ivFrontID.setImageURI(uri)
                        frontIDCapture = encodeToBase64(uri)
                        onboardMerchantSharedViewModel.setFrontIdCapture(frontIDCapture)
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
                        onboardMerchantSharedViewModel.setFrontIdPath(path)
                        onboardMerchantSharedViewModel.setFrontIdCaptureFile(convertPathToFile(path))
                        onboardMerchantSharedViewModel.setFrontIdCaptureUri(uri)
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
                        onboardMerchantSharedViewModel.setFrontIdCapture(frontIDCapture)
                        val path = FileUtil.getPath(uri, requireContext())
                        frontIDPath = path
                        onboardMerchantSharedViewModel.setFrontIdPath(path)
                        val file = path?.let { it1 -> convertPathToFile(it1) }
                        if (file != null) {
                            onboardMerchantSharedViewModel.setFrontIdCaptureFile(file)
                            onboardMerchantSharedViewModel.setFrontIdCaptureUri(uri)
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
                        onboardMerchantSharedViewModel.setBackIdCapture(backIDCapture)
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
                        onboardMerchantSharedViewModel.setBackIdPath(path)
                        onboardMerchantSharedViewModel.setBackIdCaptureFile(convertPathToFile(path))
                        onboardMerchantSharedViewModel.setBackIdCaptureUri(uri)
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
                        onboardMerchantSharedViewModel.setBackIdCapture(backIDCapture)
                        val path = FileUtil.getPath(uri, requireContext())
                        backIDPath = path
                        onboardMerchantSharedViewModel.setBackIdPath(path)
                        val file = path?.let { it1 -> convertPathToFile(it1) }
                        if (file != null) {
                            onboardMerchantSharedViewModel.setBackIdCaptureFile(file)
                            onboardMerchantSharedViewModel.setBackIdCaptureUri(uri)
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
                        onboardMerchantSharedViewModel.apply {
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
        onboardMerchantSharedViewModel.apply {
            roomDBId.observe(viewLifecycleOwner
            ) { roomDBId ->
                RoomDBId = roomDBId
            }
            frontIdPath.observe(viewLifecycleOwner
            ) { frontIdPath ->
                FrontIDPath = frontIdPath
            }
            backIdPath.observe(viewLifecycleOwner
            ) { backIdPath ->
                BackIDPath = backIdPath
            }
            lastStep.observe(viewLifecycleOwner
            ) { lastStep ->
                lastStep1 = if (IsFromIncompleteDialog) {
                    lastStep
                } else {
                    lastStep2
                }

            }
        }
        val compositeDisposable = CompositeDisposable()
        onboardMerchantSharedViewModel.setLastStep(this::class.java.simpleName)
        compositeDisposable.add(roomDBViewModel.updateMerchantIDDetails(
            FrontIDPath, BackIDPath, lastStep1, RoomDBId
        )
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                compositeDisposable.dispose()
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    companion object {
        private const val TAG = "MerchantIDPhotoFragment"
        private const val REQUEST_CODE_STORAGE_PERMISSION = 1

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AgentIDPhotoFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}