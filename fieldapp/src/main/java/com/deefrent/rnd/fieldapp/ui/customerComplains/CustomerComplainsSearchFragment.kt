package com.deefrent.rnd.fieldapp.ui.customerComplains

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.deefrent.rnd.fieldapp.bodies.complains.CreateComplainBody
import com.deefrent.rnd.fieldapp.databinding.FragmentCustomerComplainsSearchBinding
import com.deefrent.rnd.fieldapp.models.complainTypes.ComplainType
import com.deefrent.rnd.fieldapp.utils.Constants
import com.deefrent.rnd.fieldapp.viewModels.ComplainsViewModel
import com.deefrent.rnd.fieldapp.viewModels.DataViewModel
import com.deefrent.rnd.fieldapp.viewModels.ExistingAccountViewModel
import com.google.gson.Gson
import dev.ronnie.github.imagepicker.ImagePicker
import es.dmoral.toasty.Toasty
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.properties.Delegates

class CustomerComplainsSearchFragment : Fragment() {
    private var _binding: FragmentCustomerComplainsSearchBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: CustomerComplainsSharedViewModel by activityViewModels()
    private val existingAccountViewModel: ExistingAccountViewModel by activityViewModels()
    private lateinit var dataViewModel: DataViewModel
    private var complainAttachment = ""
    private lateinit var imagePicker: ImagePicker
    private lateinit var complainsViewModel: ComplainsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePicker = ImagePicker(fragment = this)
        arguments?.let {
            isFromAgent360 = it.getBoolean("isFromAgent360")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        complainsViewModel = ViewModelProvider(this).get(ComplainsViewModel::class.java)
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        _binding = FragmentCustomerComplainsSearchBinding.inflate(inflater, container, false)
        val view = binding.root
/*
        binding.tvAttachDocument.setOnClickListener {
            imagePicker.pickFromStorage(object : ImageResult {
                override fun onFailure(reason: String) {
                    Toast.makeText(requireContext(), reason, Toast.LENGTH_LONG).show()
                }

                override fun onSuccess(uri: Uri) {
                    binding.tvAttachDocument.text = getFileName(uri)
                    complainAttachment = encodeToBase64(uri)
                    val path = FileUtil.getPath(uri, requireContext())
                    sharedViewModel.setComplainAttachmentPath(path)
                    val file = path?.let { it1 -> convertPathToFile(it1) }
                    if (file != null) {
                        sharedViewModel.setComplainAttachmentFile(file)
                    }
                }
            })
        }
*/
        binding.btnSubmitMessage.setOnClickListener { v ->
            //callDialog("Submitting...", requireContext(), v)
            if (isValid()) {
                submitComplain(v)
            }
        }
        getComplainTypes()
        observeExistingAccountViewModel()
        return view
    }

    private fun isValid(): Boolean {
        val isValid: Boolean
        if (binding.acComplainType.text.toString().isNullOrEmpty() ||
            binding.etSubject.text.toString().isNullOrEmpty() ||
            binding.etMessage.text.toString().isNullOrEmpty()
        ) {
            isValid = false
            Toasty.error(requireContext(), "Please fill in all the details", Toasty.LENGTH_LONG)
                .show()
        } else if (binding.tvAttachDocument.text.toString() == "Attach Document") {
            isValid = false
            Toasty.error(requireContext(), "Please attach a document", Toasty.LENGTH_LONG)
                .show()
        } else {
            isValid = true
        }
        return isValid
    }

