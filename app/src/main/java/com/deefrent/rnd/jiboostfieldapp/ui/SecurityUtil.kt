package com.deefrent.rnd.jiboostfieldapp.ui

import android.app.Activity
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.view.WindowManager

fun isSIMInserted(context: Context): Boolean {
    return TelephonyManager.SIM_STATE_ABSENT != (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simState
}

fun isEmulator(): Boolean {
    val model = Build.MODEL
    val product = Build.PRODUCT
    return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
            || Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.HARDWARE.contains("goldfish")
            || Build.HARDWARE.contains("ranchu")
            || Build.HARDWARE.contains("nox")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("SM-G950U1")
            || Build.MODEL.contains("Android SDK built for x86")
            || Build.MANUFACTURER.contains("Genymotion")
            || Build.PRODUCT.contains("sdk_google")
            || Build.PRODUCT.contains("google_sdk")
            || Build.PRODUCT.contains("sdk")
            || Build.PRODUCT.contains("sdk_x86")
            || Build.PRODUCT.contains("vbox86p")
            || Build.PRODUCT.contains("emulator")
            || Build.PRODUCT.contains("dreamqlteue")
            || Build.PRODUCT.contains("simulator"))
}

fun preventScreenShotCapture(activity: Activity) {
    activity.window.setFlags(
        WindowManager.LayoutParams.FLAG_SECURE,
        WindowManager.LayoutParams.FLAG_SECURE
    )
}
