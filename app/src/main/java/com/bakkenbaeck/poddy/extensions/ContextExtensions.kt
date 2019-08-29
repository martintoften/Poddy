package com.bakkenbaeck.poddy.extensions

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun Context.getColorById(@ColorRes id: Int) = ContextCompat.getColor(this, id)
