package com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.BottomSheetChoosePhotoLayoutBinding
import com.deefrent.rnd.fieldapp.databinding.FragmentCustomerIDPhotoBinding
import com.deefrent.rnd.fieldapp.view.auth.forgetPin.GeneralResponseStatus
import com.deefrent.rnd.fieldapp.utils.*
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.IOException

class CustomerIDPhotoFragment : Fragment() {
    private lateinit var binding: FragmentCustomerIDPhotoBinding
    private  lateinit var bottomsheetbinding: BottomSheetChoosePhotoLayoutBinding
    private lateinit var viewmodel:OnboardCustomerViewModel
    private var currentCapture=0
    private val frontCapture=1
    private val backCapture=2
    private lateinit var frontDestinationUri: Uri
    private lateinit var backDestinationUri: Uri
    private lateinit var frontFile: File
    private lateinit var backFile: File
    private var frontPhotoPath: String? = null
    private var backPhotoPath: String? = null
    companion object {

        private const val CAMERA_REQUEST_CODE = 2
        private const val GALLERY_REQUEST_CODE = 1
        private const val PERMISSION_REQUEST_CODE = 0
        private const val GALLERY_PERMISSION_REQUEST_CODE = 3

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCustomerIDPhotoBinding.inflate(layoutInflater)
        viewmodel= ViewModelProvider(requireActivity()).get(OnboardCustomerViewModel::class.java)
        capturePhotoListener()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            ivBack.setOnClickListener {
                findNavController() .navigateUp()
            }

            btnContinue.setOnClickListener {
                if (!::frontFile.isInitialized) {
                    toastyErrors("please take front ID photo")
                } else if (!::backFile.isInitialized) {
                    toastyErrors("please take back ID photo")
                } else {
                    viewmodel.frontIdPhoto=frontFile
                    viewmodel.backIdPhoto=backFile
                }

            }
            viewmodel.responseStatus.observe(viewLifecycleOwner) {
                if (null != it) {
                    when (it) {

                        GeneralResponseStatus.LOADING -> {
                            binding.progressbar.mainPBar.makeVisible()
                        }
                        GeneralResponseStatus.DONE -> {
                            binding.progressbar.mainPBar.makeGone()
                        }
                        else -> {
                            binding.progressbar.mainPBar.makeGone()
                        }
                    }
                }
            }
/*
            viewmodel.statusCode.observe(viewLifecycleOwner) {
                if (null != it) {
                    binding.btnContinue.isEnabled = true
                    viewmodel.stopObserving()
                    when (it) {
                        1 -> {
                            viewmodel.stopObserving()
                            binding.btnContinue.isEnabled = true
                            findNavController().navigate(R.id.action_customerID_to_customerAdditionalDetailsFragment)
                        }
                        0 -> {
                            viewmodel.stopObserving()
                            binding.btnContinue.isEnabled = true
                            onInfoDialog( viewmodel.statusMessage.value)
                        }

                        else -> {
                            viewmodel.stopObserving()
                            binding.btnContinue.isEnabled = true
                            onInfoDialog(getString(R.string.error_occurred))
                        }
                    }
                }
            }
*/

        }
        val mContextWrapper = ContextWrapper(requireContext())
        val mDirectory: File = mContextWrapper.getDir("Pictures",
            Context.MODE_PRIVATE)
        val frontPath = File(mDirectory, "Frontphoto.png")
        val backPath = File(mDirectory, "Backphoto.png")
        frontDestinationUri = Uri.parse(frontPath.path)
        backDestinationUri = Uri.parse(backPath.path)
    }
    private fun capturePhotoListener(){
        binding.apply {
            binding.FrontNoPhoto.setOnClickListener {
                currentCapture=frontCapture
                showChoosePicDialog()
            }
            binding.BackNoPhoto.setOnClickListener {
                currentCapture=backCapture
                showChoosePicDialog()
            }
            binding.TvTakeAnotherPhoto.setOnClickListener {
                currentCapture=frontCapture
                showChoosePicDialog()
            }
            binding.TvTakeBackPhoto.setOnClickListener {
                currentCapture=backCapture
                showChoosePicDialog()
            }
        }

    }
    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        //val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
      //  intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            // Error occurred while creating the File
            toastyErrors("Failed to capture photo")
            null
        }
        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.deefrent.rnd.jiboostfieldapp",
                it
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(intent,GALLERY_REQUEST_CODE)
        }
    }

    private fun takePictures(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    toastyErrors("Failed to capture photo")
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.deefrent.rnd.jiboostfieldapp",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
                }
            }
        }
    }
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val directory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${uniqueFileName()}_", /* prefix */
            ".jpg", /* suffix */
            directory /* directory */
        ).apply {
            if (currentCapture==frontCapture){
                Log.d("TAG","ABSOLU $absolutePath")
                frontPhotoPath=absolutePath
            }else{
                backPhotoPath=absolutePath
            }
        }
    }

    private fun cameraCheckPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            takePictures()
        } else {
            /**else if we dont have permission to use the cam we request for it*/
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CODE
            )

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // permission was granted
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                    /**we use camera functionality*/
                    takePictures()
                } else {
                    toastyErrors(
                        "Camera Permission was denied!! \n But don't worry you can allow this permission on the app setting",
                    )
                }
            }
            GALLERY_PERMISSION_REQUEST_CODE -> {
                // permission was granted
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                    /**we use camera functionality*/
                    pickFromGallery()
                } else {
                    toastyErrors(
                        "Permission was denied!! \n But don't worry you can allow this permission on the app setting",
                    )
                }
            }
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            /**we display the data as bitmap and set our image to that data
             * nb// you are not supposed to change the key here ,else the app will crash with
             * kotlin.TypeCastException: null cannot be cast to non-null type android.graphics.Bitmap*/
            when(requestCode){
                CAMERA_REQUEST_CODE ->{
                    if (currentCapture==frontCapture){
                        val file = File(frontPhotoPath!!)
                        val uri = Uri.fromFile(file)
                        UCrop.of(uri, frontDestinationUri)
                            .withAspectRatio(16f, 9f)
                           .start(requireContext(),this)
                    }else{
                        val file = File(backPhotoPath!!)
                        val uri = Uri.fromFile(file)
                        UCrop.of(uri, backDestinationUri)
                            .withAspectRatio(16f, 9f)
                            .start(requireContext(),this)
                    }

                }
                GALLERY_REQUEST_CODE ->{
                    if (currentCapture==frontCapture){
                        val file = File(frontPhotoPath!!)
                        val uri = Uri.fromFile(file)
                        UCrop.of(uri, frontDestinationUri)
                            .withAspectRatio(16f, 9f)
                           .start(requireContext(),this)
                    }else{
                        val file = File(backPhotoPath!!)
                        val uri = Uri.fromFile(file)
                        UCrop.of(uri, backDestinationUri)
                            .withAspectRatio(16f, 9f)
                            .start(requireContext(),this)
                    }

                }
                UCrop.REQUEST_CROP->{
                    val uri: Uri = UCrop.getOutput(data!!)!!
                    val filePath = getRealPathFromURIPath(uri, requireActivity())
                    if (currentCapture==frontCapture){
                        frontFile = File(filePath)
                        val bitmap = if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            MediaStore.Images.Media.getBitmap(
                                requireActivity().contentResolver,
                                uri
                            )

                        } else {
                            val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
                            ImageDecoder.decodeBitmap(source)
                        }
                        binding.FrontNoPhoto.makeGone()
                        binding.withPhotoFront.makeVisible()
                        binding.frontPhoto.setImageBitmap(bitmap)

                    }else{
                        backFile = File(filePath)
                        val bitmap = if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            MediaStore.Images.Media.getBitmap(
                                requireActivity().contentResolver,
                                uri
                            )

                        } else {
                            val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
                            ImageDecoder.decodeBitmap(source)
                        }
                        binding.BackNoPhoto.makeGone()
                        binding.withPhotoBack.makeVisible()
                        binding.backPhoto.setImageBitmap(bitmap)
                    }

                }
            }

        }
    }
    private fun showChoosePicDialog() {
        val dialog = Dialog(requireContext(),R.style.CustomAlertDialog)
        bottomsheetbinding = BottomSheetChoosePhotoLayoutBinding.inflate(layoutInflater)
        dialog.setContentView(bottomsheetbinding.root)
        bottomsheetbinding.ivCam.setOnClickListener {
            dialog.dismiss()
            cameraCheckPermission()
        }
        bottomsheetbinding.ivGallery.setOnClickListener {
            dialog.dismiss()
           checkGalleryPermission()
        }

        dialog.show()
    }
    private fun checkGalleryPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), GALLERY_PERMISSION_REQUEST_CODE)
            } else{
                pickFromGallery()
            }
        }else{
            pickFromGallery()
        }
    }



}