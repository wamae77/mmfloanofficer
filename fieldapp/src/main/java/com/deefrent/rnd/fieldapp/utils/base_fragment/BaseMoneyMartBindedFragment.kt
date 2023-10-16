package com.deefrent.rnd.fieldapp.utils.base_fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.viewbinding.ViewBinding
import com.deefrent.rnd.common.abstractions.BaseDaggerFragment
import com.deefrent.rnd.common.dialogs.base.adapter_detail.model.DialogDetailCommon
import com.deefrent.rnd.common.dialogs.dialog_confirm.ConfirmDialogCallBacks
import com.deefrent.rnd.common.dialogs.dialog_confirm.ConfirmDialogCommon
import com.deefrent.rnd.fieldapp.view.fingerPrint.method1.ConfigurationForCaptureFingerprint
import io.reactivex.disposables.Disposable

abstract class BaseMoneyMartBindedFragment<VB : ViewBinding>(
    private val bindingInflater: (inflater: LayoutInflater) -> VB
) : BaseDaggerFragment() {

    private var _binding: VB? = null
    val binding get() = _binding as VB

    private lateinit var intentLauncher: ActivityResultLauncher<Intent>
    private var disposableFingerPrint: Disposable? = null
    private lateinit var captureFingerprint: ConfigurationForCaptureFingerprint
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater)
        if (_binding == null) throw IllegalArgumentException("Binding Not Found")

        return binding.root
    }


    /**
     *CUSTOM DIALOGS
     */
    fun showConfirmationDialog(
        title: String?,
        subtitle: String?,
        dialogDetailCommons: List<DialogDetailCommon>,
        confirmDialogCallBacks: ConfirmDialogCallBacks?
    ) {
        val confirmDialog = ConfirmDialogCommon(requireContext())
        confirmDialog.setDialogTitle(title!!)
        confirmDialog.setDialogSubtitle(subtitle!!)
        confirmDialog.setUpRecyclerAdapter(dialogDetailCommons)
        confirmDialog.setCallbacks(confirmDialogCallBacks!!)
        confirmDialog.create().show()
    }

    fun showConfirmationDialog(
        title: String?,
        subtitle: String?,
        details: HashMap<String, String>,
        confirmDialogCallBacks: ConfirmDialogCallBacks?
    ) {
        val dialogDetailCommons: MutableList<DialogDetailCommon> = ArrayList()
        for (key in details.keys) {
            dialogDetailCommons.add(DialogDetailCommon(key!!, details[key]!!))
        }
        val confirmDialog = ConfirmDialogCommon(requireContext())
        confirmDialog.setDialogTitle(title!!)
        confirmDialog.setDialogSubtitle(subtitle!!)
        confirmDialog.setUpRecyclerAdapter(dialogDetailCommons)
        confirmDialog.setCallbacks(confirmDialogCallBacks!!)
        confirmDialog.create().show()
    }

    /*
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

        fun launchFingerPrintRequest() {
            intentLauncher.launch(Intent(requireContext(), SetUpInKotlinActivity::class.java))
        }

        private fun subscribeFingerPrintData() {
            disposableFingerPrint = captureFingerprint.fingerPrintAvailable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { fingerPrintBitmap ->
                }
        }
    */


}
