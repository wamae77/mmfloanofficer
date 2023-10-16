package com.deefrent.rnd.jiboostfieldapp

import SecuGen.FDxSDKPro.JSGFPLib
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Parcelable
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.deefrent.rnd.common.abstractions.BaseApplication
import com.deefrent.rnd.common.utils.Constants
import com.deefrent.rnd.common.utils.Constants.deleteCacheImageFromInternalStorage
import com.deefrent.rnd.jiboostfieldapp.di.AppComponent
import com.deefrent.rnd.jiboostfieldapp.di.BaseModuleInjector
import com.deefrent.rnd.jiboostfieldapp.di.DaggerAppComponent
import com.deefrent.rnd.jiboostfieldapp.di.helpers.features.FeatureModule
import com.deefrent.rnd.jiboostfieldapp.ui.printer.DeviceType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nexgo.oaf.apiv3.APIProxy
import com.pos.sdk.DeviceManager
import com.pos.sdk.DevicesFactory
import com.pos.sdk.callback.ResultCallback
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*
import javax.inject.Inject

class BaseApp : BaseApplication(), HasActivityInjector, HasSupportFragmentInjector {

    // ActivityInjector / FragmentInjector used in the main module
    @Inject
    lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var dispatchingFragmentInjector: DispatchingAndroidInjector<Fragment>

    // List of ActivityInjector / FragmentInjector used in each Feature module
    private var moduleActivityInjectors = mutableListOf<DispatchingAndroidInjector<Activity>>()
    private val moduleFragmentInjectors = mutableListOf<DispatchingAndroidInjector<Fragment>>()

    // AndroidInjector <Activity> that actually injects
    private val activityInjector = AndroidInjector<Activity> { instance ->
        // If true is returned by maybeInject, Inject is successful

        // Main module
        if (dispatchingActivityInjector.maybeInject(instance)) {
            return@AndroidInjector
        }

        /*val fragmentInjector=moduleActivityInjectors[0]
        if (fragmentInjector.maybeInject(instance)) {
            return@AndroidInjector
        }*/
        val gson = Gson()
        val json = gson.toJson(moduleActivityInjectors)
        Log.d("TAG", "injectors: $json ")
        if (moduleActivityInjectors.isNotEmpty()) {
            Log.d("TAG", "injectorsTrue: ${moduleFragmentInjectors.size} ")
            AppPreferences.activityInjectors = json
            // Each Feature module
            moduleActivityInjectors.forEach { injector ->
                if (injector.maybeInject(instance)) {
                    return@AndroidInjector
                }
            }
        } else {
            val savedActivityInjectors = AppPreferences.activityInjectors
            val type: Type =
                object : TypeToken<List<DispatchingAndroidInjector<Activity>?>?>() {}.type
            moduleActivityInjectors = gson.fromJson(savedActivityInjectors, type)
            Log.d("TAG", "injectorsFalse: ${moduleFragmentInjectors.size} ")
            // Each Feature module
            moduleActivityInjectors.forEach { injector ->
                if (injector.maybeInject(instance)) {
                    return@AndroidInjector
                }
            }
        }

        throw IllegalStateException("Injector not found for $instance")
    }

    data class Test(val test: String)

    // AndroidInjector <Fragment> that actually injects each
    private val fragmentInjector = AndroidInjector<Fragment> { instance ->
        // If true is returned by maybeInject, Inject is successful

        // Main module
        if (dispatchingFragmentInjector.maybeInject(instance)) {
            return@AndroidInjector
        }

        // Each Feature module
        moduleFragmentInjectors.forEach { injector ->
            if (injector.maybeInject(instance)) {
                return@AndroidInjector
            }
        }
        throw IllegalStateException("Injector not found for $instance")
    }

    // Set for determining whether the Injector of the Feature module has been generated
    private val injectedModules = mutableSetOf<FeatureModule>()

