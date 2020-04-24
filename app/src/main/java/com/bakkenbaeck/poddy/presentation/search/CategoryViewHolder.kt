package com.bakkenbaeck.poddy.presentation.search

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.category_item.*

class CategoryViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun setItem(podcast: ViewPodcast) {
        image.apply {
            transitionName = podcast.id
            load(podcast.image) {
                crossfade(400)
            }
        }
    }

    fun setOnItemClickedListener(
        podcast: ViewPodcast,
        listener: (View, ViewPodcast) -> Unit
    ) {
        containerView.setOnClickListener { listener(image, podcast) }
    }
}