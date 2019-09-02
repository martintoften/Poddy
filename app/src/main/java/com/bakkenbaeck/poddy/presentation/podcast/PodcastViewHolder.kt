package com.bakkenbaeck.poddy.presentation.podcast

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.bakkenbaeck.poddy.presentation.custom.ClickableViewHolder
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.podcast_item.*
import org.db.Podcast

class PodcastViewHolder(override val containerView: View) : ClickableViewHolder<Podcast>(containerView) {
    fun setPodcast(podcast: Podcast) {
        image.load(podcast.image)
    }
}
