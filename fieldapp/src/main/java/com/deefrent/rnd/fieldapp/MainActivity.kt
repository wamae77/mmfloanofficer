package com.deefrent.rnd.fieldapp

import android.content.IntentFilter
import android.content.IntentSender
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.deefrent.rnd.common.abstractions.BaseActivity
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.fieldapp.databinding.ActivityMainBinding
import com.deefrent.rnd.fieldapp.ui.onboardAccount.toast
import com.deefrent.rnd.fieldapp.utils.ConnectionLiveData
import com.deefrent.rnd.fieldapp.utils.ConnectivityReceiver
import com.deefrent.rnd.fieldapp.utils.isNetworkAvailable
import com.deefrent.rnd.fieldapp.utils.showInfoDialog
import com.deefrent.rnd.fieldapp.view.fingerPrint.method2.LoginFingerPrintCaptureActivity.Companion.IS_AUTH_SUCCESSFUL
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.jiboostfieldapp.ui.isEmulator
import com.deefrent.rnd.jiboostfieldapp.ui.isSIMInserted
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.scottyab.rootbeer.RootBeer
import dagger.android.AndroidInjector
import javax.inject.Inject

class MainActivity : BaseActivity(), ConnectivityReceiver.ConnectivityReceiverListener {
    private var binding: ActivityMainBinding? = null
    private var offlineSnackBar: Snackbar? = null
    private val TAG = "MainActivity"
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var MY_UPDATE_REQUEST_CODE = 1
    private lateinit var mAppUpdateManager: AppUpdateManager

    private lateinit var navController: NavController

    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences

    /**
     *TODO : SETTING FINGERPRINT ITEMS
     */

    /*    private var m_DPI: Int = 0

        private val ACTION_USB_PERMISSION = "com.digitalpersona.uareu.dpfpddusbhost.USB_PERMISSION"

        private var m_reader: Reader? = null

        private lateinit var readers: ReaderCollection //? = null

        private var m_deviceName = ""
        private val m_versionName = ""


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

        val REQUEST_CODE = 1
        lateinit var applContext: Context //? = null
        var mPermissionIntent: PendingIntent? = null

        private val disposables = CompositeDisposable()
    // ----------------------------------------------------------------------------------------*/

    @JvmField
    @Inject
    var viewModelFactory: ViewModelProvider.Factory? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**check for rooted devices*/
        //checkForRootedDevices(savedInstanceState)

        doNotCheckForRootedDevice(savedInstanceState)

