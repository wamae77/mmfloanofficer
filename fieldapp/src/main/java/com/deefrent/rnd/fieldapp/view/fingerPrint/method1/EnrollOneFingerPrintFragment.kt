package com.deefrent.rnd.fieldapp.view.fingerPrint.method1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.common.data.fingerprint.FingerPrintData
import com.deefrent.rnd.common.data.request.UpdateFingerprintRegIdRequets
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.common.network.ResourceNetworkFlow
import com.deefrent.rnd.common.utils.CUSTOMER_PHONE_NUMBER_AT_FP_TAKING
import com.deefrent.rnd.common.utils.AUTH_IMAGE_FILE_PATH
import com.deefrent.rnd.common.utils.saveBitmapToFile
import com.deefrent.rnd.common.utils.showOneButtonDialog
import com.deefrent.rnd.common.utils.snackBarCustom
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentEnrollFingerPrintWithOneBinding
import com.deefrent.rnd.fieldapp.network.models.LoanLookupData
import com.deefrent.rnd.fieldapp.utils.base_fragment.BaseMoneyMartBindedFragment
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.utils.setToolbarTitle
import com.deefrent.rnd.fieldapp.view.fingerPrint.FingerPrintViewModel
import com.gne.pm.PM
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class EnrollOneFingerPrintFragment :
    BaseMoneyMartBindedFragment<FragmentEnrollFingerPrintWithOneBinding>(
        FragmentEnrollFingerPrintWithOneBinding::inflate
    ) {
    @Inject
    lateinit var viewModel: FingerPrintViewModel

    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences

    private lateinit var captureFingerprint: ConfigurationForCaptureFingerprint

    private var disposableFingerPrint: Disposable? = null

    private lateinit var intentLauncher: ActivityResultLauncher<Intent>

    private lateinit var loanLookupData: LoanLookupData
    private var customerFingerImages: HashSet<MultipartBody.Part> = HashSet()
    private var fingerDataList: HashSet<FingerPrintData> = HashSet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AUTH_IMAGE_FILE_PATH = ""
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
            title = "Enroll by Finger Print",
            action = {
                findNavController().navigateUp()
            }
        )
        launchFingerPrint()
        loanLookupData = Gson().fromJson(
            commonSharedPreferences.getStringData(CommonSharedPreferences.LOANLOOKUPDATA),
            LoanLookupData::class.java
        )
        binding.btnEnroll.setOnClickListener {
            if (AUTH_IMAGE_FILE_PATH != "") {
                performApiRequestEnrollWithMultipleImages()
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

    private fun launchFingerPrint() {
        intentLauncher.launch(Intent(requireContext(), SetUpInKotlinActivity::class.java))
    }

    private fun subscribeFingerPrintData() {
        disposableFingerPrint = captureFingerprint.fingerPrintAvailable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { fingerPrintBitmap ->
                if (fingerPrintBitmap != null) {
                    lifecycleScope.launch {
                        setImagerViews()
                        viewModel.saveFingerPrintDataRoom(
                            FingerPrintData(
                                phoneNumber = loanLookupData.idNumber,
                                fingerImage = saveBitmapToFile(
                                    bitmap = fingerPrintBitmap!!,
                                    context = requireContext(),
                                    fileName = "${CUSTOMER_PHONE_NUMBER_AT_FP_TAKING}_${(100..10000).random()}"
                                ),
                                handType = "1",
                                fingerPosition = "1",
                                description = "Customer"
                            )
                        )
                    }
                }
                binding.capturedFingerprintImage.setImageBitmap(fingerPrintBitmap)
                binding.imgCheck.visibility = View.VISIBLE
                binding.tvFingeprintCapturedSuccess.visibility = View.VISIBLE
                binding.tvFingeprintCapturedSuccess.text = "Fingerprint Captured Successfully"
            }
    }


    override fun onPause() {
        super.onPause()
        disposableFingerPrint?.dispose()
    }


    private fun performApiRequestEnrollWithMultipleImages() {
        viewLifecycleOwner.lifecycleScope.launch {
            snackBarCustom("${customerFingerImages.size}")
            Timber.d(
                "${loanLookupData.idNumber} ${customerFingerImages.toMutableList()}"
            )
            viewModel.enrollWithMultipleImages(
                idNumber = loanLookupData.idNumber.toString(),
                finger_index = "1",
                hand_type = "1",
                mutableListOfFiles = customerFingerImages.toMutableList()
            ).collect {
                when (it) {
                    is ResourceNetworkFlow.Error -> {
                        binding.progressbar.mainPBar.makeGone()
                        showOneButtonDialog(
                            title = "ERROR",
                            description = "${it.error}",
                            image = com.deefrent.rnd.common.R.drawable.ic_baseline_error_outline_24
                        )
                    }

                    is ResourceNetworkFlow.Loading -> {
                        binding.progressbar.mainPBar.makeVisible()
                    }

                    is ResourceNetworkFlow.Success -> {
                        binding.progressbar.mainPBar.makeGone()
                        if (it.data?.status == 200) {
                            performApiRequestUpdateCustomerFingerPrint(it?.data?.data?.userUid.toString())
                        } else {
                            showOneButtonDialog(
                                title = "Failed",
                                description = it.data!!.message.toString(),
                                image = com.deefrent.rnd.common.R.drawable.ic_baseline_error_outline_24
                            ) {
                                lifecycleScope.launch {
                                    viewModel.deleteByPhoneNumber(loanLookupData.idNumber)
                                }

                            }
                        }
                    }

                    else -> {
                        Log.e("", "else RESPONSE:")
                    }
                }
            }
        }
    }

    private fun performApiRequestUpdateCustomerFingerPrint(fingerPrintRegId: String) {
        lifecycleScope.launch {
            viewModel.updateFingerprintRegId(
                UpdateFingerprintRegIdRequets(
                    clientId = loanLookupData.clientId,
                    fingerPrintRegId = fingerPrintRegId
                )
            ).collect {
                when (it) {
                    is ResourceNetworkFlow.Error -> {
                        binding.progressbar.mainPBar.makeGone()
                        showOneButtonDialog(
                            title = "ERROR",
                            description = "${it.error}",
                            image = com.deefrent.rnd.common.R.drawable.ic_baseline_error_outline_24
                        )
                    }

                    is ResourceNetworkFlow.Loading -> {
                        binding.progressbar.mainPBar.makeVisible()
                    }

                    is ResourceNetworkFlow.Success -> {
                        binding.progressbar.mainPBar.makeGone()
                        val response = it.data!!
                        if (response.status == 1) {
                            showOneButtonDialog(
                                title = "Success",
                                description = it.data!!.message.toString(),
                                image = com.deefrent.rnd.common.R.drawable.ic_success_check
                            ) {
                                findNavController().popBackStack(R.id.dashboardFragment, false)
                            }
                        } else {
                            showOneButtonDialog(
                                title = "Failed",
                                description = it.data!!.message.toString(),
                                image = com.deefrent.rnd.common.R.drawable.ic_baseline_error_outline_24
                            ) {
                                lifecycleScope.launch {
                                    viewModel.deleteByPhoneNumber(loanLookupData.idNumber)
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    private fun setImagerViews() {
        fingerDataList.clear()
        customerFingerImages.clear()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getListFingerPrintDataByPhoneNumber(loanLookupData.idNumber)
                .collect {
                    fingerDataList.addAll(it)
                    val filterData = it.filter { it.phoneNumber == loanLookupData.idNumber }
                    Timber.d("FILTER_DATA ${filterData.map { it.phoneNumber }}")
                    filterData.map { dataModel ->
                        getFingerPrintImages(dataModel.fingerImage.toString())
                        // Process each user object
                        Timber.d("FINGER POSITION: ${dataModel.fingerPosition}, HAND TYPE: ${dataModel.handType}")

                    }
                }
        }

    }

    private fun getFingerPrintImages(imageFile: String) {
        AUTH_IMAGE_FILE_PATH = imageFile
        val mediaType = "image/png".toMediaTypeOrNull()
        val fileAsset = File("${imageFile}")
        val requestFileAsset = fileAsset.asRequestBody(mediaType)
        val assetPhotoUrl =
            MultipartBody.Part.createFormData(
                "images",
                fileAsset.name,
                requestFileAsset
            )
        customerFingerImages.add(assetPhotoUrl)

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