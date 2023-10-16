package com.deefrent.rnd.fieldapp.view.fingerPrint.method2

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.digitalpersona.uareu.Reader
import com.digitalpersona.uareu.ReaderCollection
import com.digitalpersona.uareu.UareUException
import com.digitalpersona.uareu.dpfpddusbhost.DPFPDDUsbException
import com.digitalpersona.uareu.dpfpddusbhost.DPFPDDUsbHost
import com.deefrent.rnd.common.data.fingerprint.FingerPrintData
import com.deefrent.rnd.common.data.request.UpdateFingerprintRegIdRequets
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.common.network.ResourceNetworkFlow
import com.deefrent.rnd.common.utils.CUSTOMER_PHONE_NUMBER_AT_FP_TAKING
import com.deefrent.rnd.common.utils.GENERAL_ACTIVITY_RESULT
import com.deefrent.rnd.common.utils.MY_PERMISSIONS
import com.deefrent.rnd.common.utils.NUMBER_OF_FINGER_BEING_TAKEN
import com.deefrent.rnd.common.utils.REQUEST_CODE
import com.deefrent.rnd.common.utils.TYPE_OF_FINGER
import com.deefrent.rnd.common.utils.TYPE_OF_HAND
import com.deefrent.rnd.common.utils.deleteImageFileWithFileName
import com.deefrent.rnd.common.utils.getBitmapFromFile
import com.deefrent.rnd.common.utils.showOneButtonDialog
import com.deefrent.rnd.common.utils.showTwoButtonDialog
import com.deefrent.rnd.common.utils.snackBarCustom
import com.deefrent.rnd.common.utils.visibilityView
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentUpdateCustomerFingerPrintBinding
import com.deefrent.rnd.fieldapp.network.models.LoanLookupData
import com.deefrent.rnd.fieldapp.utils.base_fragment.BaseMoneyMartBindedFragment
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.utils.setToolbarTitle
import com.deefrent.rnd.fieldapp.view.fingerPrint.FingerPrintViewModel
import com.deefrent.rnd.fieldapp.view.fingerPrint.SharedViewModelToStoreImageData
import com.gne.pm.PM
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject


