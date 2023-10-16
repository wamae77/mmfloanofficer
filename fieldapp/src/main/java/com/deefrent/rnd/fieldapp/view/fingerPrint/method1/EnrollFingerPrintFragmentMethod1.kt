package com.deefrent.rnd.fieldapp.view.fingerPrint.method1

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.common.utils.AUTH_IMAGE_FILE_PATH
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentEnrollFingerPrintBinding
import com.deefrent.rnd.fieldapp.utils.base_fragment.BaseMoneyMartBindedFragment
import com.deefrent.rnd.fieldapp.utils.setToolbarTitle
import com.deefrent.rnd.fieldapp.view.fingerPrint.FingerPrintViewModel
import com.deefrent.rnd.fieldapp.view.fingerPrint.SharedViewModelToStoreImageData
import com.gne.pm.PM
import io.reactivex.disposables.Disposable
import okhttp3.MultipartBody
import javax.inject.Inject

class EnrollFingerPrintFragmentMethod1 :
    BaseMoneyMartBindedFragment<FragmentEnrollFingerPrintBinding>(
        FragmentEnrollFingerPrintBinding::inflate
    ) {
    @Inject
    lateinit var viewModel: FingerPrintViewModel
    private val sharedViewModel: SharedViewModelToStoreImageData by activityViewModels()

    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences
    private var customerFingerImages: MutableList<MultipartBody.Part> = ArrayList()

    /**
     *
     */

    // private lateinit var captureFingerprint: CaptureFingerprint
    private var disposableFingerPrint: Disposable? = null
    private lateinit var intentLauncher: ActivityResultLauncher<Intent>
    private var typeOfFinger: Int = 0

    /**
     *
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        setToolbarTitle(
            toolBarBinding = binding.toolBar,
            activity = requireActivity(),
            title = "Enroll", //getString(com.ekenya.rnd.common.R.string.funeral_insurance),
            action = {
                findNavController().navigateUp()
            }
        )

        binding.tvServiceDesc.setOnClickListener {
            AUTH_IMAGE_FILE_PATH = ""
            findNavController().navigate(R.id.loginWithFingerPrintFragment)
        }


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    /**
     * OTHER CONFIGURATIONS
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            data?.extras?.run {
                getString("device_name")?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    /*captureFingerprint =
                        CaptureFingerprint(
                            requireContext(),
                            it
                        )
                    captureFingerprint.initDpSdk()
                    subscribeFingerPrintData()*/
                }
            }
        }
    }





    override fun onPause() {
        super.onPause()
        disposableFingerPrint?.dispose()
        PM.powerOff()
    }

}


