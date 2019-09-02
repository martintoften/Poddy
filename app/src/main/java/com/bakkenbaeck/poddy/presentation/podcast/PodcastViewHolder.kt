package com.bakkenbaeck.poddy.presentation.podcast

import android.view.View
import coil.api.load
import com.bakkenbaeck.poddy.presentation.custom.ClickableViewHolder
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import kotlinx.android.synthetic.main.podcast_item.*

class PodcastViewHolder(override val containerView: View) : ClickableViewHolder<ViewPodcast>(containerView) {
    fun setPodcast(podcast: ViewPodcast) {
        image.load(podcast.image)
    }
}
