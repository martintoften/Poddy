package com.bakkenbaeck.poddy.extensions

import android.view.View
import androidx.annotation.DimenRes

fun View.getDimen(@DimenRes resource: Int): Float {
    return this.resources.getDimension(resource)
}
