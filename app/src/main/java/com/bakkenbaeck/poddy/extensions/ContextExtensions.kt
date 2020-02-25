package com.bakkenbaeck.poddy.extensions

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import java.io.File

fun Context.getColorById(@ColorRes id: Int) = ContextCompat.getColor(this, id)

fun Context.getPodcastDir(): File? = filesDir

fun Context.dpToPx(@DimenRes dimenRes: Int): Int {
    return resources.getDimensionPixelSize(dimenRes)
}
