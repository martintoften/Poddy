package com.bakkenbaeck.poddy.presentation.custom

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

class SimpleViewPager constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewPager(context, attrs) {
    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        super.setCurrentItem(item, false)
    }

    override fun setCurrentItem(item: Int) {
        super.setCurrentItem(item, false)
    }
}
