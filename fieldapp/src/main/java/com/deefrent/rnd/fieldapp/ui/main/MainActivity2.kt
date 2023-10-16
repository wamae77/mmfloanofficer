package com.deefrent.rnd.fieldapp.ui.main

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.deefrent.rnd.fieldapp.databinding.ActivityMainBinding

import com.deefrent.rnd.fieldapp.utils.ConnectionLiveData
import com.google.android.material.snackbar.Snackbar

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var offlineSnackBar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //val file= getFileFromInternalStorage(Constants.IMAGES_DIR,"field_app_20210903T161608.png")
        //Log.d("TAG", "onCreate: $file")
        val connectionLiveData = ConnectionLiveData(this@MainActivity2)

        connectionLiveData.observe(this) { isConnected ->
            offlineSnackBar = Snackbar.make(
                findViewById(R.id.content),
                "You are offline",
                Snackbar.LENGTH_LONG
            )

            offlineSnackBar.setAction("DISMISS") { // Call your action method here
                offlineSnackBar.dismiss()
            }
            isConnected?.let {
                if (!it) {
                    //offlineSnackBar.show()
                }
            }
        }

    }

}

