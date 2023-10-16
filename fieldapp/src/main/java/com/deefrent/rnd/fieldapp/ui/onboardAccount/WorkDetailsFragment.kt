package com.deefrent.rnd.fieldapp.ui.onboardAccount

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.bodies.onboardAccount.CreateCustomerBody
import com.deefrent.rnd.fieldapp.databinding.FragmentWorkDetailsBinding
import com.deefrent.rnd.fieldapp.models.employmentTypes.EmploymentType
import com.deefrent.rnd.fieldapp.network.models.GenderItems
import com.deefrent.rnd.fieldapp.view.homepage.LoginSessionSharedViewModel
import com.deefrent.rnd.fieldapp.utils.Constants
import com.deefrent.rnd.fieldapp.utils.isNetworkAvailable
import com.deefrent.rnd.fieldapp.viewModels.DataViewModel
import com.deefrent.rnd.fieldapp.viewModels.OnboardAccountViewModel
import com.deefrent.rnd.fieldapp.viewModels.RoomDBViewModel
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import id.zelory.compressor.Compressor
import io.reactivex.disposables.CompositeDisposable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import kotlin.properties.Delegates
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class WorkDetailsFragment : Fragment() {
    private var _binding: FragmentWorkDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataViewModel: DataViewModel
    private val sharedViewModel: OnboardAccountSharedViewModel by activityViewModels()
    private val loginSessionSharedViewModel: LoginSessionSharedViewModel by activityViewModels()
    private lateinit var onboardAccountViewModel: OnboardAccountViewModel
    private lateinit var roomDBViewModel: RoomDBViewModel
    private var locationSharedPreferences: SharedPreferences? = null
    private var IsFromIncompleteDialog = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        roomDBViewModel = ViewModelProvider(this).get(RoomDBViewModel::class.java)
        onboardAccountViewModel = ViewModelProvider(this).get(OnboardAccountViewModel::class.java)
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        _binding = FragmentWorkDetailsBinding.inflate(inflater, container, false)
        val view = binding.root
        observeLoginSharedViewModel()
        locationSharedPreferences =
            requireContext().getSharedPreferences("location", Context.MODE_PRIVATE)
        getEmploymentTypes()
        initViews()
        observeSharedViewModel()
        binding.btnContinue.setOnClickListener { v ->
            //callDialog("Submitting...",requireContext(),v)
            if (isValid()) {
                saveDataLocally(v)
                if (isNetworkAvailable(requireContext())) {
                    val createCustomerBody =
                        CreateCustomerBody(
                            userAccountType,
                            phoneNumber,
                            personalAccountType,
                            branchId,
                            idNo,
                            Surname,
                            fName,
                            lName,
                            1,
                            DOB,
                            gender1,
                            25000,
                            binding.etWorkplace.text.toString(),
                            employment,
                            binding.etPurpose.text.toString(),
                            getLongitude(),
                            getLatitude()
                        )
                    //createCustomer(createCustomerBody, v)
                    val json = Gson().toJson(createCustomerBody)
                    val customerData = RequestBody.create(MultipartBody.FORM, json)
                    GlobalScope.launch(Dispatchers.Main) {
                        //val file= getFileFromInternalStorage(Constants.IMAGES_DIR,"field_app_20210903T161608.png")
                        val compressedFrontID =
                            Compressor.compress(requireContext(), FrontIdCaptureFile)
                        val frontIdFile = MultipartBody.Part.createFormData(
                            "frontIdCapture",
                            FrontIdCaptureFile.name, RequestBody.create(
                                "multipart/form-data".toMediaTypeOrNull(),
                                compressedFrontID
                            )
                        )
                        val compressedBackID =
                            Compressor.compress(requireContext(), BackIdCaptureFile)
                        val backIdFile = MultipartBody.Part.createFormData(
                            "backIdCapture",
                            BackIdCaptureFile.name, RequestBody.create(
                                "multipart/form-data".toMediaTypeOrNull(),
                                compressedBackID
                            )
                        )
                        val compressedPassport =
                            Compressor.compress(requireContext(), PassportPhotoCaptureFile)
                        val passportFile = MultipartBody.Part.createFormData(
                            "passportPhotoCapture",
                            PassportPhotoCaptureFile.name, RequestBody.create(
                                "multipart/form-data".toMediaTypeOrNull(),
                                compressedPassport
                            )
                        )
                        onboardIndividualAccount(
                            customerData, frontIdFile, backIdFile, passportFile,
                            RoomDBID, v
                        )
                    }

                } else {
                    showNoInternetDialog(v)
                }
            }
        }

        return view
    }

    private fun observeLoginSharedViewModel() {
        loginSessionSharedViewModel.apply {
            isFromIncompleteDialog.observe(viewLifecycleOwner
            ) { isFromIncompleteDialog ->
                if (isFromIncompleteDialog) {
                    IsFromIncompleteDialog = true
                }
            }
        }
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

    private fun onboardIndividualAccount(
        customerdata: RequestBody,
        frontIdCapture: MultipartBody.Part,
        backIdCapture: MultipartBody.Part,
        passportPhotoCapture: MultipartBody.Part,
        roomDBID: Int,
        v: View
    ) {
        Constants.callDialog2("Submitting...", requireContext())
        onboardAccountViewModel.onboardIndividualAccount(
            customerdata,
            frontIdCapture,
            backIdCapture,
            passportPhotoCapture
        )
            .observe(viewLifecycleOwner) { createCustomerResponse ->
                if (createCustomerResponse != null) {
                    Constants.cancelDialog()
                    when (createCustomerResponse.status) {
                        "success" -> {
                            Toasty.success(
                                requireContext(),
                                createCustomerResponse.message,
                                Toasty.LENGTH_LONG
                            ).show()
                            deleteRecordLocally(roomDBID)
                        }
                        "failed" -> {
                            Toasty.error(
                                requireContext(),
                                createCustomerResponse.message,
                                Toasty.LENGTH_LONG
                            ).show()
                            showErrorDialog(v)
                        }
                    }
                } else {
                    Constants.cancelDialog()
                    showErrorDialog(v)
                    Toasty.error(requireContext(), "An error occurred. Please try again", Toasty.LENGTH_LONG).show()
                }
            }
    }

    private fun deleteRecordLocally(roomRecordID: Int) {
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(roomDBViewModel.deleteCustomerRecord(roomRecordID)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                compositeDisposable.dispose()
            })
    }

    private fun getLatitude(): String? {
        return locationSharedPreferences?.getString("latitude", "nothing")
    }

    private fun getLongitude(): String? {
        return locationSharedPreferences?.getString("longitude", "nothing")
    }

    private fun isValid(): Boolean {
        var isValid: Boolean
        if (binding.acEmploymentType.text.toString()
                .isNullOrEmpty() || binding.etWorkplace.text.toString().isNullOrEmpty() ||
            binding.acIncomeBand.text.toString()
                .isNullOrEmpty() || binding.etPurpose.text.toString().isNullOrEmpty()
        ) {
            isValid = false
            Toasty.error(requireContext(), "Please fill in all the details", Toasty.LENGTH_LONG)
                .show()
        } else {
            isValid = true
        }
        return isValid
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

    private fun getEmploymentTypes() {
        dataViewModel.fetchEmploymentTypes()
            .observe(viewLifecycleOwner) { fetchEmploymentTypesResponse ->
                if (fetchEmploymentTypesResponse != null) {
                    val employmentTypes: List<EmploymentType> =
                        fetchEmploymentTypesResponse.employmentTypesData.employmentTypes
                    populateEmploymentTypes(employmentTypes)
                } else {
                    Toasty.error(requireContext(), "An error occurred. Please try again", Toasty.LENGTH_LONG).show()
                }
            }
    }

    private fun populateEmploymentTypes(fetchEmploymentTypesList: List<EmploymentType>) {
        //populate employment type dropdown
        val employmentTypesAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                fetchEmploymentTypesList
            )
        binding.acEmploymentType.setAdapter(employmentTypesAdapter)
        binding.acEmploymentType.keyListener = null
        binding.acEmploymentType.setOnItemClickListener { parent, _, position, _ ->
            val selected: EmploymentType = parent.adapter.getItem(position) as EmploymentType
            sharedViewModel.setEmploymentType(selected.id)
        }
    }

    private fun saveDataLocally(v: View) {
        var RoomDBId = 0
        sharedViewModel.apply {
            roomDBId.observe(viewLifecycleOwner,
                { roomDBId ->
                    RoomDBId = roomDBId
                })
            employmentType.observe(viewLifecycleOwner,
                { employmentType ->
                    employment = employmentType
                })
        }
        val longitudeValue = getLongitude()
        val latitudeValue = getLatitude()
        val lastStep = this::class.java.simpleName
        val Complete = true
        //Log.d("json", "saveDataLocally: ${Gson().toJson(individualAccountDetails)}")
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(roomDBViewModel.updateCustomerWorkDetails(
            employment,
            binding.etWorkplace.text.toString(),
            binding.etPurpose.text.toString(),
            binding.acIncomeBand.text.toString(),
            latitudeValue,
            longitudeValue,
            lastStep,
            Complete,
            RoomDBId
        )
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                compositeDisposable.dispose()
            })
    }

    private fun observeSharedViewModel() {
        sharedViewModel.apply {
            personalAccountTypeId.observe(viewLifecycleOwner,
                { personalAccountTypeId ->
                    personalAccountType = personalAccountTypeId
                })
            userAccountTypeId.observe(viewLifecycleOwner,
                { userAccountTypeId ->
                    userAccountType = userAccountTypeId
                })
            KCBBranchId.observe(viewLifecycleOwner,
                { KCBBranchId ->
                    branchId = KCBBranchId
                })
            phoneNo.observe(viewLifecycleOwner,
                { phoneNo ->
                    phoneNumber = phoneNo
                })
            idNumber.observe(viewLifecycleOwner,
                { idNumber ->
                    idNo = idNumber
                })
            surname.observe(viewLifecycleOwner,
                { surname ->
                    Surname = surname
                })
            firstName.observe(viewLifecycleOwner,
                { firstName ->
                    fName = firstName
                })
            lastName.observe(viewLifecycleOwner,
                { lastName ->
                    lName = lastName
                })
            dob.observe(viewLifecycleOwner,
                { dob ->
                    DOB = dob
                })
            gender.observe(viewLifecycleOwner,
                { gender ->
                    gender1 = gender
                })
            frontIdCapture.observe(viewLifecycleOwner,
                { frontIdCapture ->
                    frontID = frontIdCapture
                })
            backIdCapture.observe(viewLifecycleOwner,
                { backIdCapture ->
                    backID = backIdCapture
                })
            passportPhoto.observe(viewLifecycleOwner,
                { passportPhoto ->
                    passport = passportPhoto
                })
            income.observe(viewLifecycleOwner,
                { income ->
                    incomeRange = income
                })
            workLocation.observe(viewLifecycleOwner,
                { workLocation ->
                    workPlace = workLocation
                })
            employmentType.observe(viewLifecycleOwner,
                { employmentType ->
                    employment = employmentType
                })
            accountOpeningPurpose.observe(viewLifecycleOwner,
                { accountOpeningPurpose ->
                    purpose = accountOpeningPurpose
                })
            frontIdCaptureFile.observe(viewLifecycleOwner,
                { frontIdCaptureFile ->
                    FrontIdCaptureFile = frontIdCaptureFile
                })
            backIdCaptureFile.observe(viewLifecycleOwner,
                { backIdCaptureFile ->
                    BackIdCaptureFile = backIdCaptureFile
                })
            passportPhotoCaptureFile.observe(viewLifecycleOwner,
                { passportPhotoCaptureFile ->
                    PassportPhotoCaptureFile = passportPhotoCaptureFile
                })
            frontIdCaptureUri.observe(viewLifecycleOwner,
                { frontIdCaptureUri ->
                    FrontIdCaptureUri = frontIdCaptureUri
                })
            backIdCaptureUri.observe(viewLifecycleOwner,
                { backIdCaptureUri ->
                    BackIdCaptureUri = backIdCaptureUri
                })
            passportPhotoCaptureUri.observe(viewLifecycleOwner,
                { passportPhotoCaptureUri ->
                    PassportPhotoCaptureUri = passportPhotoCaptureUri
                })
            roomDBId.observe(viewLifecycleOwner,
                { roomDBId ->
                    RoomDBID = roomDBId
                })
        }

    }

    private fun initViews() {
        //populate income band dropdown
        binding.acEmploymentType.keyListener = null
        val incomeBands = resources.getStringArray(R.array.income_bands)
        val incomeBandsAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, incomeBands)
        binding.acIncomeBand.setAdapter(incomeBandsAdapter)
        binding.acIncomeBand.keyListener = null
        binding.acIncomeBand.setOnItemClickListener { _, _, _, _ ->
            val selected = binding.acIncomeBand.text.toString()
            sharedViewModel.setIncome(selected)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private lateinit var phoneNumber: String
        private var userAccountType by Delegates.notNull<Int>()
        private var personalAccountType by Delegates.notNull<Int>()
        private var branchId by Delegates.notNull<Int>()
        private lateinit var idNo: String
        private lateinit var Surname: String
        private lateinit var fName: String
        private lateinit var lName: String
        private lateinit var DOB: String
        private lateinit var gender1: String
        private lateinit var frontID: String
        private lateinit var backID: String
        private lateinit var passport: String
        private lateinit var workPlace: String
        private lateinit var incomeRange: String
        private lateinit var list: List<GenderItems>
        private var employment by Delegates.notNull<Int>()
        private lateinit var purpose: String
        private lateinit var FrontIdCaptureFile: File
        private lateinit var FrontIdCaptureUri: Uri
        private lateinit var FrontIdPath: String
        private lateinit var BackIdCaptureFile: File
        private lateinit var BackIdCaptureUri: Uri
        private lateinit var BackIdPath: String
        private lateinit var PassportPhotoCaptureFile: File
        private lateinit var PassportPhotoCaptureUri: Uri
        private lateinit var PassportPhotoPath: String
        private var RoomDBID by Delegates.notNull<Int>()

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WorkDetailsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}