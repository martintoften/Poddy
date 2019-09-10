package com.bakkenbaeck.poddy.extensions

import android.widget.ImageView
import androidx.annotation.DimenRes
import coil.api.load
import coil.transform.RoundedCornersTransformation
import com.bakkenbaeck.poddy.R

fun ImageView.loadWithRoundCorners(url: String?, @DimenRes radius: Int = R.dimen.radius_default) {
    val radius = getDimen(radius)
    val roundedCorners = RoundedCornersTransformation(radius)
    load(url) { transformations(roundedCorners) }
}
