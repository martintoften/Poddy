package com.bakkenbaeck.poddy.presentation.custom

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginLeft
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.clearDrawables
import com.bakkenbaeck.poddy.extensions.dpToPx
import com.bakkenbaeck.poddy.extensions.getColorFromAttr
import com.bakkenbaeck.poddy.extensions.setRightDrawable
import com.bakkenbaeck.poddy.util.TextListener
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.view_toolbar.view.*

class Toolbar : ConstraintLayout {

    private var title: String = ""
    private var showBackArrow = false
    private var showInput = false
    private var showMore = false

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
        showInput = a.getBoolean(R.styleable.Toolbar_showInput, false)
        showMore = a.getBoolean(R.styleable.Toolbar_showMore, false)
        a.recycle()
    }

    private fun init() {
        inflate(context, R.layout.view_toolbar, this)
        initBackArrow()
        initTitle()
        initText()
        initMore()
        setBackgroundColor(getColorFromAttr(R.attr.colorPrimary))
        initArrowAnimation()
    }

    private fun initBackArrow() {
        backButton.visibility = if (showBackArrow) VISIBLE else GONE
    }

    private fun initTitle() {
        titleView.text = title
    }

    private fun initText() {
        if (showInput) {
            input.visibility = View.VISIBLE
            titleView.visibility = View.GONE
        } else {
            input.visibility = View.GONE
            titleView.visibility = View.VISIBLE
        }
    }

    private fun initMore() {
        if (showMore) {
            moreButton.visibility = View.VISIBLE
        } else {
            moreButton.visibility = View.GONE
        }
    }

    fun setOnBackClickedListener(action: () -> Unit) {
        backButton.setOnClickListener { action() }
    }

    fun setText(text: String) {
        titleView.text = text
        initTitleAnimation()
    }

    fun setTextSize(size: Float) {
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
    }

    fun addTextChangedListener(cb: (String) -> Unit) {
        input.addTextChangedListener(object : TextListener() {
            override fun onTextChanged(value: String) {
                if (!input.text.isNullOrEmpty()) {
                    input.setRightDrawable(R.drawable.ic_clear)
                } else {
                    input.clearDrawables()
                }
                cb(value)
            }
        })
    }

    fun setOnClearClickedListener(cb: (TextInputEditText) -> Unit) {
        input.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP && !input.text.isNullOrEmpty()) {
                if (event.rawX >= input.right - input.totalPaddingRight) {
                    cb(input)
                    return@OnTouchListener true
                }
            }
            return@OnTouchListener false
        })
    }

    fun setOnMoreClickedListener(cb: () -> Unit) {
        moreButton.setOnClickListener { cb() }
    }

    private fun initTitleAnimation() {
        if (titleView.text.isEmpty()) return

        titleView.alpha = 0f
        titleView.animate()
            .setInterpolator(FastOutSlowInInterpolator())
            .alpha(1f)
            .setDuration(600)
            .start()
    }

    private fun initArrowAnimation() {
        if (!showBackArrow) return

        val startPosition = dpToPx(24f) + backButton.marginLeft
        backButton.x = -startPosition
        backButton.animate()
            .setInterpolator(FastOutSlowInInterpolator())
            .setDuration(600)
            .translationX(0f)
            .start()
    }
}
