package com.bakkenbaeck.poddy.extensions

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import java.io.File


fun Fragment.navigate(
    id: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    extras: FragmentNavigator.Extras? = null
) {
    findNavController().navigate(id, args, navOptions, extras)
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

fun Fragment.getPodcastDir(): File? = context?.filesDir

inline fun <reified T> Fragment.startForegroundService(intent: Intent.() -> Intent) {
    val context = context ?: return
    ContextCompat.startForegroundService(context, Intent(activity, T::class.java).intent())
}

fun Fragment.getScreenHeight(): Int {
    return Resources.getSystem().displayMetrics.heightPixels
}

fun Fragment.getColorById(@ColorRes id: Int) = context?.getColorById(id) ?: 0

fun Fragment.dpToPx(@DimenRes dimen: Int): Float {
    return requireContext().resources.getDimensionPixelSize(dimen).toFloat()
}

fun Fragment.dpToPx(dp: Float): Float {
    return requireContext().dpToPx(dp)
}

fun Fragment.pxToDp(px: Float): Float {
    return requireContext().pxToDp(px)
}

fun Fragment.hideKeyboard() {
    val imm: InputMethodManager =
        activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = activity?.currentFocus
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}