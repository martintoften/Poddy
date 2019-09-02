package com.bakkenbaeck.poddy.presentation.feed

import android.view.View
import coil.api.load
import coil.transform.RoundedCornersTransformation
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.getDimen
import com.bakkenbaeck.poddy.presentation.custom.ClickableViewHolder
import kotlinx.android.synthetic.main.episode_item_header.*

class EpisodeHeaderViewHolder(override val containerView: View) : ClickableViewHolder<Header>(containerView) {
    private val radius by lazy { containerView.getDimen(R.dimen.radius_default) }
    private val roundedCorners by lazy { RoundedCornersTransformation(radius) }

    fun setHeader(header: Header) {
        title.text = header.title
        image.load(header.image) {
            crossfade(true)
            transformations(roundedCorners)
        }
    }
}
