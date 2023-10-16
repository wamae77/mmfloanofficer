package com.deefrent.rnd.fieldapp.view.fingerPrint.method1

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.digitalpersona.uareu.Reader
import com.digitalpersona.uareu.ReaderCollection
import com.digitalpersona.uareu.UareUException
import com.digitalpersona.uareu.dpfpddusbhost.DPFPDDUsbException
import com.digitalpersona.uareu.dpfpddusbhost.DPFPDDUsbHost
import com.deefrent.rnd.fieldapp.view.fingerPrint.method2.Globals
import com.gne.pm.PM
import io.reactivex.disposables.CompositeDisposable

class SetUpInKotlinActivity : Activity() {
    private val m_DPI = 0
    private var m_reader: Reader? = null
    private lateinit var readers: ReaderCollection//? = null
    private lateinit var m_deviceName: String//? = ""
    private val m_versionName = ""
    lateinit var applContext: Context//? = null
    var mPermissionIntent: PendingIntent? = null
    private val disposables = CompositeDisposable()

    private val ACTION_USB_PERMISSION =
        "com.digitalpersona.uareu.dpfpddusbhost.USB_PERMISSION"
    var MY_PERMISSIONS = arrayOf(
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.MOUNT_UNMOUNT_FILESYSTEMS",
        "android.permission.WRITE_OWNER_DATA",
        "android.permission.READ_OWNER_DATA",
        "android.hardware.usb.accessory",
        "com.digitalpersona.uareu.dpfpddusbhost.USB_PERMISSION",
        "android.permission.HARDWARE_TEST",
        "android.hardware.usb.host"
    )
    private val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*   val checkCallPhonePermission = ContextCompat.checkSelfPermission(
               this@SetUpInKotlinActivity,
               Manifest.permission.WRITE_EXTERNAL_STORAGE
           )
           if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(this, MY_PERMISSIONS, REQUEST_CODE);
           } else {*/
        initPowerOn();
        // }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            /* if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(SetUpInKotlin.this, "no permission ,plz to request~", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(SetUpInKotlin.this, MY_PERMISSIONS, REQUEST_CODE);
            } else {*/
            initPowerOn();
            // }
        }
    }

    private fun initDpSDK() {
        try {
            Globals.ClearLastBitmap()
            // initialize dp sdk
            val applContext = applicationContext

            // try to get the reader as long as its still within 5 seconds
            val startTime = System.currentTimeMillis()
            readers = Globals.getInstance().getReaders(applContext)
            while (readers.size == 0 && System.currentTimeMillis() - startTime < 5000) {
                readers = Globals.getInstance().getReaders(applContext)
            }

//            Toast.makeText(this, String.valueOf(readers.size()), Toast.LENGTH_SHORT).show();
            if (intent != null && intent.action != null && intent.action == UsbManager.ACTION_USB_DEVICE_ATTACHED) {
                InitDevice(0)
            }
            if (readers.size > 0) {
                m_deviceName = readers.get(0).GetDescription().name
                setUpDevice()
                val i = Intent()
                i.putExtra("device_name", m_deviceName)
                setResult(RESULT_OK, i)
                finish()
            } else {
                displayReaderNotFound()
            }
        } catch (e: UareUException) {
            displayReaderNotFound()
        }
    }

    private fun InitDevice(position: Int) {
        try {
            readers!![position].Open(Reader.Priority.COOPERATIVE)
            readers!![position].Close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun initPowerOn() {
        //power on
        PM.powerOn()
        //enable tracing
        System.setProperty("DPTRACE_ON", "1")
        try {
            applContext = applicationContext
            mPermissionIntent = PendingIntent.getBroadcast(
                applContext, 0, Intent(
                    ACTION_USB_PERMISSION
                ), 0
            )
            val filter = IntentFilter(ACTION_USB_PERMISSION)
            //Toast.makeText(this, "IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);", Toast.LENGTH_SHORT).show();
            applContext.registerReceiver(mUsbReceiver, filter)
        } catch (e: Exception) {
            // already registered
            Toast.makeText(this, "Could not register Try again" + e.message, Toast.LENGTH_SHORT)
                .show()
        }
        initDpSDK()
    }

    private fun setUpDevice() {
        if (m_deviceName != null && !m_deviceName!!.isEmpty()) {
            try {
                m_reader = Globals.getInstance().getReader(m_deviceName, applContext)
                if (DPFPDDUsbHost.DPFPDDUsbCheckAndRequestPermissions(
                        applContext,
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

    protected fun CheckDevice() {
        try {
            m_reader!!.Open(Reader.Priority.EXCLUSIVE)
            val cap = m_reader!!.GetCapabilities()
            m_reader!!.Close()
        } catch (e1: UareUException) {
            displayReaderNotFound()
        } catch (e: Exception) {
            displayReaderNotFound()
        }
    }

    private fun displayReaderNotFound() {
        Toast.makeText(this, "Plug in a reader and try again.", Toast.LENGTH_SHORT)
//        Toast.makeText(this, " ", Toast.LENGTH_SHORT).show();
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

    override fun onBackPressed() {
        val i = Intent()
        i.putExtra("device_name", m_deviceName)
        setResult(RESULT_OK, i)
        finish()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(mUsbReceiver)
            disposables.clear()
        } catch (e: Exception) {
            // already unregistered
        }
        //power off
        super.onDestroy()
    }

    companion object {

    }
}