package com.deefrent.rnd.fieldapp.ui.onboardAccount

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.deefrent.rnd.fieldapp.R


class FragmentAddViewpg(var context: Context):PagerAdapter() {
    val pageCount=3
    var layouts:kotlin.IntArray? = intArrayOf(R.layout.layout_ad,R.layout.layout_ad,R.layout.layout_ad)


    private val layoutInflater: LayoutInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    // // Returns the number of pages/items to be displayed in the ViewPager.
    override fun getCount(): Int {
        return layouts!!.size

    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layout =
            layoutInflater.inflate(layouts!![position], container, false) as ViewGroup

        container.addView(layout)
        return layout

    }
    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) {

    }


}