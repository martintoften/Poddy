package com.bakkenbaeck.poddy.extensions

import android.content.Context
import android.util.TypedValue
import android.view.ViewGroup
import androidx.annotation.AttrRes

fun ViewGroup.getColorFromAttr(
    @AttrRes attrColor: Int,
    resolveRefs: Boolean = true
): Int {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}
