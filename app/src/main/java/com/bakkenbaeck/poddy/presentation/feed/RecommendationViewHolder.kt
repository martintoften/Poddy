package com.bakkenbaeck.poddy.presentation.feed

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import com.bakkenbaeck.poddy.util.generateColor
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.recommendation_item.*

class RecommendationViewHolder(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun setItem(podcast: ViewPodcast) {
        image.apply {
            setBackgroundColor(generateColor())
            load(podcast.image) {
                crossfade(400)
            }
        }
    }
}