package com.bakkenbaeck.poddy.presentation.podcast

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.podcast_filter_item.*

class PodcastFilterViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(podcastFilter: PodcastFilter) {
        name.text = podcastFilter.name
        selected.isVisible = podcastFilter.isSelected
    }

    fun setOnClickListener(filter: PodcastFilter, cb: (PodcastFilter) -> Unit) {
        containerView.setOnClickListener { cb(filter) }
    }
}