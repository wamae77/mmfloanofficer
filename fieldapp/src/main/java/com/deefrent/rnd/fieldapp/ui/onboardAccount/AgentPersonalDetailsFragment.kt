package com.deefrent.rnd.fieldapp.ui.onboardAccount

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentAgentPersonalDetailsBinding
import com.deefrent.rnd.fieldapp.models.idType.IdentificationType
import com.deefrent.rnd.fieldapp.view.homepage.LoginSessionSharedViewModel
import com.deefrent.rnd.fieldapp.viewModels.DataViewModel
import com.deefrent.rnd.fieldapp.viewModels.OnboardAccountViewModel
import com.deefrent.rnd.fieldapp.viewModels.RoomDBViewModel
import dev.ronnie.github.imagepicker.ImagePicker
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_customer_details.*
import java.text.SimpleDateFormat
import java.util.*

class AgentPersonalDetailsFragment : Fragment() {
    private lateinit var roomDBViewModel: RoomDBViewModel
    private val onboardMerchantSharedViewModel: OnboardMerchantSharedViewModel by activityViewModels()
    private lateinit var onboardAccountViewModel: OnboardAccountViewModel
    private var _binding: FragmentAgentPersonalDetailsBinding? = null
    private val binding get() = _binding!!
    lateinit var imagePicker: ImagePicker
    private lateinit var dataViewModel: DataViewModel
    private val loginSessionSharedViewModel: LoginSessionSharedViewModel by activityViewModels()
    private var IsFromIncompleteDialog = false

    private val captureOption = ""
    private val isCard = true
    private val cardType = ""

    private var cardBitmap: Bitmap? = null
    private var faceBitmap: Bitmap? = null

    //private val scannerInterfaceImp = ScannerInterfaceImp()

    private lateinit var calendar: Calendar

  /*  private val cardResultsCallback: ScannerInterfaceImp.CardResults = object :
        ScannerInterfaceImp.CardResults {
        override fun onCardResult(scanResult: IdScanResult) {
            if (scanResult != null) {
                binding.ivScanID.setImageBitmap(scanResult.card)
                cardBitmap = scanResult.card
                extractData(scanResult.details)
            }
        }

        override fun onCardFailure(error: String) {
            Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
        }
    }*/

    /*private val livenessResultsCallback: ScannerInterfaceImp.LivenessResults = object :
        ScannerInterfaceImp.LivenessResults {
        override fun isLive(result: Boolean) {
            if (result) Toast.makeText(requireContext(), "Face is live", Toast.LENGTH_SHORT)
                .show() else Toast.makeText(
                requireContext(),
                "Face is not live",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun checkFailed(error: String) {
            Toast.makeText(
                requireContext(),
                "Liveness check has failed $error",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun faceImage(face: Bitmap) {
            binding.ivScanID.setImageBitmap(face)
            faceBitmap = face
        }
    }*/

  /*  private val verificationScoreCallback: ScannerInterfaceImp.VerificationScore = object :
        ScannerInterfaceImp.VerificationScore {
        override fun matchScore(result: Float) {
            showResultsDialog(result)
        }

        override fun matchFailed(error: String) {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }*/

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
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        onboardAccountViewModel = ViewModelProvider(this).get(OnboardAccountViewModel::class.java)
        _binding = FragmentAgentPersonalDetailsBinding.inflate(inflater, container, false)
        val view = binding.root
        observeLoginSharedViewModel()
        binding.btnContinue.setOnClickListener { v ->
            if (isValid()) {
                onboardMerchantSharedViewModel.setMerchantFirstName(binding.etFirstName.text.toString())
                onboardMerchantSharedViewModel.setMerchantLastName(binding.etLastName.text.toString())
                onboardMerchantSharedViewModel.setMerchantSurname(binding.etSurname.text.toString())
                onboardMerchantSharedViewModel.setMerchantIDNumber(binding.etIDNumber.text.toString())
                onboardMerchantSharedViewModel.setMerchantGender(binding.acGender.text.toString())
                onboardMerchantSharedViewModel.setDob(binding.etDOB.text.toString())
                saveDataLocally(
                    binding.etFirstName.text.toString(), binding.etLastName.text.toString(),
                    binding.etSurname.text.toString(), binding.etIDNumber.text.toString(),
                    binding.acGender.text.toString(), binding.etDOB.text.toString(),
                    binding.acIDType.text.toString()
                )
            }
        }