    private fun submitComplain(v: View) {
        sharedViewModel.apply {
            userAccountTypeId.observe(
                viewLifecycleOwner
            ) { userAccountTypeId ->
                UserAccountTypeId = userAccountTypeId
            }
            complainTypeId.observe(
                viewLifecycleOwner
            ) { complainTypeId ->
                ComplainTypeId = complainTypeId
            }
            complainAttachmentFile.observe(
                viewLifecycleOwner
            ) { complainAttachmentFile ->
                ComplainAttachmentFile = complainAttachmentFile
            }
            accountNumber.observe(
                viewLifecycleOwner
            ) { accountNumber ->
                AccountNumber = accountNumber
            }
        }
        Constants.callDialog2("Submitting...", requireContext())
        val createComplainBody = CreateComplainBody(
            AccountNumber, ComplainTypeId, binding.etMessage.text.toString(),
            binding.etSubject.text.toString(), UserAccountTypeId
        )
        val json = Gson().toJson(createComplainBody);
        Log.d(TAG, "submitComplain: $json")
        val complainDetails = RequestBody.create(MultipartBody.FORM, json)
        val complainAttachmentFile = MultipartBody.Part.createFormData(
            "complainFiles",
            ComplainAttachmentFile.name, RequestBody.create(
                "multipart/form-data".toMediaTypeOrNull(),
                ComplainAttachmentFile
            )
        )
        complainsViewModel.createComplain(complainDetails, complainAttachmentFile)
            .observe(viewLifecycleOwner) { createComplainResponse ->
                if (createComplainResponse != null) {
                    when (createComplainResponse.status) {
                        "success" -> {

                            Toasty.success(
                                requireContext(),
                                createComplainResponse.message,
                                Toasty.LENGTH_LONG
                            ).show()
                            Constants.cancelDialog()
                        }
                        "failed" -> {
                            Toasty.error(
                                requireContext(),
                                createComplainResponse.message,
                                Toasty.LENGTH_LONG
                            ).show()
                        }
                        else -> {
                            Constants.cancelDialog()
                            Toasty.error(
                                requireContext(),
                                "An error occurred. Please try again",
                                Toasty.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                }
            }
    }

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

    private fun getComplainTypes() {
        dataViewModel.fetchComplainTypes()
            .observe(viewLifecycleOwner) { fetchComplainTypesResponse ->
                if (fetchComplainTypesResponse != null) {
                    val complainTypes: List<ComplainType> =
                        fetchComplainTypesResponse.complainTypesData.complainTypes
                    populateComplainTypes(complainTypes)
                } else {
                    Toasty.error(
                        requireContext(),
                        "An error occurred. Please try again",
                        Toasty.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun populateComplainTypes(complainTypes: List<ComplainType>) {
        val complainTypesAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, complainTypes)
        binding.acComplainType.setAdapter(complainTypesAdapter)
        binding.acComplainType.keyListener = null
        binding.acComplainType.setOnItemClickListener { parent, _, position, _ ->
            val selected: ComplainType = parent.adapter.getItem(position) as ComplainType
            sharedViewModel.setComplainTypeId(selected.id)
        }
    }

    private fun observeExistingAccountViewModel() {
        lateinit var AccountNumber: String
        existingAccountViewModel.apply {
            userType.observe(viewLifecycleOwner,
                { userType ->
                    SelectedUserType = userType
                })
            accountNumber.observe(viewLifecycleOwner,
                { accountNumber ->
                    AccountNumber = accountNumber
                    when (SelectedUserType) {
                        "Individual" -> {
                            customerDetailsResponse.observe(viewLifecycleOwner,
                                { customerDetailsResponse ->
                                    binding.tvAccountNumber.text =
                                        "ACC: ${customerDetailsResponse.customerDetailsData.customerDetails.accountTypeName} - \n $AccountNumber"
                                    binding.tvAgentNumber.text =
                                        "${customerDetailsResponse.customerDetailsData.customerDetails.firstName} ${customerDetailsResponse.customerDetailsData.customerDetails.lastName}"
                                    binding.tvAgentTelephone.text = "ID: 30441304"
                                })
                        }
                        "Agent" -> {
                            binding.tvFragmentTitle.text = "$SelectedUserType Complains"
                            merchantAgentDetailsResponse.observe(viewLifecycleOwner,
                                { merchantAgentDetailsResponse ->
                                    binding.tvAccountNumber.text =
                                        "ACC: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.businessName} - \n $AccountNumber"
                                    binding.tvAgentNumber.text = "Agent No: 3782"
                                    binding.tvAgentTelephone.text =
                                        "Tel: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.phoneNo}"
                                })
                        }
                        "Merchant" -> {
                            binding.tvFragmentTitle.text = "$SelectedUserType Complains"
                            merchantAgentDetailsResponse.observe(viewLifecycleOwner,
                                { merchantAgentDetailsResponse ->
                                    binding.tvAccountNumber.text =
                                        "ACC: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.businessName} - \n $AccountNumber"
                                    binding.tvAgentNumber.text = "Merchant No: 3782"
                                    binding.tvAgentTelephone.text =
                                        "Tel: ${merchantAgentDetailsResponse.merchantAgentDetailsData.merchantDetails.phoneNo}"
                                })
                        }
                    }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val TAG = CustomerComplainsSearchFragment::class.qualifiedName
        private var UserAccountTypeId by Delegates.notNull<Int>()
        private var ComplainTypeId by Delegates.notNull<Int>()
        private lateinit var ComplainAttachmentPath: String
        private lateinit var ComplainAttachmentFile: File
        private lateinit var AccountNumber: String
        private lateinit var SelectedUserType: String
        private var isFromAgent360 by Delegates.notNull<Boolean>()

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CustomerComplainsSearchFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}