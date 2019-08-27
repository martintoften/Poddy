package com.bakkenbaeck.poddy.extensions

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

inline fun <reified T : ViewModel> FragmentActivity.getViewModel(): T {
    return ViewModelProviders.of(this)[T::class.java]
}

inline fun <reified T> Activity.startActivity() {
    startActivity(Intent(this, T::class.java))
}

inline fun <reified T> Activity.startActivity(func: Intent.() -> Intent) {
    startActivity(Intent(this, T::class.java).func())
}
