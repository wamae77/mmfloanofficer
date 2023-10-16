package com.deefrent.rnd.fieldapp.view.fingerPrint.method1

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.common.network.ResourceNetworkFlow
import com.deefrent.rnd.common.utils.AUTH_IMAGE_FILE_PATH
import com.deefrent.rnd.common.utils.deleteImageFile
import com.deefrent.rnd.common.utils.saveBitmapToFile
import com.deefrent.rnd.common.utils.showOneButtonDialog
import com.deefrent.rnd.fieldapp.databinding.FragmentCaptureFingerPrintBinding
import com.deefrent.rnd.fieldapp.utils.base_fragment.BaseMoneyMartBindedFragment
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.utils.setToolbarTitle
import com.deefrent.rnd.fieldapp.view.fingerPrint.FingerPrintViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginWithFingerPrintFragment :
    BaseMoneyMartBindedFragment<FragmentCaptureFingerPrintBinding>(
        FragmentCaptureFingerPrintBinding::inflate
    ) {

    @Inject
    lateinit var viewModel: FingerPrintViewModel

    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences

    private lateinit var captureFingerprint: ConfigurationForCaptureFingerprint

    private var disposableFingerPrint: Disposable? = null

    private lateinit var intentLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpOnCreated()
    }

    private fun setUpOnCreated() {
        intentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.extras?.run {
                        getString("device_name")?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                            captureFingerprint =
                                ConfigurationForCaptureFingerprint(
                                    requireContext(),
                                    it
                                )
                            captureFingerprint.initDpSdk()
                            subscribeFingerPrintData()
                        }
                    }
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle(
            toolBarBinding = binding.toolBar,
            activity = requireActivity(),
            title = "Login by Finger Print",
            action = {
                findNavController().navigateUp()
            }
        )
        launchFingerPrint()

        binding.btnLaunchReader.setOnClickListener {
            launchFingerPrint()
        }
        binding.btnEnroll.setOnClickListener {
            if (AUTH_IMAGE_FILE_PATH != "") {
                performApiLoginUsersWithFingerPrintRequest()
            } else {
                Toast.makeText(requireContext(), "Fingerprint is required", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun launchFingerPrint() {
        intentLauncher.launch(Intent(requireContext(), SetUpInKotlinActivity::class.java))
    }

    private fun subscribeFingerPrintData() {
        disposableFingerPrint = captureFingerprint.fingerPrintAvailable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { fingerPrintBitmap ->
                binding.capturedFingerprintImage.setImageBitmap(fingerPrintBitmap)
                saveToDevice(fingerPrintBitmap)
                binding.imgCheck.visibility = View.VISIBLE
                binding.tvFingeprintCapturedSuccess.visibility = View.VISIBLE
                binding.tvFingeprintCapturedSuccess.text = "Fingerprint Captured Successfully"
            }
    }

    private fun performApiLoginUsersWithFingerPrintRequest() {
        viewLifecycleOwner.lifecycleScope.launch {
            val userUid =
                commonSharedPreferences.getStringData("TEST_FINGER_PRINT")
            viewModel.loginUsersTestWithMultipleImages(userUid)
                .collect {
                    when (it) {
                        is ResourceNetworkFlow.Error -> {
                            binding.progressbar.mainPBar.makeGone()
                            showOneButtonDialog(
                                title = "ERROR",
                                description = it.error.toString(),
                                image = com.deefrent.rnd.common.R.drawable.ic_baseline_error_outline_24
                            )
                        }

                        is ResourceNetworkFlow.Loading -> {
                            binding.progressbar.mainPBar.makeVisible()
                        }

                        is ResourceNetworkFlow.Success -> {
                            binding.progressbar.mainPBar.makeGone()
                            Log.e("RESPONSE", "${it.data}")
                            if (it.data?.status == 200) {
                                deleteImageFile(AUTH_IMAGE_FILE_PATH)
                                showOneButtonDialog(
                                    title = "Success",
                                    description = "Login Success",
                                    image = com.deefrent.rnd.common.R.drawable.ic_success_check
                                ) {
                                    /*startActivity(
                                        Intent(
                                            requireActivity(),
                                            MainActivity::class.java
                                        )
                                    )
                                    requireActivity().finish()*/
                                }
                            } else {
                                showOneButtonDialog(
                                    title = "Failed",
                                    description = "Login Failed",
                                    image = com.deefrent.rnd.common.R.drawable.ic_baseline_error_outline_24
                                )
                            }
                        }

                        else -> {
                            Log.e("", "else RESPONSE:")
                        }
                    }
                }
        }
    }

    private fun saveToDevice(fingerPrintBitmap: Bitmap?) {
        AUTH_IMAGE_FILE_PATH = saveBitmapToFile(
            bitmap = fingerPrintBitmap!!,
            context = requireContext(),
            fileName = "AUTH_FINGER_${(100..10000).random()}"
        ).toString()
    }


    override fun onPause() {
        super.onPause()
        disposableFingerPrint?.dispose()
    }
}