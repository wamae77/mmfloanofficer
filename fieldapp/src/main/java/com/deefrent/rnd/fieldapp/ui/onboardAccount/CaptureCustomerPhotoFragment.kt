package com.deefrent.rnd.fieldapp.ui.onboardAccount

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.deefrent.rnd.fieldapp.databinding.FragmentCaptureCustomerPhotoBinding
import com.deefrent.rnd.fieldapp.view.homepage.LoginSessionSharedViewModel
import com.deefrent.rnd.fieldapp.viewModels.RoomDBViewModel
import dev.ronnie.github.imagepicker.ImagePicker
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.File

class CaptureCustomerPhotoFragment : Fragment() {
    private lateinit var roomDBViewModel: RoomDBViewModel
    private var _binding: FragmentCaptureCustomerPhotoBinding? = null
    private val binding get() = _binding!!
    lateinit var imagePicker: ImagePicker
    private var userType = ""
    private var customerPhoto = ""
    private val sharedViewModel: OnboardAccountSharedViewModel by activityViewModels()
    private var customerPassportPhotoPath = ""
    private val loginSessionSharedViewModel: LoginSessionSharedViewModel by activityViewModels()
    private var IsFromIncompleteDialog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userType = arguments?.getString("userType").toString()
        Log.d("userType", "captureCustomerPhoto: $userType")
        arguments?.let {
        }
        imagePicker = ImagePicker(fragment = this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        roomDBViewModel = ViewModelProvider(this).get(RoomDBViewModel::class.java)
        _binding = FragmentCaptureCustomerPhotoBinding.inflate(inflater, container, false)
        val view = binding.root
        observeLoginSharedViewModel()
        // Initialize a list of required permissions to request on runtime
        val list = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )

    /*    binding.btnCameraPhoto.setOnClickListener {
            if (checkStoragePermission()) {
                imagePicker.takeFromCamera(object : ImageResult {
                    override fun onFailure(reason: String) {
                        Toast.makeText(requireContext(), reason, Toast.LENGTH_LONG).show()
                    }

                    override fun onSuccess(uri: Uri) {
                        binding.ivCustomerPhoto.setImageURI(uri)
                        customerPhoto = encodeToBase64(uri)
                        sharedViewModel.setPassportPhoto(customerPhoto)
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
                        customerPassportPhotoPath = path
                        sharedViewModel.setPassportPhotoPath(path)
                        sharedViewModel.setPassportPhotoCaptureFile(convertPathToFile(path))
                        sharedViewModel.setPassportPhotoCaptureUri(uri)
                    }
                })
            }
        }

        binding.btnGalleryPhoto.setOnClickListener {
            if (checkStoragePermission()) {
                //showChooserDialog()
                imagePicker.pickFromStorage(object : ImageResult {
                    override fun onFailure(reason: String) {
                        Toast.makeText(requireContext(), reason, Toast.LENGTH_LONG).show()
                    }

                    override fun onSuccess(uri: Uri) {
                        binding.ivCustomerPhoto.setImageURI(uri)
                        customerPhoto = encodeToBase64(uri)
                        sharedViewModel.setPassportPhoto(customerPhoto)
                        val path = FileUtil.getPath(uri, requireContext())
                        customerPassportPhotoPath = path
                        sharedViewModel.setPassportPhotoPath(path)
                        val file = path?.let { it1 -> convertPathToFile(it1) }
                        if (file != null) {
                            sharedViewModel.setPassportPhotoCaptureFile(file)
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
                            passportPhotoPath.observe(viewLifecycleOwner,
                                { passportPhotoPath ->
                                    customerPassportPhotoPath = passportPhotoPath
                                    val passportPhotoFile = File(customerPassportPhotoPath)
                                    if (passportPhotoFile.exists()) {
                                        binding.ivCustomerPhoto.setImageURI(Uri.fromFile(passportPhotoFile))
                                    }
                                })
                        }
                    }
                })
        }
    }

    private fun saveDataLocally() {
        var RoomDBId = 0
        var PassportPhotoPath = ""
        var lastStep1 = ""
        val lastStep2 = this::class.java.simpleName
        sharedViewModel.apply {
            roomDBId.observe(viewLifecycleOwner,
                { roomDBId ->
                    RoomDBId = roomDBId
                })
            passportPhotoPath.observe(viewLifecycleOwner,
                { passportPhotoPath ->
                    PassportPhotoPath = passportPhotoPath
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
        compositeDisposable.add(roomDBViewModel.updateCustomerPassportPhoto(
            PassportPhotoPath, lastStep1, RoomDBId
        )
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                compositeDisposable.dispose()
            })
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
        if (customerPassportPhotoPath.isNullOrEmpty()) {
            isValid = false
            Toasty.error(requireContext(), "Please select image to upload", Toasty.LENGTH_LONG)
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
        private const val REQUEST_CODE_STORAGE_PERMISSION = 1
        private const val REQUEST_CODE_SELECT_IMAGE = 2
        private const val REQUEST_CODE_CAMERA = 3

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CaptureCustomerPhotoFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

}

// Extension function to show toast message
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}