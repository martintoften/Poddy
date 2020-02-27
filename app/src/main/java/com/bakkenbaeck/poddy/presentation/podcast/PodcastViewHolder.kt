package com.bakkenbaeck.poddy.presentation.podcast

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.podcast_item.*

class PodcastViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun setPodcast(podcast: ViewPodcast) {
        image.apply {
            transitionName = podcast.id
            load(podcast.image)
        }
    }

    fun setOnItemClickedListener(podcast: ViewPodcast, listener: (View, ViewPodcast) -> Unit) {
        containerView.setOnClickListener { listener(image, podcast) }
    }
}