        binding.switchScanID.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                true -> {
                    binding.ivScanID.visibility = View.VISIBLE
                    binding.btnTakePhoto.visibility = View.VISIBLE
                    binding.clDetails.visibility = View.GONE
                }
                else -> {
                    binding.ivScanID.visibility = View.GONE
                    binding.btnTakePhoto.visibility = View.GONE
                    binding.clDetails.visibility = View.VISIBLE
                }
            }
        }
        binding.btnTakePhoto.setOnClickListener { v ->
            //Check for camera permission
          /*  if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) requestCameraPermission()
            scannerInterfaceImp.scanIdCard(
                requireContext(),
                ScannerInterface.CardType.IDCARD,
                cardResultsCallback
            )*/
        }
        binding.etDOB.setOnClickListener {
            showDatePicker()
        }
        initViews()
        getIDTypes()
        observeSharedViewModel()
        return view
    }

    private fun showDatePicker() {
        calendar = Calendar.getInstance()
        val dialog = DatePickerDialog(requireContext(), { _, year, month, day_of_month ->
            calendar[Calendar.YEAR] = year
            calendar[Calendar.MONTH] = month
            calendar[Calendar.DAY_OF_MONTH] = day_of_month
            val myFormat = "yyyy-MM-dd"
            val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
            binding.etDOB.setText(sdf.format(calendar.time))
        }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
        // dialog.datePicker.minDate = calendar.timeInMillis
        calendar.add(Calendar.YEAR, -18)
        dialog.datePicker.maxDate = calendar.timeInMillis
        dialog.show()
    }

    private fun observeLoginSharedViewModel() {
        loginSessionSharedViewModel.apply {
            isFromIncompleteDialog.observe(viewLifecycleOwner
            ) { isFromIncompleteDialog ->
                if (isFromIncompleteDialog) {
                    IsFromIncompleteDialog = true
                    onboardMerchantSharedViewModel.apply {
                        merchantIDNumber.observe(viewLifecycleOwner
                        ) { idNumber ->
                            binding.etIDNumber.setText(idNumber)
                        }
                        merchantSurname.observe(viewLifecycleOwner,
                            { surname ->
                                binding.etSurname.setText(surname)
                            })
                        merchantFirstName.observe(viewLifecycleOwner,
                            { firstName ->
                                binding.etFirstName.setText(firstName)
                            })
                        merchantLastName.observe(viewLifecycleOwner,
                            { lastName ->
                                binding.etLastName.setText(lastName)
                            })
                        dob.observe(viewLifecycleOwner,
                            { dob ->
                                binding.etDOB.setText(dob)
                            })
                        merchantGender.observe(viewLifecycleOwner,
                            { gender ->
                                binding.acGender.setText(gender)
                                val genders = resources.getStringArray(R.array.genders)
                                val gendersAdapter =
                                    ArrayAdapter(
                                        requireContext(),
                                        android.R.layout.simple_list_item_1,
                                        genders
                                    )
                                binding.acGender.setAdapter(gendersAdapter)
                            })
                    }
                }
            }
        }
    }

    private fun saveDataLocally(
        MerchantFirstName: String, MerchantLastName: String,
        MerchantSurname: String, MerchantIDNumber: String,
        MerchantGender: String, MerchantDOB: String, IDType: String
    ) {
        var RoomDBId = 0
        var lastStep1 = ""
        val lastStep2 = this::class.java.simpleName
        onboardMerchantSharedViewModel.apply {
            roomDBId.observe(viewLifecycleOwner,
                { roomDBId ->
                    RoomDBId = roomDBId
                })
            lastStep.observe(viewLifecycleOwner,
                { lastStep ->
                    lastStep1 = if (IsFromIncompleteDialog) {
                        lastStep
                    } else {
                        lastStep2
                    }
                })
        }
        onboardMerchantSharedViewModel.setLastStep(this::class.java.simpleName)
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(roomDBViewModel.updateMerchantPersonalDetails(
            MerchantIDNumber, MerchantSurname, MerchantFirstName, MerchantLastName, MerchantDOB,
            MerchantGender, lastStep1, IDType, RoomDBId
        )
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                compositeDisposable.dispose()
            })
    }

    private fun getIDTypes() {
        dataViewModel.fetchIDTypes()
            .observe(viewLifecycleOwner) { fetchIDTypesResponse ->
                if (fetchIDTypesResponse != null) {
                    val idTypes: List<IdentificationType> =
                        fetchIDTypesResponse.getIDTypeData.identificationTypeList
                    populateIDTypes(idTypes)
                } else {
                    Toasty.error(
                        requireContext(),
                        "An error occurred. Please try again",
                        Toasty.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun populateIDTypes(idTypes: List<IdentificationType>) {
        //populate ID type dropdown
        val idTypesAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, idTypes)
        binding.acIDType.keyListener = null
        binding.acIDType.setOnItemClickListener { _, _, position, _ ->
            // You can get the label or item that the user clicked:
            when (binding.acIDType.text.toString()) {
                "National ID" -> {
                    binding.tiIDNumber.hint = "National ID Number"
                    binding.rgScanCaptureDetails.visibility = View.VISIBLE
                }
                "Passport" -> {
                    binding.tiIDNumber.hint = "Passport Number"
                    binding.rgScanCaptureDetails.visibility = View.GONE
                }
            }
        }
        if (IsFromIncompleteDialog) {
            onboardMerchantSharedViewModel.apply {
                idType.observe(viewLifecycleOwner,
                    { idType ->
                        binding.acIDType.setText(idType)
                        binding.acIDType.setAdapter(idTypesAdapter)
                        when (idType) {
                            "National ID" -> {
                                binding.tiIDNumber.hint = "National ID Number"
                                binding.rgScanCaptureDetails.visibility = View.VISIBLE
                            }
                            "Passport" -> {
                                binding.tiIDNumber.hint = "Passport Number"
                                binding.rgScanCaptureDetails.visibility = View.GONE
                            }
                        }
                    })
            }
        } else {
            binding.acIDType.setAdapter(idTypesAdapter)
        }
    }

    /**
     * Method requests for camera permission
     */
    private fun requestCameraPermission() {
        val permissions = arrayOf(Manifest.permission.CAMERA)
        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.CAMERA
            )
        ) {
            val CAMERA_PERMISSION = 111
            ActivityCompat.requestPermissions(requireActivity(), permissions, CAMERA_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode != 111) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if (grantResults.size != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(
                requireContext(),
                "You won't be able to access the functionality",
                Toast.LENGTH_LONG
            ).show()
        }
    }

   /* private fun extractData(details: CardDetails) {
        binding.etFirstName.setText(details.firstName)
        binding.etLastName.setText(details.middleName)
        binding.etSurname.setText(details.surname)
        binding.etIDNumber.setText(details.cardNumber)
        binding.acGender.setText(details.gender)
        binding.etDOB.setText(formatScannedDOB(details.dob))
        *//*tvPlaceOfBirth.setText("Place of birth: " + details.placeOfBirth)
        tvDateOfIssue.setText("Date of issue: " + details.dateOfIssue)*//*
    }*/

    private fun showResultsDialog(similarity: Float) {
        AlertDialog.Builder(requireContext())
            .setTitle("Verification Results!")
            .setMessage("Similarity score is: $similarity")
            .setIcon(R.drawable.ic_info)
            .setPositiveButton(
                "CLOSE"
            ) { dialog: DialogInterface, id: Int -> dialog.cancel() }.show()
    }

    private fun isValid(): Boolean {
        var isValid = false
        if (binding.acIDType.text.toString().isEmpty() || binding.etIDNumber.text.toString()
                .isEmpty()
            || binding.etSurname.text.toString().isEmpty() || binding.etFirstName.text.toString()
                .isEmpty() ||
            binding.etLastName.text.toString().isEmpty() || etDOB.text.toString()
                .isEmpty() || binding.acGender.text.toString().isEmpty()
        ) {
            isValid = false
            Toasty.error(requireContext(), "Please fill in all details", Toasty.LENGTH_LONG).show()
        } else if (binding.etIDNumber.text.toString().length < 8) {
            Log.d("TAG", "length is: ${binding.etIDNumber.text.toString().length}")
            isValid = false
            Toasty.error(requireContext(), "Please enter a valid ID Number", Toasty.LENGTH_LONG)
                .show()
        } else if (binding.etIDNumber.text.toString().length > 10) {
            Log.d("TAG", "length is: ${binding.etIDNumber.text.toString().length}")
            isValid = false
            Toasty.error(requireContext(), "Please enter a valid ID Number", Toasty.LENGTH_LONG)
                .show()
        } else {
            isValid = true
        }
        return isValid
    }

    private fun initViews() {
        binding.etDOB.keyListener = null
        //populate genders dropdown
        val genders = resources.getStringArray(R.array.genders)
        val gendersAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, genders)
        binding.acGender.setAdapter(gendersAdapter)
        binding.acGender.keyListener = null
        //radio buttons listener
        binding.rgScanCaptureDetails.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = requireActivity().findViewById(checkedId)
            when (radio.text) {
                requireContext().getString(R.string.scan_details) -> {
                    binding.btnTakePhoto.visibility = View.VISIBLE
                    binding.ivScanID.visibility = View.VISIBLE
                }
                requireContext().getString(R.string.type_in_details) -> {
                    binding.btnTakePhoto.visibility = View.GONE
                    binding.ivScanID.visibility = View.GONE
                }
            }
        }
    }

    /*private fun showMaterialDatePicker() {
        val materialDateBuilder: MaterialDatePicker.Builder<*> =
            MaterialDatePicker.Builder.datePicker()
        materialDateBuilder.setTitleText("DATE OF BIRTH")
        val materialDatePicker: MaterialDatePicker<Long> =
            materialDateBuilder.build() as MaterialDatePicker<Long>
        materialDatePicker.addOnPositiveButtonClickListener { selection: Long ->
            // Get the offset from our timezone and UTC.
            val timeZoneUTC: TimeZone = TimeZone.getDefault()
            // It will be negative, so that's the -1
            val offsetFromUTC: Int = timeZoneUTC.getOffset(Date().getTime()) * -1
            // Create a date format, then a date object with our offset
            val simpleFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            val simpleFormat1 = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = Date(selection + offsetFromUTC)
            Log.d("showMaterialDatePicker", "date: " + simpleFormat1.format(date).toString())
            binding.etDOB.setText(simpleFormat1.format(date).toString())
            onboardMerchantSharedViewModel.setDob(simpleFormat1.format(date).toString())
        }
        materialDatePicker.show(requireActivity().supportFragmentManager, "MATERIAL_DATE_PICKER")
    }*/

    private fun observeSharedViewModel() {
        onboardMerchantSharedViewModel.apply {
            userType.observe(viewLifecycleOwner,
                { userType ->
                    binding.tvFragmentTitle.text = "$userType's Personal Details"
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AgentPersonalDetailsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}