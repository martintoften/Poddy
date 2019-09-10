package com.bakkenbaeck.poddy.presentation.feed

import android.view.View
import coil.api.load
import coil.transform.RoundedCornersTransformation
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.getDimen
import com.bakkenbaeck.poddy.extensions.getString
import com.bakkenbaeck.poddy.extensions.loadWithRoundCorners
import com.bakkenbaeck.poddy.presentation.custom.ClickableViewHolder
import kotlinx.android.synthetic.main.episode_item_header.*

class EpisodeHeaderViewHolder(override val containerView: View) : ClickableViewHolder<Header>(containerView) {
    fun setHeader(header: Header) {
        title.text = header.title

        if (header.hasSubscribed) {
            subscribe.setBackgroundResource(R.drawable.ic_unsubscribe_background)
            subscribe.text = containerView.getString(R.string.unsubscribe)
        } else {
            subscribe.setBackgroundResource(R.drawable.ic_subscribe_background)
            subscribe.text = containerView.getString(R.string.subscribe)
        }

        image.loadWithRoundCorners(header.image)
    }

    fun setOnSubscribedClicked(header: Header, onItemClickedListener: (Header) -> Unit) {
        subscribe.setOnClickListener { onItemClickedListener(header) }
    }
}
