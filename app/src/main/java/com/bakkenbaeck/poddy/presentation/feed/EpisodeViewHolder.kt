package com.bakkenbaeck.poddy.presentation.feed

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bakkenbaeck.poddy.model.Channel
import com.bakkenbaeck.poddy.util.OUTPUT_DATE_FORMAT
import com.bakkenbaeck.poddy.util.parseDateString
import com.bakkenbaeck.poddy.util.parseSecondsToMinutes
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.episode_item.*

class EpisodeViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun setEpisode(item: Channel.Item) {
        date.text = parseDateString(item.pubDate, OUTPUT_DATE_FORMAT)
        name.text = item.title
        length.text = parseSecondsToMinutes(item.duration)
    }

    fun setOnItemClickListener(item: Channel.Item, onItemClickListener: (Channel.Item) -> Unit) {
        containerView.setOnClickListener { onItemClickListener(item) }
    }
}
