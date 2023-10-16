package com.deefrent.rnd.fieldapp.view.feedbacks

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.deefrent.rnd.fieldapp.R
import com.deefrent.rnd.fieldapp.databinding.FragmentFeedBackBinding
import com.deefrent.rnd.fieldapp.utils.base_fragment.BaseMoneyMartBindedFragment
import com.deefrent.rnd.fieldapp.utils.setToolbarTitle


class FeedBackFragment : BaseMoneyMartBindedFragment<FragmentFeedBackBinding>(
    FragmentFeedBackBinding::inflate
) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // findNavController().navigateUp()
                    findNavController().popBackStack(R.id.feedBackFragment, true)
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
            title = getString(R.string.customer_feedback),
            action = {
                findNavController().popBackStack(R.id.feedBackFragment, true)
                findNavController().navigate(R.id.dashboardFragment)
            }
        )
    }


}