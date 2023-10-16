package com.deefrent.rnd.fieldapp.ui.assetManagement

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
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.deefrent.rnd.fieldapp.bodies.assetManagement.SubmitAssetBody
import com.deefrent.rnd.fieldapp.databinding.FragmentSubmitAssetBinding
import com.deefrent.rnd.fieldapp.models.assets.Asset
import com.deefrent.rnd.fieldapp.utils.Constants
import com.deefrent.rnd.fieldapp.viewModels.AssetsViewModel
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

class SubmitAssetFragment : Fragment() {
    private var _binding: FragmentSubmitAssetBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: AssetManagementSharedViewModel by activityViewModels()
    private val existingAccountViewModel: ExistingAccountViewModel by activityViewModels()
    private lateinit var dataViewModel: DataViewModel
    lateinit var imagePicker: ImagePicker
    private lateinit var assetsViewModel: AssetsViewModel

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
        assetsViewModel = ViewModelProvider(this).get(AssetsViewModel::class.java)
        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        _binding = FragmentSubmitAssetBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnSubmitAsset.setOnClickListener { v ->
            //callDialog("Submitting...", requireContext(), v)
            if (isValid()) {
                submitAsset(v)
            }
        }
/*
        binding.tvAttachPhoto.setOnClickListener {
            imagePicker.pickFromStorage(object : ImageResult {
                override fun onFailure(reason: String) {
                    Toast.makeText(requireContext(), reason, Toast.LENGTH_LONG).show()
                }

                override fun onSuccess(uri: Uri) {
                    binding.ivAssetPhoto.visibility = View.VISIBLE
                    AssetPhoto1 = encodeToBase64(uri)
                    binding.ivAssetPhoto.setImageURI(uri)
                    val path = FileUtil.getPath(uri, requireContext())
                    sharedViewModel.setAssetPhoto1Path(path)
                    val file = path?.let { it1 -> convertPathToFile(it1) }
                    if (file != null) {
                        sharedViewModel.setAssetPhoto1File(file)
                    }
                }
            })
        }
*/
        getAssetTypes()
        observeExistingAccountViewModel()
        return view
    }

    private fun observeExistingAccountViewModel() {
        lateinit var AccountNumber: String
        lateinit var SelectedUserType: String
        existingAccountViewModel.apply {
            userType.observe(viewLifecycleOwner,
                { userType ->
                    SelectedUserType = userType
                })
            accountNumber.observe(viewLifecycleOwner,
                { accountNumber ->
                    AccountNumber = accountNumber
                    when (SelectedUserType) {
                        "Agent" -> {
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

    private fun getAssetTypes() {
        dataViewModel.fetchAssets()
            .observe(viewLifecycleOwner) { fetchAssetTypesResponse ->
                if (fetchAssetTypesResponse != null) {
                    val assetTypes: List<Asset> =
                        fetchAssetTypesResponse.assetsData.assets
                    populateAssetTypes(assetTypes)
                } else {
                    Toasty.error(
                        requireContext(),
                        "An error occurred. Please try again",
                        Toasty.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun populateAssetTypes(assetTypes: List<Asset>) {
        val assetTypesAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, assetTypes)
        binding.acAsset.setAdapter(assetTypesAdapter)
        binding.acAsset.keyListener = null
        binding.acAsset.setOnItemClickListener { parent, _, position, _ ->
            val selected: Asset = parent.adapter.getItem(position) as Asset
            sharedViewModel.setAssetId(selected.id)
        }
    }

    private fun isValid(): Boolean {
        val isValid: Boolean
        if (binding.acAsset.text.toString().isNullOrEmpty() ||
            binding.etSerial.text.toString().isNullOrEmpty()
        ) {
            isValid = false
            Toasty.error(requireContext(), "Please fill in all the details", Toasty.LENGTH_LONG)
                .show()
        } else if (binding.ivAssetPhoto.visibility == View.GONE) {
            isValid = false
            Toasty.error(requireContext(), "Please attach a photo of the asset", Toasty.LENGTH_LONG)
                .show()
        } else {
            isValid = true
        }
        return isValid
    }

    private fun submitAsset(v: View) {
        sharedViewModel.apply {
            userAccountTypeId.observe(
                viewLifecycleOwner
            ) { userAccountTypeId ->
                UserAccountTypeId = userAccountTypeId
            }
            assetId.observe(
                viewLifecycleOwner
            ) { assetId ->
                AssetId = assetId
            }
            accountNumber.observe(
                viewLifecycleOwner
            ) { accountNumber ->
                AccountNumber = accountNumber
            }
            assetPhoto1File.observe(
                viewLifecycleOwner
            ) { assetPhoto1File ->
                AssetPhoto1File = assetPhoto1File
            }
        }
        Constants.callDialog2("Submitting...", requireContext())
        val submitAssetBody = SubmitAssetBody(
            AccountNumber, AssetId,
            UserAccountTypeId
        )
        val json = Gson().toJson(submitAssetBody);
        Log.d("SubmitAsset", "submitAsset: $json")
        val assetDetails = RequestBody.create(MultipartBody.FORM, json)
        val assetFiles = MultipartBody.Part.createFormData(
            "assetFiles",
            AssetPhoto1File.name, RequestBody.create(
                "multipart/form-data".toMediaTypeOrNull(),
                AssetPhoto1File
            )
        )
        assetsViewModel.submitAsset(assetDetails, assetFiles)
            .observe(viewLifecycleOwner) { submitAssetResponse ->
                if (submitAssetResponse != null) {
                    when (submitAssetResponse.status) {
                        "success" -> {
                            Toasty.success(
                                requireContext(),
                                submitAssetResponse.message,
                                Toasty.LENGTH_LONG
                            ).show()
                            Constants.cancelDialog()
                        }
                        "failed" -> {
                            Constants.cancelDialog()
                            Toasty.error(
                                requireContext(),
                                submitAssetResponse.message,
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

    fun encodeToBase64(imageUri: Uri): String {
        val input = requireActivity().contentResolver.openInputStream(imageUri)

        // Encode image to base64 string
        val baos = ByteArrayOutputStream()
        BitmapFactory.decodeStream(input, null, null)
            ?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    companion object {
        private lateinit var AssetPhoto1: String
        private lateinit var AssetPhoto1File: File
        private lateinit var AccountNumber: String
        private var AssetId by Delegates.notNull<Int>()
        private var UserAccountTypeId by Delegates.notNull<Int>()

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SubmitAssetFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}