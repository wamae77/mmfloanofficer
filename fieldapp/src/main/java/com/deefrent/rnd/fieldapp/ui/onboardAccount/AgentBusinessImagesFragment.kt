package com.deefrent.rnd.fieldapp.ui.onboardAccount

import android.Manifest
import android.content.*
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
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.bodies.onboardAccount.CreateMerchantBody
import com.deefrent.rnd.fieldapp.databinding.FragmentAgentBusinessImagesBinding
import com.deefrent.rnd.fieldapp.view.homepage.LoginSessionSharedViewModel
import com.deefrent.rnd.fieldapp.utils.*
import com.deefrent.rnd.fieldapp.viewModels.OnboardAccountViewModel
import com.deefrent.rnd.fieldapp.viewModels.RoomDBViewModel
import com.google.gson.Gson
import dev.ronnie.github.imagepicker.ImagePicker
import es.dmoral.toasty.Toasty
import id.zelory.compressor.Compressor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.properties.Delegates

class AgentBusinessImagesFragment : Fragment() {
    private lateinit var roomDBViewModel: RoomDBViewModel
    private val onboardMerchantSharedViewModel: OnboardMerchantSharedViewModel by activityViewModels()
    private lateinit var onboardAccountViewModel: OnboardAccountViewModel
    private var _binding: FragmentAgentBusinessImagesBinding? = null
    private val binding get() = _binding!!
    lateinit var imagePicker: ImagePicker
    private var locationSharedPreferences: SharedPreferences? = null
    private var termsAndConditionDoc = ""
    private var businessPermitDoc = ""
    private var companyRegistrationDoc = ""
    private val loginSessionSharedViewModel: LoginSessionSharedViewModel by activityViewModels()
    private var IsFromIncompleteDialog = false
    private lateinit var connectionLiveData: ConnectionLiveData
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
        connectionLiveData = ConnectionLiveData(requireContext())
        roomDBViewModel = ViewModelProvider(this).get(RoomDBViewModel::class.java)
        onboardAccountViewModel = ViewModelProvider(this).get(OnboardAccountViewModel::class.java)
        _binding = FragmentAgentBusinessImagesBinding.inflate(inflater, container, false)
        val view = binding.root
        observeLoginSharedViewModel()
        locationSharedPreferences =
            requireContext().getSharedPreferences("location", Context.MODE_PRIVATE)
        updateFragmentTitle()
        binding.btnContinue.setOnClickListener { v ->
            if (isValid()) {
                //callDialog("Submitting...", requireContext(), v)
                observeSharedViewModel(v)
            }
        }
        binding.etTermsDoc.keyListener = null
        /*binding.etTermsDoc.setOnClickListener {
            imagePicker.pickFromStorage(object : ImageResult {
                override fun onFailure(reason: String) {
                    Toast.makeText(requireContext(), reason, Toast.LENGTH_LONG).show()
                }

                override fun onSuccess(uri: Uri) {
                    binding.etTermsDoc.setText(getFileName(uri))
                    termsAndConditionDoc = encodeToBase64(uri)
                    onboardMerchantSharedViewModel.setTermsAndConditionDoc(termsAndConditionDoc)
                    val path = FileUtil.getPath(uri, requireContext())
                    onboardMerchantSharedViewModel.setTermsAndConditionsDocPath(path)
                    val file = path?.let { it1 -> convertPathToFile(it1) }
                    if (file != null) {
                        onboardMerchantSharedViewModel.setTermsAndConditionDocFile(file)
                        onboardMerchantSharedViewModel.setTermsAndConditionDocUri(uri)
                    }
                }
            })
        }
        binding.etAttachKRAPin.keyListener = null
        binding.etAttachKRAPin.setOnClickListener {
            imagePicker.pickFromStorage(object : ImageResult {
                override fun onFailure(reason: String) {
                    Toast.makeText(requireContext(), reason, Toast.LENGTH_LONG).show()
                }

                override fun onSuccess(uri: Uri) {
                    binding.etAttachKRAPin.setText(getFileName(uri))
                    val path = FileUtil.getPath(uri, requireContext())
                    onboardMerchantSharedViewModel.setKRAPinPath(path)
                    val file = path?.let { it1 -> convertPathToFile(it1) }
                    if (file != null) {
                        onboardMerchantSharedViewModel.setKRAPinFile(file)
                        onboardMerchantSharedViewModel.setKRAPinUri(uri)
                    }
                }
            })
        }
        binding.etAttachBusinessLicense.keyListener = null
        binding.etAttachBusinessLicense.setOnClickListener {
            imagePicker.pickFromStorage(object : ImageResult {
                override fun onFailure(reason: String) {
                    Toast.makeText(requireContext(), reason, Toast.LENGTH_LONG).show()
                }

                override fun onSuccess(uri: Uri) {
                    binding.etAttachBusinessLicense.setText(getFileName(uri))
                    val path = FileUtil.getPath(uri, requireContext())
                    onboardMerchantSharedViewModel.setBusinessLicensePath(path)
                    val file = path?.let { it1 -> convertPathToFile(it1) }
                    if (file != null) {
                        onboardMerchantSharedViewModel.setBusinessLicenseFile(file)
                        onboardMerchantSharedViewModel.setBusinessLicenseUri(uri)
                    }
                }
            })
        }*/
        binding.etBusinessPermit.keyListener = null
        /* binding.etBusinessPermit.setOnClickListener {
             imagePicker.pickFromStorage(object : ImageResult {
                 override fun onFailure(reason: String) {
                     Toast.makeText(requireContext(), reason, Toast.LENGTH_LONG).show()
                 }

                 override fun onSuccess(uri: Uri) {
                     binding.etBusinessPermit.setText(getFileName(uri))
                     businessPermitDoc = encodeToBase64(uri)
                     onboardMerchantSharedViewModel.setBusinessPermitDoc(businessPermitDoc)
                     val path = FileUtil.getPath(uri, requireContext())
                     onboardMerchantSharedViewModel.setBusinessPermitPath(path)
                     val file = path?.let { it1 -> convertPathToFile(it1) }
                     if (file != null) {
                         onboardMerchantSharedViewModel.setBusinessPermitDocFile(file)
                         onboardMerchantSharedViewModel.setBusinessPermitDocUri(uri)
                     }
                 }
             })
         }
         binding.etRegistrationCertificate.keyListener = null
         binding.etRegistrationCertificate.setOnClickListener {
             imagePicker.pickFromStorage(object : ImageResult {
                 override fun onFailure(reason: String) {
                     Toast.makeText(requireContext(), reason, Toast.LENGTH_LONG).show()
                 }

                 override fun onSuccess(uri: Uri) {
                     binding.etRegistrationCertificate.setText(getFileName(uri))
                     companyRegistrationDoc = encodeToBase64(uri)
                     onboardMerchantSharedViewModel.setCompanyRegistrationDoc(companyRegistrationDoc)
                     val path = FileUtil.getPath(uri, requireContext())
                     onboardMerchantSharedViewModel.setCompanyRegistrationDocPath(path)
                     val file = path?.let { it1 -> convertPathToFile(it1) }
                     if (file != null) {
                         onboardMerchantSharedViewModel.setCompanyRegistrationDocFile(file)
                         onboardMerchantSharedViewModel.setCompanyRegistrationDocUri(uri)
                     }
                 }
             })
         }*/
        //pick shop image
        /* binding.btnCameraShopPhoto.setOnClickListener {
             if (checkStoragePermission()) {
                 imagePicker.takeFromCamera(object : ImageResult {
                     override fun onFailure(reason: String) {
                         Toast.makeText(requireContext(), reason, Toast.LENGTH_LONG).show()
                     }

                     override fun onSuccess(uri: Uri) {
                         binding.ivShopPhoto.setImageURI(uri)
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
                         onboardMerchantSharedViewModel.setShopPhotoPath(path)
                         onboardMerchantSharedViewModel.setShopPhotoFile(convertPathToFile(path))
                     }
                 })
             }
         }
         binding.btnGalleryShopPhoto.setOnClickListener {
             if (checkStoragePermission()) {
                 imagePicker.pickFromStorage(object : ImageResult {
                     override fun onFailure(reason: String) {
                         Toast.makeText(requireContext(), reason, Toast.LENGTH_LONG).show()
                     }

                     override fun onSuccess(uri: Uri) {
                         binding.ivShopPhoto.setImageURI(uri)
                         val path = FileUtil.getPath(uri, requireContext())
                         onboardMerchantSharedViewModel.setShopPhotoPath(path)
                         val file = path?.let { it1 -> convertPathToFile(it1) }
                         if (file != null) {
                             onboardMerchantSharedViewModel.setShopPhotoFile(file)
                         }
                     }
                 })
             }
         }*/
        return view
    }

    private fun observeLoginSharedViewModel() {
        loginSessionSharedViewModel.apply {
            isFromIncompleteDialog.observe(
                viewLifecycleOwner
            ) { isFromIncompleteDialog ->
                if (isFromIncompleteDialog) {
                    IsFromIncompleteDialog = true
                }
            }
        }
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

    private fun saveDataLocally() {
        var RoomDBId = 0
        var TermsAndConditionPath = ""
        var KRAPinPath = ""
        var BusinessLicensePath = ""
        var BusinessPermitPath = ""
        var CompanyRegistrationCertificatePath = ""
        var Complete = true
        onboardMerchantSharedViewModel.apply {
            roomDBId.observe(viewLifecycleOwner,
                { roomDBId ->
                    RoomDBId = roomDBId
                })
            termsAndConditionDocPath.observe(viewLifecycleOwner,
                { termsAndConditionDocPath ->
                    TermsAndConditionPath = termsAndConditionDocPath
                })
            kraPINPath.observe(viewLifecycleOwner,
                { kraPINPath ->
                    KRAPinPath = kraPINPath
                })
            businessLicensePath.observe(viewLifecycleOwner,
                { businessLicensePath ->
                    BusinessLicensePath = businessLicensePath
                })
            businessPermitDocPath.observe(viewLifecycleOwner,
                { businessPermitDocPath ->
                    BusinessPermitPath = businessPermitDocPath
                })
            companyRegistrationPath.observe(viewLifecycleOwner,
                { companyRegistrationPath ->
                    CompanyRegistrationCertificatePath = companyRegistrationPath
                })
            shopPhotoPath.observe(viewLifecycleOwner,
                { shopPhotoPath ->
                    ShopPhotoPath = shopPhotoPath
                })
            val lastStep = this::class.java.simpleName
            val compositeDisposable = CompositeDisposable()
            compositeDisposable.add(roomDBViewModel.updateMerchantBusinessImages(
                TermsAndConditionPath,
                KRAPinPath,
                BusinessLicensePath,
                BusinessPermitPath, CompanyRegistrationCertificatePath, lastStep,
                Complete, ShopPhotoPath, RoomDBId
            )
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    compositeDisposable.dispose()
                })
        }
    }

    private fun updateFragmentTitle() {
        onboardMerchantSharedViewModel.apply {
            userType.observe(viewLifecycleOwner,
                { userType ->
                    binding.tvFragmentTitle.text = "$userType's Business Images"
                })
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

    private fun getLatitude(): String? {
        return locationSharedPreferences?.getString("latitude", "nothing")
    }

    private fun getLongitude(): String? {
        return locationSharedPreferences?.getString("longitude", "nothing")
    }

    private fun isValid(): Boolean {
        val isValid: Boolean
        if (binding.etTermsDoc.text.toString().isNullOrEmpty() ||
            binding.etAttachKRAPin.text.toString().isNullOrEmpty() ||
            binding.etAttachBusinessLicense.text.toString().isNullOrEmpty() ||
            binding.etBusinessPermit.text.toString().isNullOrEmpty() ||
            binding.etRegistrationCertificate.text.toString().isNullOrEmpty()
        ) {
            isValid = false
            Toasty.error(requireContext(), "Please fill in all the details", Toasty.LENGTH_LONG)
                .show()
        } else {
            isValid = true
        }
        return isValid
    }

/*
    private fun getFileName(uri: Uri): String {
        val uriString = uri.toString() //The uri with the location of the file
        val returnedFile = File(uriString)
        val absolutePath = returnedFile.absolutePath
        var displayName = ""

        if (uriString.startsWith("content://")) {
            var cursor: Cursor? = null
            try {
                cursor =
                    uri?.let { requireActivity().contentResolver.query(it, null, null, null, null) }
                if (cursor != null && cursor.moveToFirst()) {
                    displayName =
                        cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        } else if (uriString.startsWith("file://")) {
            displayName = returnedFile.name
        }
        return displayName
    }
*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeSharedViewModel(v: View) {
        onboardMerchantSharedViewModel.apply {
            businessName.observe(viewLifecycleOwner,
                { businessName ->
                    BusinessName = businessName
                })
            userAccountTypeId.observe(viewLifecycleOwner,
                { userAccountTypeId ->
                    UserAccountTypeId = userAccountTypeId
                })
            merchAgentAccountTypeId.observe(viewLifecycleOwner,
                { merchAgentAccountTypeId ->
                    MerchAgentAccountTypeId = merchAgentAccountTypeId
                })
            mobileNumber.observe(viewLifecycleOwner,
                { mobileNumber ->
                    MobileNumber = mobileNumber
                })
            email.observe(viewLifecycleOwner,
                { email ->
                    Email = email
                })
            businessTypeId.observe(viewLifecycleOwner,
                { businessTypeId ->
                    BusinessTpeId = businessTypeId
                })
            businessNature.observe(viewLifecycleOwner,
                { businessNature ->
                    BusinessNature = businessNature
                })
            liquidationTypeId.observe(viewLifecycleOwner,
                { liquidationTypeId ->
                    LiquidationTypeId = liquidationTypeId
                })
            liquidationRate.observe(viewLifecycleOwner,
                { liquidationRate ->
                    LiquidationRate = liquidationRate
                })
            bankCode.observe(viewLifecycleOwner,
                { bankCode ->
                    BankCode = bankCode
                })
            branchCode.observe(viewLifecycleOwner,
                { branchCode ->
                    BranchCode = branchCode
                })
            accountName.observe(viewLifecycleOwner,
                { accountName ->
                    AccountName = accountName
                })
            accountNumber.observe(viewLifecycleOwner,
                { accountNumber ->
                    AccountNumber = accountNumber
                })
            countyCode.observe(viewLifecycleOwner,
                { countyCode ->
                    CountyCode = countyCode
                })
            townName.observe(viewLifecycleOwner,
                { townName ->
                    TownName = townName
                })
            streetName.observe(viewLifecycleOwner,
                { streetName ->
                    StreetName = streetName
                })
            buldingName.observe(viewLifecycleOwner,
                { buldingName ->
                    BuildingName = buldingName
                })
            roomNo.observe(viewLifecycleOwner,
                { roomNo ->
                    RoomNo = roomNo
                })
            termsAndConditionDoc.observe(viewLifecycleOwner,
                { termsAndConditionDoc ->
                    TermsAndConditionDoc = termsAndConditionDoc
                })
            customerPhotoPath.observe(viewLifecycleOwner,
                { customerPhotoPath ->
                    CustomerPhotoPath = customerPhotoPath
                })
            signatureDocPath.observe(viewLifecycleOwner,
                { signatureDocPath ->
                    SignatureDocPath = signatureDocPath
                })
            businessPermitDoc.observe(viewLifecycleOwner,
                { businessPermitDoc ->
                    BusinessPermitDoc = businessPermitDoc
                })
            companyRegistrationDoc.observe(viewLifecycleOwner,
                { companyRegistrationDoc ->
                    CompanyRegistrationDoc = companyRegistrationDoc
                })
            companyRegistrationDocFile.observe(viewLifecycleOwner,
                { companyRegistrationDocFile ->
                    CompanyRegistrationDocFile = companyRegistrationDocFile
                })
            companyRegistrationDocUri.observe(viewLifecycleOwner,
                { companyRegistrationDocUri ->
                    CompanyRegistrationDocUri = companyRegistrationDocUri
                })
            businessPermitDocFile.observe(viewLifecycleOwner,
                { businessPermitDocFile ->
                    BusinessPermitDocFile = businessPermitDocFile
                })
            businessPermitDocUri.observe(viewLifecycleOwner,
                { businessPermitDocUri ->
                    BusinessPermitDocUri = businessPermitDocUri
                })
            termsAndConditionDocFile.observe(viewLifecycleOwner,
                { termsAndConditionDocFile ->
                    TermsAndConditionDocFile = termsAndConditionDocFile
                })
            termsAndConditionDocUri.observe(viewLifecycleOwner,
                { termsAndConditionDocUri ->
                    TermsAndConditionDocUri = termsAndConditionDocUri
                })
            customerPhotoFile.observe(viewLifecycleOwner,
                { customerPhotoFile ->
                    CustomerPhotoFile = customerPhotoFile
                })
            customerPhotoUri.observe(viewLifecycleOwner,
                { customerPhotoUri ->
                    CustomerPhotoUri = customerPhotoUri
                })
            signatureDocFile.observe(viewLifecycleOwner,
                { signatureDocFile ->
                    SignatureDocFile = signatureDocFile
                })
            signatureDocUri.observe(viewLifecycleOwner,
                { signatureDocUri ->
                    SignatureDocUri = signatureDocUri
                })
            merchantIDNumber.observe(viewLifecycleOwner,
                { merchantIDNumber ->
                    MerchantIDNumber = merchantIDNumber
                })
            merchantSurname.observe(viewLifecycleOwner,
                { merchantSurname ->
                    Surname = merchantSurname
                })
            merchantFirstName.observe(viewLifecycleOwner,
                { merchantFirstName ->
                    FirstName = merchantFirstName
                })
            merchantLastName.observe(viewLifecycleOwner,
                { merchantLastName ->
                    LastName = merchantLastName
                })
            dob.observe(viewLifecycleOwner,
                { dob ->
                    DOB = dob
                })
            merchantGender.observe(viewLifecycleOwner,
                { merchantGender ->
                    Gender = merchantGender
                })
            frontIdCaptureFile.observe(viewLifecycleOwner,
                { frontIdCaptureFile ->
                    FrontIDFile = frontIdCaptureFile
                })
            frontIdCaptureUri.observe(viewLifecycleOwner,
                { frontIdCaptureUri ->
                    FrontIDUri = frontIdCaptureUri
                })
            backIdCaptureFile.observe(viewLifecycleOwner,
                { backIdCaptureFile ->
                    BackIDFile = backIdCaptureFile
                })
            backIdCaptureUri.observe(viewLifecycleOwner,
                { backIdCaptureUri ->
                    BackIDUri = backIdCaptureUri
                })
            goodConductFile.observe(viewLifecycleOwner,
                { goodConductFile ->
                    GoodConductFile = goodConductFile
                })
            goodConductUri.observe(viewLifecycleOwner,
                { goodConductUri ->
                    GoodConductUri = goodConductUri
                })
            fieldApplicationFormFile.observe(viewLifecycleOwner,
                { fieldApplicationFormFile ->
                    FieldApplicationFormFile = fieldApplicationFormFile
                })
            fieldApplicationFormUri.observe(viewLifecycleOwner,
                { fieldApplicationFormUri ->
                    FieldApplicationFormUri = fieldApplicationFormUri
                })
            kraPINFile.observe(viewLifecycleOwner,
                { kraPINFile ->
                    KRAPinFile = kraPINFile
                })
            kraPINUri.observe(viewLifecycleOwner,
                { kraPINUri ->
                    KRAPinUri = kraPINUri
                })
            businessLicenseFile.observe(viewLifecycleOwner,
                { businessLicenseFile ->
                    BusinessLicenseFile = businessLicenseFile
                })
            businessLicenseUri.observe(viewLifecycleOwner,
                { businessLicenseUri ->
                    BusinessLicenseUri = businessLicenseUri
                })
            roomDBId.observe(viewLifecycleOwner,
                { roomDBId ->
                    RoomRecordID = roomDBId
                })
            shopPhotoFile.observe(viewLifecycleOwner,
                { shopPhotoFile ->
                    ShopPhotoFile = shopPhotoFile
                })
        }

        //create merchant body
        val createMerchantBody = CreateMerchantBody(
            BusinessName, MobileNumber, Email, BusinessNature, BankCode, BranchCode, AccountName,
            AccountNumber, "47", TownName, StreetName, BuildingName, RoomNo,
            MerchAgentAccountTypeId, UserAccountTypeId, BusinessTpeId, LiquidationTypeId,
            LiquidationRate, getLongitude(), getLatitude(), MerchantIDNumber, Surname, FirstName,
            LastName, DOB, Gender
        )
        val json = Gson().toJson(createMerchantBody)
        Log.d("json", "observeSharedViewModel: $json")
        val merchDetails = RequestBody.create(MultipartBody.FORM, json)
        GlobalScope.launch(Dispatchers.Main) {
            val compressedCompanyRegistrationDocFile =
                Compressor.compress(requireContext(), CompanyRegistrationDocFile)
            val companyRegistrationFile = MultipartBody.Part.createFormData(
                "companyRegistrationDoc",
                CompanyRegistrationDocFile.name, RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    compressedCompanyRegistrationDocFile
                )
            )
            val compressedTermsAndConditionDocFile =
                Compressor.compress(requireContext(), TermsAndConditionDocFile)
            val termsAndConditionFile = MultipartBody.Part.createFormData(
                "termsAndConditionDoc",
                TermsAndConditionDocFile.name, RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    compressedTermsAndConditionDocFile
                )
            )
            val compressedCustomerPhotoFile =
                Compressor.compress(requireContext(), CustomerPhotoFile)
            val customerPhotoFile = MultipartBody.Part.createFormData(
                "customerPhoto",
                CustomerPhotoFile.name, RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    compressedCustomerPhotoFile
                )
            )
            val compressedSignatureDocFile = Compressor.compress(requireContext(), SignatureDocFile)
            val signatureDocFile = MultipartBody.Part.createFormData(
                "signatureDoc",
                SignatureDocFile.name, RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    compressedSignatureDocFile
                )
            )
            val compressedBusinessPermitDocFile =
                Compressor.compress(requireContext(), BusinessPermitDocFile)
            val businessPermitDocFile = MultipartBody.Part.createFormData(
                "businessPermitDoc",
                BusinessPermitDocFile.name, RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    compressedBusinessPermitDocFile
                )
            )
            val compressedFrontIDFile = Compressor.compress(requireContext(), FrontIDFile)
            val frontIDFile = MultipartBody.Part.createFormData(
                "frontID",
                FrontIDFile.name, RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    compressedFrontIDFile
                )
            )
            val compressedBackIDFile = Compressor.compress(requireContext(), BackIDFile)
            val backIDFile = MultipartBody.Part.createFormData(
                "backID",
                BackIDFile.name, RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    compressedBackIDFile
                )
            )
            val compressedGoodConductFile = Compressor.compress(requireContext(), GoodConductFile)
            val goodConductFile = MultipartBody.Part.createFormData(
                "certificateOFGoodConduct",
                GoodConductFile.name, RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    compressedGoodConductFile
                )
            )
            val compressedFieldApplicationFormFile =
                Compressor.compress(requireContext(), FieldApplicationFormFile)
            val fieldApplicationFormFile = MultipartBody.Part.createFormData(
                "fieldApplicationForm",
                FieldApplicationFormFile.name, RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    compressedFieldApplicationFormFile
                )
            )
            val compressedKRAPinFile = Compressor.compress(requireContext(), KRAPinFile)
            val kraPinFile = MultipartBody.Part.createFormData(
                "kraPinCertificate",
                KRAPinFile.name, RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    compressedKRAPinFile
                )
            )
            val compressedBusinessLicenseFile =
                Compressor.compress(requireContext(), BusinessLicenseFile)
            val businessLicenseFile = MultipartBody.Part.createFormData(
                "businessLicense",
                BusinessLicenseFile.name, RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    compressedBusinessLicenseFile
                )
            )
            val compressedShopPhotoFile = Compressor.compress(requireContext(), ShopPhotoFile)
            val shopPhotoFile = MultipartBody.Part.createFormData(
                "shopPhoto",
                ShopPhotoFile.name, RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    compressedShopPhotoFile
                )
            )
            //createMerchantAgent(createMerchantBody, v)
            saveDataLocally()
            if (isNetworkAvailable(requireContext())) {
                onboardMerchantAgentAccount(
                    merchDetails, termsAndConditionFile, customerPhotoFile, signatureDocFile,
                    businessPermitDocFile, companyRegistrationFile, frontIDFile, backIDFile,
                    goodConductFile, fieldApplicationFormFile, kraPinFile, businessLicenseFile,
                    shopPhotoFile, v, RoomRecordID
                )
            } else {
                showNoInternetDialog(v)
            }
        }
    }

    private fun onboardMerchantAgentAccount(
        merchDetails: RequestBody,
        termsAndConditionDoc: MultipartBody.Part,
        customerPhoto: MultipartBody.Part,
        signatureDoc: MultipartBody.Part,
        businessPermitDoc: MultipartBody.Part,
        companyRegistrationDoc: MultipartBody.Part,
        frontIDFile: MultipartBody.Part,
        backIDFile: MultipartBody.Part,
        goodConductFile: MultipartBody.Part,
        fieldApplicationFormFile: MultipartBody.Part,
        kraPinFile: MultipartBody.Part,
        businessLicenseFile: MultipartBody.Part,
        shopPhotoFile: MultipartBody.Part,
        v: View, roomRecordID: Int
    ) {
        Constants.callDialog2("Submitting...", requireContext())
        onboardAccountViewModel.onboardMerchantAgentAccount(
            merchDetails, termsAndConditionDoc, customerPhoto, signatureDoc,
            businessPermitDoc, companyRegistrationDoc, frontIDFile, backIDFile, goodConductFile,
            fieldApplicationFormFile, kraPinFile, businessLicenseFile, shopPhotoFile
        )
            .observe(viewLifecycleOwner) { createMerchantAgentResponse ->
                if (createMerchantAgentResponse != null) {
                    Constants.cancelDialog()
                    when (createMerchantAgentResponse.status) {
                        "success" -> {
                            Toasty.success(
                                requireContext(),
                                createMerchantAgentResponse.message,
                                Toasty.LENGTH_LONG
                            ).show()
                            Log.d(
                                "message",
                                "onboardMerchantAgentAccount: ${createMerchantAgentResponse.message}"
                            )
                            deleteRecordLocally(roomRecordID)
                        }
                        "failed" -> {
                            Toasty.error(
                                requireContext(),
                                createMerchantAgentResponse.message,
                                Toasty.LENGTH_LONG
                            ).show()
                            showErrorDialog(v)
                        }
                    }
                } else {
                    Constants.cancelDialog()
                    showErrorDialog(v)
                    Toasty.error(
                        requireContext(),
                        "An error occurred. Please try again",
                        Toasty.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun deleteRecordLocally(roomRecordID: Int) {
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(roomDBViewModel.deleteMerchantAgentRecord(roomRecordID)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                compositeDisposable.dispose()
            })
    }

    private fun showNoInternetDialog(v: View) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("No Internet")
        builder.setIcon(R.drawable.ic_info)
        builder.setMessage(
            "You are offline. Data has been saved locally and you can sync to the online server later"
        )


        builder.setNegativeButton("Try Again") { _, _ ->
        }

        builder.setNeutralButton("Cancel") { _, _ ->
        }
        builder.show()
    }

    private fun showErrorDialog(v: View) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        builder.setIcon(R.drawable.ic_info)
        builder.setMessage(
            "An error occurred while submitting the data. Data has been saved locally " +
                    "and you can sync to the online server later"
        )



        builder.setNegativeButton("Try Again") { _, _ ->
        }

        builder.setNeutralButton("Cancel") { _, _ ->
        }
        builder.show()
    }

    companion object {
        private lateinit var BusinessName: String
        private var UserAccountTypeId by Delegates.notNull<Int>()
        private var MerchAgentAccountTypeId by Delegates.notNull<Int>()
        private var BusinessTpeId by Delegates.notNull<Int>()
        private lateinit var MobileNumber: String
        private lateinit var Email: String
        private lateinit var BusinessNature: String
        private var LiquidationTypeId by Delegates.notNull<Int>()
        private var LiquidationRate by Delegates.notNull<Int>()
        private lateinit var BankCode: String
        private lateinit var BranchCode: String
        private lateinit var AccountName: String
        private lateinit var AccountNumber: String
        private lateinit var CountyCode: String
        private lateinit var TownName: String
        private lateinit var StreetName: String
        private var RoomRecordID by Delegates.notNull<Int>()
        private lateinit var BuildingName: String
        private lateinit var RoomNo: String
        private lateinit var TermsAndConditionDoc: String
        private lateinit var CustomerPhotoPath: String
        private lateinit var SignatureDocPath: String
        private lateinit var BusinessPermitDoc: String
        private lateinit var CompanyRegistrationDoc: String
        private lateinit var TermsAndConditionDocFile: File
        private lateinit var TermsAndConditionDocUri: Uri
        private lateinit var CustomerPhotoFile: File
        private lateinit var CustomerPhotoUri: Uri
        private lateinit var SignatureDocFile: File
        private lateinit var SignatureDocUri: Uri
        private lateinit var BusinessPermitDocFile: File
        private lateinit var BusinessPermitDocUri: Uri
        private lateinit var CompanyRegistrationDocFile: File
        private lateinit var CompanyRegistrationDocUri: Uri
        private lateinit var MerchantIDNumber: String
        private lateinit var Surname: String
        private lateinit var FirstName: String
        private lateinit var LastName: String
        private lateinit var DOB: String
        private lateinit var Gender: String
        private lateinit var FrontIDFile: File
        private lateinit var FrontIDUri: Uri
        private lateinit var BackIDFile: File
        private lateinit var BackIDUri: Uri
        private lateinit var GoodConductFile: File
        private lateinit var GoodConductUri: Uri
        private lateinit var FieldApplicationFormFile: File
        private lateinit var FieldApplicationFormUri: Uri
        private lateinit var KRAPinFile: File
        private lateinit var KRAPinUri: Uri
        private lateinit var BusinessLicenseFile: File
        private lateinit var BusinessLicenseUri: Uri
        private lateinit var ShopPhotoPath: String
        private lateinit var ShopPhotoFile: File
        private const val REQUEST_CODE_STORAGE_PERMISSION = 1

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AgentBusinessImagesFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}