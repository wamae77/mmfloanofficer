package com.deefrent.rnd.fieldapp.view.success

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deefrent.rnd.common.auth.BaseCommonDIFragment
import com.deefrent.rnd.common.databinding.FragmentCustomSuccessBinding
import com.deefrent.rnd.common.dialogs.base.adapter_detail.DetailDialogAdapter
import com.deefrent.rnd.common.dialogs.base.adapter_detail.model.DialogDetailCommon
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.common.utils.SUCCESS_DESCRIPTION
import com.deefrent.rnd.common.utils.SUCCESS_DIALOGDETAILCOMMON
import com.deefrent.rnd.common.utils.SUCCESS_TITLE
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.view.printreceipt.MoneyMartPrintServiceActivity
import com.deefrent.rnd.jiboostfieldapp.BaseApp
import com.deefrent.rnd.jiboostfieldapp.di.helpers.activities.ActivityHelperKt
import com.deefrent.rnd.jiboostfieldapp.di.helpers.activities.AddressableActivity
import com.deefrent.rnd.jiboostfieldapp.di.helpers.features.FeatureModule
import javax.inject.Inject


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class GeneralSuccessfulFragment :
    BaseCommonDIFragment<FragmentCustomSuccessBinding>(FragmentCustomSuccessBinding::inflate) {
    private var dialogDetailCommonHashSet = HashSet<DialogDetailCommon>()

    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences

    override fun onResume() {
        super.onResume()
        if (commonSharedPreferences.getIsPrintReceipt()) {
            binding.btnContinue.setText("Print receipt")
        } else {
            binding.btnContinue.setText("Done")
        }
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSuccessToolbarTitle(
            clToolBar = binding.toolBar,
            activity = requireActivity(),
            title = "Successful",
            action = {
                findNavController().popBackStack(R.id.dashboardFragment, false)
                findNavController().navigateUp()
            }
        )



        binding.btnContinue.setOnClickListener {
            if (commonSharedPreferences.getIsPrintReceipt()) {
                val intent = Intent(requireActivity(), MoneyMartPrintServiceActivity::class.java)
                startActivityForResult(intent, 234)
            } else {
                findNavController().popBackStack(R.id.dashboardFragment, false)
            }
        }
        setUpOnBackPressedCallback()
        val title = arguments?.getString(SUCCESS_TITLE)
        if (title.toString().isNotEmpty()) {
            binding.checkTitle.text = title
        }
        val description = arguments?.getString(SUCCESS_DESCRIPTION)
        if (description.toString().isNotEmpty()) {
            binding.tv1.text = description
        }
        try {
            val dialogDetailCommonList =
                arguments?.getParcelableArrayList<DialogDetailCommon>(SUCCESS_DIALOGDETAILCOMMON)
            setUpRecyclerAdapter(dialogDetailCommonList as ArrayList<DialogDetailCommon>)
        } catch (ex: Exception) {
            throw Exception("brand list cannot be null")
        }

    }

    // set up UI
    private fun setUpRecyclerAdapter(detailCommons: List<DialogDetailCommon>) {
        val dialogAdapter = DetailDialogAdapter()
        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerViewDialogContents.apply {
            layoutManager = linearLayoutManager
            dialogAdapter.submitList(detailCommons.toSet().toList())
            adapter = dialogAdapter
        }
    }

    private val mBackStackField by lazy {
        val field = NavController::class.java.getDeclaredField("mBackStack")
        field.isAccessible = true
        field
    }

    private fun popToRoot(navController: NavController) {
        val arrayDeque =
            mBackStackField.get(navController) as java.util.ArrayDeque<NavBackStackEntry>
        val graph = arrayDeque.first.destination as NavGraph
        val rootDestinationId = graph.startDestinationId

        val navOptions = NavOptions.Builder()
            .setPopUpTo(rootDestinationId, false)
            .build()

        navController.navigate(rootDestinationId, null, navOptions)
    }


    private fun setUpOnBackPressedCallback() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack(R.id.dashboardFragment, false)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
    }

    /**
     *
     */
    private fun showFeatureModule(module: FeatureModule, actionTo: String) {
        try {
            //Inject
            (requireActivity().application as BaseApp)!!.addModuleInjector(module)
            //
            var intent = ActivityHelperKt.intentTo(requireActivity(), module as AddressableActivity)
            //
            intent.action = actionTo
            startActivity(intent)
            //
            activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } catch (e: Exception) {
            e.message?.let { Log.d("AuthModule", it) };
        } finally {

        }
    }
}

fun setSuccessToolbarTitle(
    title: String,
    clToolBar: com.deefrent.rnd.common.databinding.ToolbarCustomWalletServiceLayoutBinding,
    activity: Activity,
    action: (() -> Unit)? = null
) {
    val toolBar = clToolBar
    clToolBar.toolbar.visibility = View.VISIBLE
    toolBar.toolbarTitle.text = title

    toolBar.ivBackButton.setOnClickListener {

        // activity.onBackPressed()
        if (action == null) {
            activity.onBackPressed()
        } else {
            action.invoke()
        }
    }
}