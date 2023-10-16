package com.deefrent.rnd.jiboostfieldapp.ui.main

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.jiboostfieldapp.di.helpers.activities.ActivityHelperKt
import com.deefrent.rnd.jiboostfieldapp.di.helpers.activities.AddressableActivity
import com.deefrent.rnd.jiboostfieldapp.di.helpers.features.FeatureModule
import com.deefrent.rnd.jiboostfieldapp.di.helpers.features.Modules
import com.deefrent.rnd.jiboostfieldapp.databinding.FragmentMainBinding
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainFragment : DaggerFragment() {

    private lateinit var binding: FragmentMainBinding

    companion object {
        fun newInstance() = MainFragment()
    }

    private var mApp: BaseApp? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var myContext: Context

    private val mViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
    }

    private val module by lazy {
        Modules.FeatureFieldApp.INSTANCE
    }

    private val splitInstallManager: SplitInstallManager by lazy {
        SplitInstallManagerFactory.create(requireActivity())
    }

    private val listener = SplitInstallStateUpdatedListener { state ->
        when (state.status()) {
            SplitInstallSessionStatus.DOWNLOADING -> {
                setStatus("DOWNLOADING")
                //display UI to show downloading progress
            }

            SplitInstallSessionStatus.INSTALLING -> {
                setStatus("INSTALLING")
            }

            SplitInstallSessionStatus.INSTALLED -> {
                //change this to show UI

                // Enable module immediately
                activity?.let { SplitCompat.install(it) }

                setStatus("${module.name} already installed")
                //
                //binding.startButton.visibility = View.VISIBLE
                /*binding.startButton.setOnClickListener{
                    showFeatureModule(module)
                }*/
                /*Handler().postDelayed({
                    showFeatureModule(module)
                    requireActivity().finish()
                }, 3000)*/
                GlobalScope.launch {
                    openFeatureModule()
                }
            }

            SplitInstallSessionStatus.FAILED -> {
                setStatus("FAILED")
                //show user that install has failed
            }
        }
    }

    private suspend fun openFeatureModule() {
        delay(1000L)
        showFeatureModule(module)
        requireActivity().finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //
        mApp = activity?.application as BaseApp
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        //
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val data = mViewModel.getData()

        Log.i("MainFragment", "=> $data")

        if (splitInstallManager.installedModules.contains(module.toString())) {
            showFeatureModule(module)
            return
        }

        val request = SplitInstallRequest
            .newBuilder()
            .addModule(module.name)
            .build()

        splitInstallManager.startInstall(request)
        setStatus("Start install for ${module.name}")
    }

    override fun onResume() {
        super.onResume()
        splitInstallManager.registerListener(listener)
    }

    override fun onPause() {
        splitInstallManager.unregisterListener(listener)
        super.onPause()
    }

    private fun setStatus(label: String) {
        //binding.status.text = label
        //Toast.makeText(context,label,Toast.LENGTH_LONG).show()
        Log.d("Main Fragment", "setStatus: $label")
    }

    /**
     *
     */
    private fun showFeatureModule(module: FeatureModule) //register field app innjector in dagger
    {
        try {
            //Inject
            mApp!!.addModuleInjector(module)
            //
            //start entry class for the dynamic module
            this.startActivity(
                ActivityHelperKt.intentTo(
                    requireActivity(),
                    module as AddressableActivity
                )
            )
            requireActivity().finish();
        } catch (e: Exception) {
            e.message?.let { Log.d("MainFragment", it) };
        }
    }
}