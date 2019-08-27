package com.bakkenbaeck.poddy.extensions

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DimenRes

fun View.getDimen(@DimenRes resource: Int): Float {
    return this.resources.getDimension(resource)
}

fun View.layoutInflater(): LayoutInflater {
    return LayoutInflater.from(this.context)
}
