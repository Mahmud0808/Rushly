package com.drdisagree.rushly.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalItemDecoration(private val amount: Int? = 15) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        if (parent.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            outRect.left = amount!!
        } else {
            outRect.right = amount!!
        }
    }
}