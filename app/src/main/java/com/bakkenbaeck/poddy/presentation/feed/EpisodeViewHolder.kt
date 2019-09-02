package com.bakkenbaeck.poddy.presentation.feed

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bakkenbaeck.poddy.extensions.toDate
import com.bakkenbaeck.poddy.extensions.toSeconds
import com.bakkenbaeck.poddy.model.EpisodeItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.episode_item.*

class EpisodeViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun setEpisode(item: EpisodeItem) {
        date.text = item.pub_date_ms.toDate()
        name.text = item.title
        length.text = "${item.audio_length_sec.toSeconds()} min"
    }

    fun setOnItemClickListener(item: EpisodeItem, onItemClickListener: (EpisodeItem) -> Unit) {
        containerView.setOnClickListener { onItemClickListener(item) }
    }
}
