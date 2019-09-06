package com.bakkenbaeck.poddy.extensions

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController

fun Fragment.navigate(id: Int, args: Bundle? = null) {
    findNavController().navigate(id, args)
}

fun Fragment.navigate(directions: NavDirections, navOptions: NavOptions? = null) {
    findNavController().navigate(directions, navOptions)
}

fun Fragment.pop(): Boolean {
    return findNavController().popBackStack()
}

fun Fragment.getDimen(@DimenRes resource: Int): Float {
    return resources.getDimension(resource)
}

fun Fragment.getDownloadService(): DownloadManager? {
    return activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
}

fun Fragment.startForegroundService(intent: Intent) {
    val context = context ?: return
    ContextCompat.startForegroundService(context, intent)
}
