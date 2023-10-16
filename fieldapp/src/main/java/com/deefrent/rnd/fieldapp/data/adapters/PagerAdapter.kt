package com.deefrent.rnd.fieldapp.data.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.deefrent.rnd.fieldapp.view.homepage.customerassessment.assessment.IncompleteAssesmentFragment

class PagerAdapter(fragmentActivity: IncompleteAssesmentFragment) : FragmentStateAdapter(fragmentActivity) {

    private val mFragmentList: ArrayList<Fragment> = ArrayList()

    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getItemCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment: Fragment) {
        mFragmentList.add(fragment)
    }
}