package com.deefrent.rnd.fieldapp.ui.assetManagement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.deefrent.rnd.fieldapp.databinding.FragmentAssetManagementBinding
import com.google.android.material.tabs.TabLayoutMediator

private const val NUM_TABS = 2
val tabsArray = arrayOf("Add Asset", "Asset Log")

class AssetManagementFragment : Fragment() {
    private var _binding: FragmentAssetManagementBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAssetManagementBinding.inflate(inflater, container, false)
        val view = binding.root

        //set up tabs
        val adapter = ViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabsArray[position]
        }.attach()
        return view
    }

    class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun getItemCount(): Int {
            return NUM_TABS
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> AddAssetFragment()
                else -> AssetLogFragment()
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AssetManagementFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}