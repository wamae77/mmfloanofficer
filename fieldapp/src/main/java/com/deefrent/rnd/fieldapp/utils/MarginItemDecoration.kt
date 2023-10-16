package com.deefrent.rnd.fieldapp.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Tom Munyiri on 17/07/2022
 * Email: munyiri.thomas@electics.io
 * Company: Eclectics International Ltd
 */
class MarginItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                top = 0
            }
            left =  0
            right = 0
            bottom = spaceHeight
        }
    }
}