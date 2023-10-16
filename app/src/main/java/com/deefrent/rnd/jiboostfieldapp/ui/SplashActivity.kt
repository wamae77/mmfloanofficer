package com.deefrent.rnd.jiboostfieldapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.deefrent.rnd.common.abstractions.BaseActivity
import com.deefrent.rnd.common.utils.CURRENCY_CODE
import com.deefrent.rnd.common.utils.getCurrentDateTimeString
import com.deefrent.rnd.common.utils.visibilityView
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.jiboostfieldapp.R
import com.deefrent.rnd.jiboostfieldapp.databinding.ActivitySplashBinding
import com.deefrent.rnd.jiboostfieldapp.ui.main.MainFragment
import com.deefrent.rnd.jiboostfieldapp.ui.main.MainViewModel
import com.deefrent.rnd.jiboostfieldapp.ui.printer.AppPrintServiceActivity
import com.deefrent.rnd.jiboostfieldapp.ui.printer.PrinterConfigs
import com.scottyab.rootbeer.RootBeer
import dagger.android.AndroidInjector
import javax.inject.Inject

class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    private var mApp: BaseApp? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val mViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //
        mApp = application as BaseApp
        /**check for rooted devices*/
        //checkForRootedDevices(savedInstanceState)
        doNotCheckForRootedDevice(savedInstanceState)
    }

    private fun doNotCheckForRootedDevice(savedInstanceState: Bundle?) {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //preventScreenShotCapture(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val data = mViewModel.getData()

        Log.i("SplashActivity", "=> $data")
        //
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }


        binding.btnPrint.visibilityView(false)
        binding.btnOpenMMF.visibilityView(false)
        binding.btnPrint.setOnClickListener {
            val receiptTextArray = arrayOf<String>(
                AppPrintServiceActivity.centeredText("COLLECTIONS", 48),
                AppPrintServiceActivity.centeredText("Validation Successful", 44),
                AppPrintServiceActivity.SEPARATOR_LINE,
                "Txn Amount:    $CURRENCY_CODE 12000",
                "Txn Fee:       $CURRENCY_CODE 120",
                "Excise Duty:   $CURRENCY_CODE 100",
                "Total Amount:  $CURRENCY_CODE 36000",
                AppPrintServiceActivity.SEPARATOR_LINE,
                "\n",
                "BILL A/C NO :  " + "QWETYUIUYTRE",
                "PHONE NO :   " + "0798997948"
            )


            PrinterConfigs.AGENT_CODE = "12334567"
            PrinterConfigs.TERMINAL_NO = "terminalID"
            PrinterConfigs.AGENT_NAME = "agentName" //operatorName

            PrinterConfigs.AGENT_BRANCH_STREET = "bankStreet" //operatorId


            val map: HashMap<String, String> = HashMap()
            map["requestTime"] = getCurrentDateTimeString()
            map["reference"] = "5256565456545"

            val intent = Intent(this, AppPrintServiceActivity::class.java)
            intent.putExtra("finishActivityOnPrint", true)
            intent.putExtra("printText", receiptTextArray)
            intent.putExtra("hasSignatureBitmap", false)
            intent.putExtra("params", map)
            startActivityForResult(intent, 234)
        }

        //todo:Remove this
        binding.btnOpenMMF.setOnClickListener {
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
            }
        }

    }

    private fun checkForRootedDevices(savedInstanceState: Bundle?) {
        val rootBeer = RootBeer(this)
        if (rootBeer.isRooted) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(title)
            builder.setMessage(getString(R.string.root_message))
            builder.setPositiveButton(getString(R.string.ok)) { _, which ->
                finishAffinity()
            }
            builder.setCancelable(false)
            builder.show()
        } else if (isEmulator()) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(title)
            builder.setMessage(getString(R.string.emulator_message))
            builder.setPositiveButton(getString(R.string.ok)) { _, which ->
                finishAffinity()
            }
            builder.setCancelable(false)
            builder.show()
        } else if (!isSIMInserted(this)) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(title)
            builder.setMessage(getString(R.string.no_sim))
            builder.setPositiveButton(getString(R.string.ok)) { _, which ->
                finishAffinity()
            }
            builder.setCancelable(false)
            builder.show()
        } else {
            binding = ActivitySplashBinding.inflate(layoutInflater)
            setContentView(binding.root)
            //preventScreenShotCapture(this)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            val data = mViewModel.getData()

            Log.i("SplashActivity", "=> $data")
            //
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
            }
        }
    }


    /**
     *To finish setting up Crashlytics and see initial data in the Crashlytics dashboard of the Firebase console, you need to force a test crash.

    Add code to your app that you can use to force a test crash.

    You can use the following code in your app's MainActivity to add a button to your app that, when pressed, causes a crash. The button is labeled "Test Crash".
     */
    private fun testCrashlytics() {
// Creates a button that mimics a crash when pressed
        val crashButton = Button(this)
        crashButton.setText("Test Crash")
        crashButton.setOnClickListener {
            throw RuntimeException("Test Crash") // Force a crash
        }

        addContentView(
            crashButton, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
// Fragment Injector should use the Application class
// If necessary, I will use AndroidInjector as well as App class (I have not done this time)
        return (application as BaseApp).supportFragmentInjector()
    }


}


fun cLEARPRINTINGDATA() {
    PrinterConfigs.RECEIPT_TEXT_ARRAY = null
    PrinterConfigs.TYPE_OF_RECEIPT = ""
    PrinterConfigs.AGENT_CODE = ""
    PrinterConfigs.TERMINAL_NO = ""
    PrinterConfigs.AGENT_NAME = ""
    PrinterConfigs.SERVER_BY = ""
    PrinterConfigs.AGENT_BRANCH_STREET = ""
    PrinterConfigs.TRANSACTION_REFERENCE = ""
    PrinterConfigs.TIME_OF_TRANSACTION_REQUEST = ""
    PrinterConfigs.FINISH_ACTIVITY_ON_PRINT = false
    PrinterConfigs.HAS_SIGNATURE_BITMAP = false
    PrinterConfigs.QR_AUTH_CODE_TO_PRINT = ""
}