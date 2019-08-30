package com.bakkenbaeck.poddy.presentation.podcast

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.podcast_item.*
import org.db.Podcast

class PodcastViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun setPodcast(podcast: Podcast) {
        image.load(podcast.image)
    }
}
