package com.bakkenbaeck.poddy.presentation.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.loadWithRoundCorners
import kotlinx.android.synthetic.main.view_small_player.view.*

class SmallPlayer : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    private fun init() {
        inflate(context, R.layout.view_small_player, this)
    }

    fun setThumbnail(url: String) {
        thumbnail.loadWithRoundCorners(url, R.dimen.radius_small)
    }

    fun setOnRewindListener(cb: () -> Unit) {
        smallBack.setOnClickListener { cb() }
    }

    fun setOnFastForwardListener(cb: () -> Unit) {
        smallForward.setOnClickListener { cb() }
    }

    fun setOnPlayListener(cb: () -> Unit) {
        playSmall.setOnClickListener { cb() }
    }

    fun setPlayImageResource(@DrawableRes drawable: Int) {
        playSmall.setImageResource(drawable)
    }

    fun getPlayDrawable(): Drawable? = playSmall.drawable
}