package com.deefrent.rnd.fieldapp.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Tom Munyiri on 20/07/2022
 * Email: munyiri.thomas@electics.io
 * Company: Eclectics International Ltd
 */
class MarginGridItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                top = 0
            }
            left = 0
            right = if (parent.getChildAdapterPosition(view) % 2 == 0) spaceHeight else 0
            bottom = spaceHeight
        }
    }
}