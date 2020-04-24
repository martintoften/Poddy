package com.bakkenbaeck.poddy.util

import android.graphics.Color
import java.util.Random

fun generateColor(): Int {
    val random = Random()
    val nextInt: Int = random.nextInt(0xffffff + 1)
    val colorString = String.format("#%06x", nextInt)
    return Color.parseColor(colorString)
}