    // Used from AppComponent and Component of each Feature module
    val appComponent by lazy {
        DaggerAppComponent.builder().create(this) as AppComponent
    }

    companion object {
        init {
            System.loadLibrary("native-lib")
        }

        lateinit var mresource: Resources
        private var INSTANCE: BaseApp? = null
        fun applicationContext(): Context {
            return INSTANCE!!.applicationContext
        }

        private var mInstance: BaseApp? = null
        private var deviceType: DeviceType? = null
        private var sgfplib: JSGFPLib? = null


        fun deviceType(): DeviceType {
            return deviceType!!
        }

        fun sgfplib(): JSGFPLib? {
            return sgfplib
        }
    }

    init {
        INSTANCE = this
    }

    private external fun getBaseURL(): String?

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        mresource = resources
        AppPreferences.init(this)
        appComponent.inject(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Constants.DEVICE_ID = getDeviceUUID(this)
        Constants.BASE_URL = getBaseURL()!!
        deleteCacheImageFromInternalStorage(this, "compressor")

        /**
         * initialize Realm using the configuration in common
         * */
        /* Realm.init(this)
         val realmConfiguration = RealmConfig.create()
         Realm.setDefaultConfiguration(realmConfiguration)*/
        //AndroidThreeTen.init(this)

        //setUpPOSConfiguration()
    }

    private fun getDeviceUUID(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            .uppercase(
                Locale.getDefault()
            )
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        // Returns the actual Injector
        return activityInjector
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        // Returns the actual Injector
        return fragmentInjector
    }

    // Add Injector for Feature module
    // Called just before the Feature module is used after installation
    fun addModuleInjector(module: FeatureModule) {
        if (injectedModules.contains(module)) {
            // Do nothing if added
            return
        }

        // Generate Injector for Feature module
        val clazz = Class.forName(module.injectorName)
        val moduleInjector = clazz.newInstance() as BaseModuleInjector
        // Inject Dispatching Android Injector of Injector of Feature module
        moduleInjector.inject(this)

        // Add to list
        moduleActivityInjectors.add(moduleInjector.activityInjector())
        moduleFragmentInjectors.add(moduleInjector.fragmentInjector())

        injectedModules.add(module)

    }

    private var mPermissionIntent: PendingIntent? = null
    private var filter: IntentFilter? = null
    private val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
    private fun setUpPOSConfiguration() {
        setUpSecugen()
        try {
            DevicesFactory.create(this, object : ResultCallback<DeviceManager?> {
                override fun onFinish(deviceManager: DeviceManager?) {
                    deviceType = DeviceType.CTA_K
                }

                override fun onError(i: Int, s: String) {
                    otherPOSType()
                }
            })
        } catch (exception: Exception) {
            otherPOSType()
        }
    }

    private fun otherPOSType() {
        deviceType = try {
            APIProxy.getDeviceEngine(this)
            DeviceType.N5
        } catch (e: Throwable) {
            e.printStackTrace()
            DeviceType.CS10
        }
    }

    private fun setUpSecugen() {
        sgfplib = JSGFPLib(this, getSystemService(USB_SERVICE) as UsbManager)
        mPermissionIntent = PendingIntent.getBroadcast(
            this, 0,
            Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_MUTABLE //0
        );
        filter = IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
    }

    private val mUsbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    val device =
                        intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            Log.d(
                                "EmvActivity.TAG",
                                """
                                Vendor ID : ${device.vendorId}
                                
                                """.trimIndent()
                            )
                            Log.d(
                                "EmvActivity.TAG",
                                """
                                Product ID: ${device.productId}
                                
                                """.trimIndent()
                            )
                        } else Log.e(
                            "EmvActivity.TAG",
                            "mUsbReceiver.onReceive() Device is null"
                        )
                    } else Log.e(
                        "EmvActivity.TAG",
                        "mUsbReceiver.onReceive() permission denied for device $device"
                    )
                }
            }
        }
    }


}