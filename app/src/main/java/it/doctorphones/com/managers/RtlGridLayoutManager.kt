package it.doctorphones.com.managers

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager

class RtlGridLayoutManager(
    context: Context?, spanCount: Int
) : GridLayoutManager(context, spanCount) {

    override fun isLayoutRTL(): Boolean {
        return true
    }
}