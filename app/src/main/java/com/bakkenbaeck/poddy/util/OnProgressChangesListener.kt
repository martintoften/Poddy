package com.bakkenbaeck.poddy.util

import android.widget.SeekBar

abstract class OnProgressChangesListener : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekbar: SeekBar?, p1: Int, p2: Boolean) {}
    override fun onStartTrackingTouch(seekbar: SeekBar?) {}
    override fun onStopTrackingTouch(seekbar: SeekBar?) {
        onProgressChanged(seekbar?.progress ?: 0)
    }

    abstract fun onProgressChanged(progress: Int)
}
