package com.bakkenbaeck.poddy.presentation.custom

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.getColorById
import com.bakkenbaeck.poddy.extensions.isVisible
import kotlinx.android.synthetic.main.view_spinner.view.*

class SpinnerOverlay : FrameLayout {

    private var overlayColor: Int? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        parseAttributes(attrs ?: return)
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int): super(context, attrs, defStyle) {
        parseAttributes(attrs ?: return)
        init()
    }

    private fun parseAttributes(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SpinnerOverlayLayout)
        overlayColor = a.getColor(
                R.styleable.SpinnerOverlayLayout_overlayColor,
                getColorById(R.color.semi_transparent_overlay)
        )
        a.recycle()
    }

    private fun init() {
        inflate(context, R.layout.view_spinner, this)
        setBackgroundColor(this.overlayColor ?: getColorById(R.color.semi_transparent_overlay))
        visibility = View.GONE
        isClickable = true
        isFocusable = true
    }

    fun isOverlayVisible(isVisible: Boolean) = isVisible(isVisible)

    fun isSpinnerVisible(isVisible: Boolean) = loadingSpinner.isVisible(isVisible)

    fun setSpinnerColor(@ColorRes id: Int) {
        loadingSpinner.indeterminateDrawable.setColorFilter(
                getColorById(id),
                PorterDuff.Mode.SRC_ATOP
        )
    }
}