        commonSharedPreferences.saveStringData(IS_AUTH_SUCCESSFUL, "0")
    }


    private fun doNotCheckForRootedDevice(savedInstanceState: Bundle?) {

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        //preventScreenShotCapture(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //register receiver for checking internet connection status
        registerReceiver(
            ConnectivityReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
        if (isNetworkAvailable(this)) {
            //checkUpdates()
        }
        //
        //mViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel.class);

        //init detect internet connection livedata
        val connectionLiveData = ConnectionLiveData(this@MainActivity)
        //observe internet connection livedata
        /*connectionLiveData.observe(this) { isConnected ->
            isConnected?.let {
                if (!it) {
                    offlineSnackBar = Snackbar.make(+
                        findViewById(R.id.container),
                        "You are offline",
                        Snackbar.LENGTH_INDEFINITE
                    )

                    offlineSnackBar?.setAction("DISMISS") { // Call your action method here
                        offlineSnackBar?.dismiss()
                    }
                    offlineSnackBar?.show()
                }
            }
            isConnected?.let {
                if (it) {
                    offlineSnackBar?.dismiss()
                }
            }
        }*/

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(topLevelDestinationIds = setOf())
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.dashboardFragment || destination.id == R.id.quickReportsFragment || destination.id == R.id.feedBackFragment) {
                binding!!.bottomNavigationView.visibility = View.VISIBLE
            } else {
                binding!!.bottomNavigationView.visibility = View.GONE
            }
        }

        setupWithNavController(binding!!.bottomNavigationView, navController)
        //idle timer handler
        /*handler = Handler()
        runnable = Runnable {
            //navController.navigate(R.id.action_global_to_loginFragment)
            startActivity(Intent(this,MainActivity::class.java))
            finish()
            //recreate()
            //navController.navigateUp()
        }

        startHandler()*/
    }

    private fun checkForRootedDevices(savedInstanceState: Bundle?) {
        val rootBeer = RootBeer(this)
        if (rootBeer.isRooted) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(title)
            builder.setMessage(getString(com.deefrent.rnd.jiboostfieldapp.R.string.root_message))
            builder.setPositiveButton(getString(com.deefrent.rnd.jiboostfieldapp.R.string.ok)) { _, _ ->
                finishAffinity()
            }
            builder.setCancelable(false)
            builder.show()
        } else if (isEmulator()) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(title)
            builder.setMessage(getString(com.deefrent.rnd.jiboostfieldapp.R.string.emulator_message))
            builder.setPositiveButton(getString(com.deefrent.rnd.jiboostfieldapp.R.string.ok)) { _, which ->
                finishAffinity()
            }
            builder.setCancelable(false)
            builder.show()
        } else if (!isSIMInserted(this)) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(title)
            builder.setMessage(getString(com.deefrent.rnd.jiboostfieldapp.R.string.no_sim))
            builder.setPositiveButton(getString(com.deefrent.rnd.jiboostfieldapp.R.string.ok)) { _, which ->
                finishAffinity()
            }
            builder.setCancelable(false)
            builder.show()
        } else {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding!!.root)
            //preventScreenShotCapture(this)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            //register receiver for checking internet connection status
            registerReceiver(
                ConnectivityReceiver(),
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
            if (isNetworkAvailable(this)) {
                //checkUpdates()
            }
            //
            //mViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel.class);

            //init detect internet connection livedata
            val connectionLiveData = ConnectionLiveData(this@MainActivity)
            //observe internet connection livedata
            /*connectionLiveData.observe(this) { isConnected ->
                isConnected?.let {
                    if (!it) {
                        offlineSnackBar = Snackbar.make(
                            findViewById(R.id.container),
                            "You are offline",
                            Snackbar.LENGTH_INDEFINITE
                        )

                        offlineSnackBar?.setAction("DISMISS") { // Call your action method here
                            offlineSnackBar?.dismiss()
                        }
                        offlineSnackBar?.show()
                    }
                }
                isConnected?.let {
                    if (it) {
                        offlineSnackBar?.dismiss()
                    }
                }
            }*/

            val navController = findNavController(R.id.nav_host_fragment)
            val appBarConfiguration = AppBarConfiguration(topLevelDestinationIds = setOf())
            navController.addOnDestinationChangedListener { _, destination, _ ->
                if (destination.id == R.id.dashboardFragment || destination.id == R.id.quickReportsFragment || destination.id == R.id.feedBackFragment) {
                    binding!!.bottomNavigationView.visibility = View.VISIBLE
                } else {
                    binding!!.bottomNavigationView.visibility = View.GONE
                }
            }

            setupWithNavController(binding!!.bottomNavigationView, navController)
            //idle timer handler
            /*handler = Handler()
            runnable = Runnable {
                //navController.navigate(R.id.action_global_to_loginFragment)
                startActivity(Intent(this,MainActivity::class.java))
                finish()
                //recreate()
                //navController.navigateUp()
            }

            startHandler()*/

        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        displayConnectionStatus(isConnected)
    }

    private fun displayConnectionStatus(isConnected: Boolean) {
        if (!isConnected) {
            offlineSnackBar = Snackbar.make(
                findViewById(R.id.container),
                "You are offline",
                Snackbar.LENGTH_LONG
            )

            offlineSnackBar?.setAction("DISMISS") { // Call your action method here
                offlineSnackBar?.dismiss()
            }
            offlineSnackBar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            offlineSnackBar?.show()
        } else {
            offlineSnackBar?.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        //startHandler()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    private fun checkUpdates() {
        mAppUpdateManager = AppUpdateManagerFactory.create(this)
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = mAppUpdateManager.appUpdateInfo
        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                // Request the update.
                try {
                    mAppUpdateManager.startUpdateFlowForResult(
                        // Pass the intent that is returned by 'getAppUpdateInfo()'.
                        appUpdateInfo,
                        // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                        //AppUpdateType.FLEXIBLE,
                        // The current activity making the update request.
                        this,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE)
                            .setAllowAssetPackDeletion(true)
                            .build(),
                        // Include a request code to later monitor this update request.
                        MY_UPDATE_REQUEST_CODE
                    )
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                    showInfoDialog(e.localizedMessage)
                }
            }
        }
    }

    private fun stopHandler() {
        handler.removeCallbacks(runnable)
        Log.d("HandlerRun", "stopHandlerMain")
    }

    private fun startHandler() {
        handler.postDelayed(runnable, (1 * 60 * 1000).toLong())
        Log.d("HandlerRun", "startHandlerMain")
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        //stopHandler()
        //startHandler()
    }

    override fun onPause() {
        //stopHandler()
        Log.d("onPause", "onPauseActivity change")
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        //stopHandler()
        toast("Closed MMF App")
        Log.d("onDestroy", "onDestroyActivity change")

        try {
            // unregisterReceiver(mUsbReceiver)
            //disposables.clear()
        } catch (e: Exception) {
            // already unregistered
        }
    }


    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        // Fragment Injector should use the Application class
        // If necessary, I will use AndroidInjector as well as App class (I have not done this time)
        return (application as BaseApp).supportFragmentInjector()
    }

    /**
     *TODO : SETTING FINGERPRINT ITEMS
     */


}