package com.bakkenbaeck.poddy.extensions

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun View.getDimen(@DimenRes resource: Int): Float {
    return this.resources.getDimension(resource)
}

fun View.layoutInflater(): LayoutInflater {
    return LayoutInflater.from(this.context)
}

fun View.getPxSize(@DimenRes id: Int) = resources.getDimensionPixelSize(id)

fun View.getColorById(@ColorRes id: Int) = context.getColorById(id)

fun View.getString(@StringRes id: Int): String = context.getString(id)

fun View.getString(@StringRes resId: Int, vararg formatArgs: Any): String {
    return context.getString(resId, *formatArgs)
}

fun View.addPadding(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
    setPadding(left, top, right, bottom)
}

fun View.getDrawableById(@DrawableRes id: Int) = AppCompatResources.getDrawable(context, id)

fun View.isVisible(bool: Boolean?, nonVisibleState: Int = View.GONE) {
    visibility = if (bool == true) View.VISIBLE else nonVisibleState
}

fun View.isVisible(): Boolean {
    return visibility == View.VISIBLE
}

fun RecyclerView.findLastCompletelyVisibleItemPosition(): Int {
    val layoutManager = layoutManager as? LinearLayoutManager ?: return -1
    return layoutManager.findLastCompletelyVisibleItemPosition()
}

fun RecyclerView.isLastVisible(): Boolean {
    val layoutManager = layoutManager as? LinearLayoutManager ?: return false
    val pos = layoutManager.findLastCompletelyVisibleItemPosition()
    val numItems = adapter?.itemCount ?: return false
    return pos >= numItems - 1
}
