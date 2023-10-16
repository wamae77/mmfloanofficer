package com.deefrent.rnd.fieldapp.view.auth

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.common.network.ResourceNetworkFlow
import com.deefrent.rnd.common.utils.AUTH_IMAGE_FILE_PATH
import com.deefrent.rnd.common.utils.deleteImageFile
import com.deefrent.rnd.common.utils.saveBitmapToFile
import com.deefrent.rnd.common.utils.showOneButtonDialog
import com.deefrent.rnd.common.utils.snackBarCustom
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentAuthByFingerPrintBinding
import com.deefrent.rnd.fieldapp.utils.base_fragment.BaseMoneyMartBindedFragment
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.utils.setToolbarTitle
import com.deefrent.rnd.fieldapp.view.fingerPrint.FingerPrintViewModel
import com.deefrent.rnd.fieldapp.view.fingerPrint.method1.ConfigurationForCaptureFingerprint
import com.deefrent.rnd.fieldapp.view.fingerPrint.method1.SetUpInKotlinActivity
import com.gne.pm.PM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthFingerPrintFragment :
    BaseMoneyMartBindedFragment<FragmentAuthByFingerPrintBinding>(
        FragmentAuthByFingerPrintBinding::inflate
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

    override fun onResume() {
        super.onResume()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle(
            toolBarBinding = binding.toolBar,
            activity = requireActivity(),
            title = "Authorize by Finger Print",
            action = {
                onDestroy()
                findNavController().navigateUp()
            }
        )

        launchFingerPrint()

        handleBackButton()

        binding.btnEnroll.setOnClickListener {
            if (AUTH_IMAGE_FILE_PATH != "") {
                performApiLoginUsersWithFingerPrintRequest()
            } else {
                launchFingerPrint()
                Toast.makeText(requireContext(), "Fingerprint is required", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.btnLaunchReader.setOnClickListener {
            launchFingerPrint()
        }
    }

    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    onDestroy()
                    findNavController().navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

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
            viewModel.loginUsersWithMultipleImages()
                .collect {
                    when (it) {
                        is ResourceNetworkFlow.Error -> {
                            binding.progressbar.mainPBar.makeGone()
                            showOneButtonDialog(
                                title = "ERROR",
                                description = getString(com.deefrent.rnd.common.R.string.oops_we_could_not_process_your_request_at_the_moment_please_try_again),
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
                                setFragmentResult(
                                    "requestKey",
                                    bundleOf(/*AuthResultTag to authResult,*/ "isAuthorized" to "true")
                                )
                                findNavController().navigateUp()
                                deleteImageFile(AUTH_IMAGE_FILE_PATH)
                                lifecycleScope.launch {
                                    viewModel.deleteByPhoneNumber(
                                        commonSharedPreferences.getStringData(
                                            CommonSharedPreferences.CU_FINGER_PRINT_ID
                                        )
                                    )
                                }
                            } else {
                                showOneButtonDialog(
                                    title = "Failed",
                                    description = "Finger print verification Failed",
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

    override fun onDestroy() {
        try {
            PM.powerOff()
            //requireActivity().unregisterReceiver(mUsbReceiver)
            snackBarCustom("Closed scanner")
        } catch (e: Exception) {
            // already unregistered
        }
        //power off
        super.onDestroy()
    }
}