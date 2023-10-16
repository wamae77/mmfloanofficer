package com.deefrent.rnd.fieldapp.view.quickreports

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentQuickReportsBinding
import com.deefrent.rnd.fieldapp.utils.base_fragment.BaseMoneyMartBindedFragment
import com.deefrent.rnd.fieldapp.utils.setToolbarTitle


class QuickReportsFragment : BaseMoneyMartBindedFragment<FragmentQuickReportsBinding>(
    FragmentQuickReportsBinding::inflate
) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // findNavController().navigateUp()
                    findNavController().popBackStack(R.id.quickReportsFragment, true)
                    findNavController().navigate(R.id.dashboardFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        arguments?.let {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle(
            toolBarBinding = binding.toolBar,
            activity = requireActivity(),
            title = getString(R.string.quick_reports),
            action = {
                findNavController().popBackStack(R.id.quickReportsFragment, true)
                findNavController().navigate(R.id.dashboardFragment)
            }
        )
    }


}