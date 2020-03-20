package com.bakkenbaeck.poddy.presentation.custom

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getColor
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.getColorById
import com.bakkenbaeck.poddy.extensions.getColorFromAttr
import kotlinx.android.synthetic.main.view_toolbar.view.*

class Toolbar : ConstraintLayout {

    private var title: String = ""
    private var showBackArrow = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        parseAttributes(attrs ?: return)
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        parseAttributes(attrs ?: return)
        init()
    }

    private fun parseAttributes(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.Toolbar)
        title = a.getString(R.styleable.Toolbar_titleText).orEmpty()
        showBackArrow = a.getBoolean(R.styleable.Toolbar_showBackArrow, false)
        a.recycle()
    }

    private fun init() {
        inflate(context, R.layout.view_toolbar, this)
        initBackArrow()
        initTitle()
        setBackgroundColor(getColorFromAttr(R.attr.colorPrimary))
    }

    private fun initBackArrow() {
        backButton.visibility = if (showBackArrow) VISIBLE else GONE
    }

    private fun initTitle() {
        titleView.text = title
    }

    fun setOnBackClickedListener(action: () -> Unit) {
        backButton.setOnClickListener { action() }
    }

    fun setText(text: String) {
        titleView.text = text
    }

    fun setTextSize(size: Float) {
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
    }
}
