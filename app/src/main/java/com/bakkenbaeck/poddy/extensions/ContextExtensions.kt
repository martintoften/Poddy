package com.bakkenbaeck.poddy.extensions

import android.content.Context
import android.util.DisplayMetrics
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import java.io.File

fun Context.getColorById(@ColorRes id: Int) = ContextCompat.getColor(this, id)

fun Context.getPodcastDir(): File? = filesDir

fun Context.dpToPx(@DimenRes dimenRes: Int): Int {
    return resources.getDimensionPixelSize(dimenRes)
}

fun Context.dpToPxAsFloat(@DimenRes dimenRes: Int): Float {
    return resources.getDimensionPixelSize(dimenRes).toFloat()
}

fun Context.dpToPx(dp: Float): Float {
    return dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Context.pxToDp(px: Float): Float {
    return px / (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}
