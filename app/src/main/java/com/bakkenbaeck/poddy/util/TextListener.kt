package com.bakkenbaeck.poddy.util

import android.text.Editable
import android.text.TextWatcher

abstract class TextListener : TextWatcher {
    override fun afterTextChanged(p0: Editable?) {}
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        onTextChanged(p0.toString())
    }

    abstract fun onTextChanged(value: String)
}
