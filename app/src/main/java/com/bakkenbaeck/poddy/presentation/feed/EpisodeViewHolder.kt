package com.bakkenbaeck.poddy.presentation.feed

import android.view.View
import com.bakkenbaeck.poddy.extensions.toDate
import com.bakkenbaeck.poddy.extensions.toSeconds
import com.bakkenbaeck.poddy.model.EpisodeItem
import com.bakkenbaeck.poddy.presentation.custom.ClickableViewHolder
import kotlinx.android.synthetic.main.episode_item.*

class EpisodeViewHolder(override val containerView: View) : ClickableViewHolder<EpisodeItem>(containerView) {
    fun setEpisode(item: EpisodeItem) {
        date.text = item.pub_date_ms.toDate()
        name.text = item.title
        length.text = "${item.audio_length_sec.toSeconds()} min"
    }


}
