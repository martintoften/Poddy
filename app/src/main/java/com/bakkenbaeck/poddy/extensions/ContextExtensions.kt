package com.bakkenbaeck.poddy.extensions

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import java.io.File

fun Context.getColorById(@ColorRes id: Int) = ContextCompat.getColor(this, id)

fun Context.getPodcastDir(): File? = filesDir
