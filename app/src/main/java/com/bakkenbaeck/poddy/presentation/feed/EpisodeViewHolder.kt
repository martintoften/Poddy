package com.bakkenbaeck.poddy.presentation.feed

import android.view.View
import com.bakkenbaeck.poddy.extensions.toDate
import com.bakkenbaeck.poddy.extensions.toSeconds
import com.bakkenbaeck.poddy.network.model.EpisodeItem
import com.bakkenbaeck.poddy.presentation.custom.ClickableViewHolder
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import kotlinx.android.synthetic.main.episode_item.*

class EpisodeViewHolder(override val containerView: View) : ClickableViewHolder<ViewEpisode>(containerView) {
    fun setEpisode(item: ViewEpisode) {
        date.text = item.pubDate.toDate()
        name.text = item.title
        length.text = "${item.duration.toSeconds()} min"
    }
}
