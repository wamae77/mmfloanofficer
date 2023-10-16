package com.deefrent.rnd.fieldapp.ui.onboardAccount

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.deefrent.rnd.fieldapp.databinding.FragmentAgentPersonalImagesBinding
import com.deefrent.rnd.fieldapp.view.homepage.LoginSessionSharedViewModel
import com.deefrent.rnd.fieldapp.viewModels.OnboardAccountViewModel
import com.deefrent.rnd.fieldapp.viewModels.RoomDBViewModel
import dev.ronnie.github.imagepicker.ImagePicker
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File

class AgentPersonalImagesFragment : Fragment() {
    private lateinit var roomDBViewModel: RoomDBViewModel
    private val onboardMerchantSharedViewModel: OnboardMerchantSharedViewModel by activityViewModels()
    private lateinit var onboardAccountViewModel: OnboardAccountViewModel
    private var _binding: FragmentAgentPersonalImagesBinding? = null
    private val binding get() = _binding!!
    lateinit var imagePicker: ImagePicker
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
        onboardAccountViewModel = ViewModelProvider(this).get(OnboardAccountViewModel::class.java)
        _binding = FragmentAgentPersonalImagesBinding.inflate(inflater, container, false)
        val view = binding.root
        observeLoginSharedViewModel()
        observeSharedViewModel()
        binding.etPassportPhoto.keyListener = null
       /* binding.etPassportPhoto.setOnClickListener {
            imagePicker.pickFromStorage(object : ImageResult {
                override fun onFailure(reason: String) {
                    Toast.makeText(requireContext(), reason, Toast.LENGTH_LONG).show()
                }

                override fun onSuccess(uri: Uri) {
                    binding.etPassportPhoto.setText(getFileName(uri))
                    val path = FileUtil.getPath(uri, requireContext())
                    val file = path?.let { it1 -> convertPathToFile(it1) }
                    onboardMerchantSharedViewModel.setCustomerPhotoPath(path)
                    if (file != null) {
                        onboardMerchantSharedViewModel.setCustomerPhotoFile(file)
                        onboardMerchantSharedViewModel.setCustomerPhotoUri(uri)
                    }
                }
            })
        }
        binding.etAttachSignaturePhoto.keyListener = null
        binding.etAttachSignaturePhoto.setOnClickListener {
            imagePicker.pickFromStorage(object : ImageResult {
                override fun onFailure(reason: String) {
                    Toast.makeText(requireContext(), reason, Toast.LENGTH_LONG).show()
                }

                override fun onSuccess(uri: Uri) {
                    binding.etAttachSignaturePhoto.setText(getFileName(uri))
                    val path = FileUtil.getPath(uri, requireContext())
                    val file = path?.let { it1 -> convertPathToFile(it1) }
                    onboardMerchantSharedViewModel.setSignatureDocPath(path)
                    if (file != null) {
                        onboardMerchantSharedViewModel.setSignatureDocFile(file)
                        onboardMerchantSharedViewModel.setSignatureDocUri(uri)
                    }
                }
            })
        }
        binding.etAttachGoodConduct.keyListener = null
        binding.etAttachGoodConduct.setOnClickListener {
            imagePicker.pickFromStorage(object : ImageResult {
                override fun onFailure(reason: String) {
                    Toast.makeText(requireContext(), reason, Toast.LENGTH_LONG).show()
                }

                override fun onSuccess(uri: Uri) {
                    binding.etAttachGoodConduct.setText(getFileName(uri))
                    val path = FileUtil.getPath(uri, requireContext())
                    val file = path?.let { it1 -> convertPathToFile(it1) }
                    onboardMerchantSharedViewModel.setGoodConductPath(path)
                    if (file != null) {
                        onboardMerchantSharedViewModel.setGoodConductFile(file)
                        onboardMerchantSharedViewModel.setGoodConductUri(uri)
                    }
                }
            })
        }*/
        binding.etAttachFieldApplicationForm.keyListener = null
/*
        binding.etAttachFieldApplicationForm.setOnClickListener {
            imagePicker.pickFromStorage(object : ImageResult {
                override fun onFailure(reason: String) {
                    Toast.makeText(requireContext(), reason, Toast.LENGTH_LONG).show()
                }

                override fun onSuccess(uri: Uri) {
                    binding.etAttachFieldApplicationForm.setText(getFileName(uri))
                    val path = FileUtil.getPath(uri, requireContext())
                    val file = path?.let { it1 -> convertPathToFile(it1) }
                    onboardMerchantSharedViewModel.setFieldApplicationFormPath(path)
                    if (file != null) {
                        onboardMerchantSharedViewModel.setFieldApplicationFormFile(file)
                        onboardMerchantSharedViewModel.setFieldApplicationFormUri(uri)
                    }
                }
            })
        }
*/
        return view
    }

    private fun observeLoginSharedViewModel() {
        loginSessionSharedViewModel.apply {
            isFromIncompleteDialog.observe(viewLifecycleOwner,
                { isFromIncompleteDialog ->
                    if (isFromIncompleteDialog) {
                        IsFromIncompleteDialog = true
                        onboardMerchantSharedViewModel.apply {
                            customerPhotoPath.observe(viewLifecycleOwner,
                                { customerPhotoPath ->
                                    binding.etPassportPhoto.setText(
                                        getFileNameFromPath(
                                            customerPhotoPath
                                        )
                                    )
                                })
                            signatureDocPath.observe(viewLifecycleOwner,
                                { signatureDocPath ->
                                    binding.etAttachSignaturePhoto.setText(
                                        getFileNameFromPath(
                                            signatureDocPath
                                        )
                                    )
                                })
                            goodConductPath.observe(viewLifecycleOwner,
                                { goodConductPath ->
                                    binding.etAttachGoodConduct.setText(
                                        getFileNameFromPath(
                                            goodConductPath
                                        )
                                    )
                                })
                            fieldApplicationFormPath.observe(viewLifecycleOwner,
                                { fieldApplicationFormPath ->
                                    binding.etAttachFieldApplicationForm.setText(
                                        getFileNameFromPath(
                                            fieldApplicationFormPath
                                        )
                                    )
                                })
                        }
                    }
                })
        }
    }

    private fun isValid(): Boolean {
        val isValid: Boolean
        if (binding.etPassportPhoto.text.toString().isNullOrEmpty() ||
            binding.etAttachSignaturePhoto.text.toString().isNullOrEmpty() ||
            binding.etAttachGoodConduct.text.toString().isNullOrEmpty() ||
            binding.etAttachFieldApplicationForm.text.toString().isNullOrEmpty()
        ) {
            isValid = false
            Toasty.error(requireContext(), "Please fill in all the details", Toasty.LENGTH_LONG)
                .show()
        } else {
            isValid = true
        }
        return isValid
    }

    private fun saveDataLocally() {
        var RoomDBId = 0
        var SignatureDocPath = ""
        var CustomerPhotoPath = ""
        var GoodConductPath = ""
        var FieldFormPath = ""
        var lastStep1 = ""
        val lastStep2 = this::class.java.simpleName
        onboardMerchantSharedViewModel.apply {
            roomDBId.observe(viewLifecycleOwner,
                { roomDBId ->
                    RoomDBId = roomDBId
                })
            customerPhotoPath.observe(viewLifecycleOwner,
                { customerPhotoPath ->
                    CustomerPhotoPath = customerPhotoPath
                })
            signatureDocPath.observe(viewLifecycleOwner,
                { signatureDocPath ->
                    SignatureDocPath = signatureDocPath
                })
            goodConductPath.observe(viewLifecycleOwner,
                { goodConductPath ->
                    GoodConductPath = goodConductPath
                })
            fieldApplicationFormPath.observe(viewLifecycleOwner,
                { fieldApplicationFormPath ->
                    FieldFormPath = fieldApplicationFormPath
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
        compositeDisposable.add(roomDBViewModel.updateMerchantPersonalImages(
            CustomerPhotoPath,
            SignatureDocPath,
            GoodConductPath,
            FieldFormPath, lastStep1,
            RoomDBId
        )
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                compositeDisposable.dispose()
            })
    }

    private fun convertPathToFile(imagePath: String): File {
        return File(imagePath)
    }

    private fun getFileNameFromPath(path: String): String {
        val file = File(path)
        return file.name
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

    private fun observeSharedViewModel() {
        onboardMerchantSharedViewModel.apply {
            userType.observe(viewLifecycleOwner,
                { userType ->
                    binding.tvFragmentTitle.text = "$userType's Personal Images"
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
            AgentPersonalImagesFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}