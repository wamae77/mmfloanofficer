package com.deefrent.rnd.common.abstractions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.deefrent.rnd.common.utils.snackBarCustom
import com.google.android.play.core.splitcompat.SplitCompat
import dagger.android.support.DaggerAppCompatActivity
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Bourne Koloh on 16 February,2021.
 * Eclectics International, Products and R&D
 * PROJECT: Dynamic App Demo
 */
abstract class CommonBaseActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    public override fun onCreate(savedInstanceState: Bundle?) {
        //AndroidInjection.inject(this);
        super.onCreate(savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        //
        /* if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // If the permission is not authorized in the first time. A new permission access
            // request will be created.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_PERMISSION);
            }
        }*/
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        // Emulates installation of on demand modules using SplitCompat.
        SplitCompat.installActivity(this)
    }

    companion object {
        const val WRITE_PERMISSION = 0
        val CAPTURE_CAMERA_CODE = 200

    }

    fun onClickRequestOpenCameraPermission(listener: (() -> Unit)? = null) {

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.e("PERMISSION : ", "Granted")
                listener?.invoke()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAPTURE_CAMERA_CODE
                )
                /* requestPermissionLauncher.launch(
                     Manifest.permission.CAMERA
                 )*/
            }

            else -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAPTURE_CAMERA_CODE
                )
                /* requestPermissionLauncher.launch(
                     Manifest.permission.CAMERA
                 )*/
            }
        }
    }

    fun openCameraForImageCapturing(code: Int) {
        try {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                startActivityForResult(this, code)
            }
        } catch (e: Exception) {
            snackBarCustom("Camera Permission Denial")
            Timber.d("--------------------------------------------------------\n\n\n\n\n--------openCameraForPickingImage ---------------------------------------------$e")
        }
    }
}