class UpdateCustomerFingerPrintFragment :
    BaseMoneyMartBindedFragment<FragmentUpdateCustomerFingerPrintBinding>(
        FragmentUpdateCustomerFingerPrintBinding::inflate
    ) {
    @Inject
    lateinit var viewModel: FingerPrintViewModel
    private val sharedViewModel: SharedViewModelToStoreImageData by activityViewModels()

    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences
    private var customerFingerImages: HashSet<MultipartBody.Part> = HashSet()
    private var fingerDataList: HashSet<FingerPrintData> = HashSet()
    private lateinit var loanLookupData: LoanLookupData

    /**
     *
     */
    private var m_deviceName = ""
    private lateinit var m_reader: Reader//? = null
    private lateinit var readers: ReaderCollection
    private val ACTION_USB_PERMISSION = "USB_PERMISSION"
    private var mPermissionIntent: PendingIntent? = null

    /**
     *
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customerFingerImages.clear()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val checkCallPhonePermission = ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                // 没有写文件的权限，去申请读写文件的权限，系统会弹出权限许可对话框
                //Without the permission to Write, to apply for the permission to Read and Write, the system will pop up the permission dialog
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    MY_PERMISSIONS,
                    REQUEST_CODE
                )
            } else {
                initPowerOn()
            }
        } else {
            initPowerOn()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle(
            toolBarBinding = binding.toolBar,
            activity = requireActivity(),
            title = "Enroll", //getString(com.ekenya.rnd.common.R.string.funeral_insurance),
            action = {
                findNavController().navigateUp()
                onDestroy()
            }
        )
        loanLookupData = Gson().fromJson(
            commonSharedPreferences.getStringData(CommonSharedPreferences.LOANLOOKUPDATA),
            LoanLookupData::class.java
        )

        Timber.d("${loanLookupData}")

        setUpViewOnclickListener()
        setImagerViews()
        handleBackButton()

        binding.tvDesGuide.setOnClickListener {
            lifecycleScope.launch {
                viewModel.deleteByPhoneNumber(loanLookupData.idNumber.toString())
            }
        }

        setRadioButton()

    }


    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                    onDestroy()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    override fun onResume() {
        super.onResume()
        customerFingerImages.clear()
    }


    private fun setImagerViews() {
        customerFingerImages.clear()
        fingerDataList.clear()
        //
        viewLifecycleOwner.lifecycleScope.launch {
            binding.progressbar.mainPBar.makeVisible()
            delay(1000)
            binding.progressbar.mainPBar.makeGone()

            viewModel.getListFingerPrintDataByPhoneNumber(loanLookupData.idNumber.toString())
                .collect {
                    fingerDataList.addAll(it)
                    it.map { dataModel ->
                        Timber.e("IMAFE FILE PATH ${dataModel.fingerImage.toString()}")
                        getFingerPrintImages(dataModel.fingerImage.toString())
                        Log.e(
                            "DETAILS",
                            "FINGER POSITION: ${dataModel}"
                        )
                        when (dataModel.fingerPosition) {
                            "1" -> binding.ivThumb.setImageBitmap(
                                getBitmapFromFile(dataModel.fingerImage.toString())
                            )

                            "2" -> binding.ivIndex.setImageBitmap(
                                getBitmapFromFile(dataModel.fingerImage.toString())
                            )

                            "3" -> binding.ivMiddle.setImageBitmap(
                                getBitmapFromFile(dataModel.fingerImage.toString())
                            )

                            "4" -> binding.ivRing.setImageBitmap(
                                getBitmapFromFile(dataModel.fingerImage.toString())

                            )

                            "5" -> binding.ivPinky.setImageBitmap(
                                getBitmapFromFile(dataModel.fingerImage.toString())
                            )
                        }
                    }
                }
        }
    }

    private fun setRadioButton() {

        binding.apply {
            rbRightHand.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    TYPE_OF_HAND = "1"
                    rbRightHand.isChecked = false
                    clFingerSelections.visibilityView(true)
                    clHandSelections.visibilityView(false)
                }
            }
            rbLeftHand.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    TYPE_OF_HAND = "0"
                    clFingerSelections.visibilityView(true)
                    clHandSelections.visibilityView(false)
                    rbRightHand.isChecked = false
                }
            }
            /**
             * SELECTION OF FINGER
             */
            rbThumb.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    rbIndex.isChecked = false
                    rbMiddle.isChecked = false
                    rbRing.isChecked = false
                    rbPinky.isChecked = false
                    //
                    TYPE_OF_FINGER = "1"
                    clHandFingerPrintSetUp.visibilityView(false)
                    clFingerPrintArea.visibilityView(true)
                }
            }
            //
            rbIndex.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    rbThumb.isChecked = false
                    rbMiddle.isChecked = false
                    rbRing.isChecked = false
                    rbPinky.isChecked = false
                    //
                    TYPE_OF_FINGER = "2"
                    clHandFingerPrintSetUp.visibilityView(false)
                    clFingerPrintArea.visibilityView(true)
                }
            }
            //
            rbMiddle.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    rbThumb.isChecked = false
                    rbIndex.isChecked = false
                    rbRing.isChecked = false
                    rbPinky.isChecked = false
                    //
                    TYPE_OF_FINGER = "3"
                    clHandFingerPrintSetUp.visibilityView(false)
                    clFingerPrintArea.visibilityView(true)
                }
            }
            //
            rbRing.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    rbThumb.isChecked = false
                    rbIndex.isChecked = false
                    rbMiddle.isChecked = false
                    rbPinky.isChecked = false
                    //
                    TYPE_OF_FINGER = "4"
                    clHandFingerPrintSetUp.visibilityView(false)
                    clFingerPrintArea.visibilityView(true)
                }
            }
            //
            rbPinky.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    rbThumb.isChecked = false
                    rbIndex.isChecked = false
                    rbMiddle.isChecked = false
                    rbRing.isChecked = false
                    //
                    TYPE_OF_FINGER = "5"
                    clHandFingerPrintSetUp.visibilityView(false)
                    clFingerPrintArea.visibilityView(true)
                }
            }


        }
    }

    private fun setUpViewOnclickListener() {
        binding.cvIndex0Finger.setOnClickListener {
            NUMBER_OF_FINGER_BEING_TAKEN = "1"
            startActivityForFingerPrintCapture()
        }
        binding.cvIndex1Finger.setOnClickListener {
            NUMBER_OF_FINGER_BEING_TAKEN = "2"
            startActivityForFingerPrintCapture()
        }
        binding.cvIndex2Finger.setOnClickListener {
            NUMBER_OF_FINGER_BEING_TAKEN = "3"
            startActivityForFingerPrintCapture()

        }
        binding.cvIndex3Finger.setOnClickListener {
            NUMBER_OF_FINGER_BEING_TAKEN = "4"
            startActivityForFingerPrintCapture()
        }
        binding.cvIndex4Finger.setOnClickListener {
            NUMBER_OF_FINGER_BEING_TAKEN = "5"
            startActivityForFingerPrintCapture()

        }

        binding.btnLaunchReader.setOnClickListener {
            launchGetReader()
        }
        binding.btnEnroll.setOnClickListener {
            if (isFieldValid()) {
                performApiRequestEnrollWithMultipleImages()
            }
        }

        binding.tvDesGuide.setOnClickListener {
            lifecycleScope.launch {
                viewModel.deleteByPhoneNumber(loanLookupData.idNumber)
            }
        }
    }

    private fun launchGetReader() {
        val i = Intent(requireActivity(), GetFingerPrintReaderActivity::class.java)
        i.putExtra("device_name", m_deviceName)
        startActivityForResult(i, 1)
    }

    private fun isFieldValid(): Boolean {
        Timber.e("FingerPrintData size is ${fingerDataList.size}")
        if (fingerDataList.size < 4) {
            // snackBarCustom("*phone number must be 10 digits")
            showOneButtonDialog(
                title = "ERROR",
                description = "5 Fingerprint should be captured to ensure successful enrollment",
                image = com.deefrent.rnd.common.R.drawable.ic_baseline_error_outline_24
            )
            return false
        } else {
            return true
        }
    }

    private fun startActivityForFingerPrintCapture() {
        CUSTOMER_PHONE_NUMBER_AT_FP_TAKING = loanLookupData.idNumber
        val i: Intent = Intent(requireActivity(), EnrollFingerPrintCaptureActivity::class.java)
        i.putExtra("device_name", m_deviceName)
        startActivityForResult(i, 1)
    }

    private fun initPowerOn() {
        //power on
        PM.powerOn()
        //enable tracing
        System.setProperty("DPTRACE_ON", "1")
        try {
            mPermissionIntent = PendingIntent.getBroadcast(
                requireActivity(), 0, Intent(
                    ACTION_USB_PERMISSION
                ), 0
            )
            val filter = IntentFilter(ACTION_USB_PERMISSION)
            //Toast.makeText(this, "IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);", Toast.LENGTH_SHORT).show();
            requireActivity().registerReceiver(mUsbReceiver, filter)
        } catch (e: Exception) {
            // already registered
            Toast.makeText(
                requireContext(),
                "Could not register Try again" + e.message,
                Toast.LENGTH_SHORT
            )
                .show()
        }
        initDpSDK()
    }

    private fun initDpSDK() {
        try {
            Globals.ClearLastBitmap()
            // initialize dp sdk
            val applContext: Context = requireActivity().getApplicationContext()

            // try to get the reader as long as its still within 5 seconds
            val startTime = System.currentTimeMillis()
            readers = Globals.getInstance().getReaders(applContext)
            while (readers.size == 0 && System.currentTimeMillis() - startTime < 5000) {
                readers = Globals.getInstance().getReaders(applContext)
            }

//            Toast.makeText(this, String.valueOf(readers.size()), Toast.LENGTH_SHORT).show();
            if (requireActivity().getIntent() != null && requireActivity().getIntent()
                    .getAction() != null && requireActivity().getIntent()
                    .getAction() == UsbManager.ACTION_USB_DEVICE_ATTACHED
            ) {
                InitDevice(0)
            }
            if (readers.size > 0) {
                m_deviceName = readers[0].GetDescription().name
                setUpDevice()
                val i = Intent()
                i.putExtra("device_name", m_deviceName)
                requireActivity().setResult(Activity.RESULT_OK, i)
                // finish()
            } else {
                displayReaderNotFound()
            }
        } catch (e: UareUException) {
            displayReaderNotFound()
        }
    }

    private fun InitDevice(position: Int) {
        try {
            readers[position].Open(Reader.Priority.COOPERATIVE)
            readers[position].Close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun setUpDevice() {
        if (m_deviceName != null && !m_deviceName.isEmpty()) {
            try {
                m_reader = Globals.getInstance()
                    .getReader(m_deviceName, requireActivity().applicationContext)
                if (DPFPDDUsbHost.DPFPDDUsbCheckAndRequestPermissions(
                        requireActivity().applicationContext,
                        mPermissionIntent,
                        m_deviceName
                    )
                ) {
                    CheckDevice()
                }
            } catch (e1: UareUException) {
                displayReaderNotFound()
            } catch (e: DPFPDDUsbException) {
                displayReaderNotFound()
            }
        } else {
            displayReaderNotFound()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) {
            displayReaderNotFound()
            return
        }
        Globals.ClearLastBitmap()
        m_deviceName = (data.extras!!["device_name"] as String?)!!
        when (requestCode) {
            GENERAL_ACTIVITY_RESULT -> if (m_deviceName != null && !m_deviceName.isEmpty()) {
                Timber.d("Device: $m_deviceName")
                try {
                    val applContext: Context = requireContext().getApplicationContext()
                    m_reader = Globals.getInstance().getReader(m_deviceName, applContext)
                    run {
                        val mPermissionIntent: PendingIntent = PendingIntent.getBroadcast(
                            applContext,
                            0,
                            Intent(ACTION_USB_PERMISSION),
                            0
                        )
                        val filter =
                            IntentFilter(ACTION_USB_PERMISSION)
                        applContext.registerReceiver(mUsbReceiver, filter)
                        if (DPFPDDUsbHost.DPFPDDUsbCheckAndRequestPermissions(
                                applContext,
                                mPermissionIntent,
                                m_deviceName
                            )
                        ) {
                            CheckDevice()
                        }
                    }
                } catch (e1: UareUException) {
                    displayReaderNotFound()
                } catch (e: DPFPDDUsbException) {
                    displayReaderNotFound()
                }
            } else {
                displayReaderNotFound()
            }
        }
    }

    private fun CheckDevice() {
        try {
            m_reader.Open(Reader.Priority.EXCLUSIVE)
            val cap = m_reader.GetCapabilities()
            m_reader.Close()
        } catch (e1: UareUException) {
            displayReaderNotFound()
        }
    }

    private fun displayReaderNotFound() {
        try {
            showTwoButtonDialog(
                title = "Reader Not Found",
                description = "Allow reader permission prompted.Click 'Get started' to get reader permission request, or 'Dismiss' to go back.",
                btnConfirmTitle = "Get Reader",
                btnCancelTitle = "Dismiss",
                listenerCancel = {
                    findNavController().navigateUp()
                    Timber.d("DISMISS")
                },
                listenerConfirm = {
                    //requireActivity().recreate()
                    launchGetReader()
                }
            )
        } catch (e: Exception) {
            Log.e("displayReaderNotFound", "$e")
        }

    }

    private val mUsbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                val action = intent.action
                if (ACTION_USB_PERMISSION == action) {
                    synchronized(this) {
                        val device =
                            intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                        if (intent.getBooleanExtra(
                                UsbManager.EXTRA_PERMISSION_GRANTED,
                                true
                            )
                        ) {
                            if (device != null) {
                                //call method to set up device communication
                                CheckDevice()
                                //                            Toast.makeText(context, "RECEIVED", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("mUsbReceiver", "$e")
            }
        }
    }

    private fun performApiRequestEnrollWithMultipleImages() {
        viewLifecycleOwner.lifecycleScope.launch {
            Timber.e("Number of prints${customerFingerImages.size}")
            Timber.d(
                "${loanLookupData.idNumber} ${customerFingerImages.toMutableList()}"
            )

            viewModel.enrollWithMultipleImages(
                idNumber = loanLookupData.idNumber.toString(),
                finger_index = TYPE_OF_FINGER,
                hand_type = TYPE_OF_HAND,
                mutableListOfFiles = customerFingerImages.toMutableList()
            ).collect {
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
                        if (it.data?.status == 200) {
                            try {
                                PM.powerOff()
                                //requireActivity().unregisterReceiver(mUsbReceiver)
                                snackBarCustom("Closed scanner")
                            } catch (e: Exception) {
                                // already unregistered
                            }

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
                            description = "${it.error?.message.toString()}",
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
                                description = "Finger prints enrollment successfully",//it.data!!.message.toString(),
                                image = com.deefrent.rnd.common.R.drawable.ic_success_check
                            ) {
                                deleteImageFileWithFileName(
                                    context = requireContext(),
                                    stringToContain = loanLookupData.idNumber
                                )
                                lifecycleScope.launch {
                                    viewModel.deleteByPhoneNumber(loanLookupData.idNumber)
                                }
                                findNavController().popBackStack(R.id.dashboardFragment, false)
                            }
                        } else {
                            showOneButtonDialog(
                                title = "Failed",
                                description = it.data!!.message.toString(),
                                image = com.deefrent.rnd.common.R.drawable.ic_baseline_error_outline_24
                            ) {
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getFingerPrintImages(bitmapString: String) {
        val mediaType = "image/png".toMediaTypeOrNull()
        val fileAsset = File("${bitmapString}")
        val requestFileAsset = fileAsset.asRequestBody(mediaType)
        val assetPhotoUrl =
            MultipartBody.Part.createFormData(
                "images",
                fileAsset.name,
                requestFileAsset
            )
        if (customerFingerImages.size != 5) {
            customerFingerImages.add(assetPhotoUrl)
        }
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



