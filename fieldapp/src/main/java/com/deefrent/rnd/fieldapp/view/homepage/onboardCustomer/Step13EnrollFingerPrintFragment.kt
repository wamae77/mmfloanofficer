package com.deefrent.rnd.fieldapp.view.homepage.onboardCustomer

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
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
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.common.utils.CUSTOMER_PHONE_NUMBER_AT_FP_TAKING
import com.deefrent.rnd.common.utils.GENERAL_ACTIVITY_RESULT
import com.deefrent.rnd.common.utils.MY_PERMISSIONS
import com.deefrent.rnd.common.utils.REQUEST_CODE
import com.deefrent.rnd.common.utils.TYPE_OF_FINGER
import com.deefrent.rnd.common.utils.addTextWatcher
import com.deefrent.rnd.common.utils.getBitmapFromFile
import com.deefrent.rnd.common.utils.showOneButtonDialog
import com.deefrent.rnd.common.utils.snackBarCustom
import com.deefrent.rnd.common.utils.visibilityView
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentEnrollFingerPrintStep13Binding
import com.deefrent.rnd.fieldapp.utils.base_fragment.BaseMoneyMartBindedFragment
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.utils.setToolbarTitle
import com.deefrent.rnd.fieldapp.view.fingerPrint.FingerPrintViewModel
import com.deefrent.rnd.fieldapp.view.fingerPrint.SharedViewModelToStoreImageData
import com.deefrent.rnd.fieldapp.view.fingerPrint.method2.EnrollFingerPrintCaptureActivity
import com.deefrent.rnd.fieldapp.view.fingerPrint.method2.GetFingerPrintReaderActivity
import com.deefrent.rnd.fieldapp.view.fingerPrint.method2.Globals
import com.gne.pm.PM
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class Step13EnrollFingerPrintFragment :
    BaseMoneyMartBindedFragment<FragmentEnrollFingerPrintStep13Binding>(
        FragmentEnrollFingerPrintStep13Binding::inflate
    ) {

    @Inject
    lateinit var viewModel: FingerPrintViewModel
    private val sharedViewModel: SharedViewModelToStoreImageData by activityViewModels()

    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences
    private var fingerDataList: HashSet<FingerPrintData> = HashSet()

    /**
     *
     */
    private var m_deviceName = ""
    private lateinit var m_reader: Reader//? = null
    private lateinit var readers: ReaderCollection
    private var mPermissionIntent: PendingIntent? = null
    private val ACTION_USB_PERMISSION = "USB_PERMISSION"

    /**
     *
     */
    private lateinit var customer_id_number: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Determine if the current Android version is >=23
        // 判断Android版本是否大于23
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


    private fun handleBackButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    onDestroy()
                    findNavController().navigate(R.id.customerAdditionalDetailsFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle(
            toolBarBinding = binding.toolBar,
            activity = requireActivity(),
            title = "Fingerprint enrollment", //getString(com.ekenya.rnd.common.R.string.funeral_insurance),
            action = {
                onDestroy()
                findNavController().navigate(R.id.customerAdditionalDetailsFragment)
            }
        )

        handleBackButton()

        customer_id_number =
            commonSharedPreferences.getStringData(
                CommonSharedPreferences.CU_ID_NUMBER
            )
        binding.tlPhoneNumber.visibilityView(false)

        addTextWatcher(binding.etPhoneNumber) { text ->
            Timber.d("PHONE ${text}")
        }



        setUpViewOnclickListener()

    }

    override fun onResume() {
        super.onResume()
        fingerDataList.clear()
        setImagerViews()
    }

    override fun onPause() {
        super.onPause()
        fingerDataList.clear()
    }

    private fun setImagerViews() {
        fingerDataList.clear()
        //
        viewLifecycleOwner.lifecycleScope.launch {
            binding.progressbar.mainPBar.makeVisible()
            delay(1000)
            binding.progressbar.mainPBar.makeGone()

            viewModel.getListFingerPrintDataByPhoneNumber(customer_id_number)
                .collect {
                    fingerDataList.addAll(it)
                    it.map { dataModel ->
                        Timber.d("FINGER POSITION: ${dataModel.fingerPosition}, FINGER_IMAGE: ${dataModel.fingerImage}")
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

    private fun setUpViewOnclickListener() {
        binding.cvIndex0Finger.setOnClickListener {
            TYPE_OF_FINGER = "1"
            startActivityForFingerPrintCapture()
        }
        binding.cvIndex1Finger.setOnClickListener {
            TYPE_OF_FINGER = "2"
            startActivityForFingerPrintCapture()
        }
        binding.cvIndex2Finger.setOnClickListener {
            TYPE_OF_FINGER = "3"
            startActivityForFingerPrintCapture()

        }
        binding.cvIndex3Finger.setOnClickListener {
            TYPE_OF_FINGER = "4"
            startActivityForFingerPrintCapture()
        }
        binding.cvIndex4Finger.setOnClickListener {
            TYPE_OF_FINGER = "5"
            startActivityForFingerPrintCapture()

        }

        binding.btnLaunchReader.setOnClickListener {
            launchGetReader()
        }

        binding.btnEnroll.setOnClickListener {
            if (isFieldValid()) {
                onDestroy()
                findNavController().navigate(R.id.summaryFragment)
                // performApiRequestEnrollWithMultipleImages()
            }
        }
        binding.tvServiceDesc.setOnClickListener {
            lifecycleScope.launch {
                viewModel.deleteByPhoneNumber(binding.etPhoneNumber.text.toString())
            }
        }
    }

    private fun launchGetReader() {
        val i = Intent(requireActivity(), GetFingerPrintReaderActivity::class.java)
        i.putExtra("device_name", m_deviceName)
        startActivityForResult(i, 1)
    }

    private fun isFieldValid(): Boolean {
        snackBarCustom("FingerPrintData size is ${fingerDataList.size}")
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
        CUSTOMER_PHONE_NUMBER_AT_FP_TAKING = customer_id_number
        val i: Intent =
            Intent(requireActivity(), EnrollFingerPrintCaptureActivity::class.java)
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
            val applContext: Context = requireActivity().applicationContext

            // try to get the reader as long as its still within 5 seconds
            val startTime = System.currentTimeMillis()
            readers = Globals.getInstance().getReaders(applContext)
            while (readers.size == 0 && System.currentTimeMillis() - startTime < 5000) {
                readers = Globals.getInstance().getReaders(applContext)
            }

//            Toast.makeText(this, String.valueOf(readers.size()), Toast.LENGTH_SHORT).show();
            if (requireActivity().intent != null && requireActivity().intent
                    .action != null && requireActivity().intent
                    .action == UsbManager.ACTION_USB_DEVICE_ATTACHED
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
                //m_selectedDevice.setText("Device: $m_deviceName")
                try {
                    val applContext: Context = requireActivity().applicationContext
                    m_reader = Globals.getInstance().getReader(m_deviceName, applContext)
                    run {
                        val mPermissionIntent: PendingIntent
                        mPermissionIntent =
                            PendingIntent.getBroadcast(
                                applContext,
                                0,
                                Intent(ACTION_USB_PERMISSION),
                                0
                            )
                        val filter = IntentFilter(ACTION_USB_PERMISSION)
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
                initDpSDK()
                // displayReaderNotFound()
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
            val alertDialogBuilder = AlertDialog.Builder(requireActivity())
            alertDialogBuilder.setTitle("Reader Not Found")
            alertDialogBuilder.setMessage("Plug in a reader and try again.").setCancelable(false)
                .setPositiveButton(
                    "Retry"
                ) { dialog, id ->
                    dialog.dismiss()
                    launchGetReader()
                }
                .setNegativeButton(
                    "Ok"
                ) { dialog, id ->
                    dialog.dismiss()
                }
            val alertDialog = alertDialogBuilder.create()
            if (!requireActivity().isFinishing) {
                alertDialog.show()
            }
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


