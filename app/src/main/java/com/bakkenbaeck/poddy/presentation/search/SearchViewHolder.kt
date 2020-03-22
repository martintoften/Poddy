package com.bakkenbaeck.poddy.presentation.search

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.loadWithRoundCorners
import com.bakkenbaeck.poddy.presentation.model.ViewPodcastSearchItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.search_item.*

class SearchViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun setSearchItem(searchItem: ViewPodcastSearchItem) {
        showName.text = searchItem.title
        author.text = searchItem.publisher
        image.apply {
            transitionName = searchItem.id
            loadWithRoundCorners(searchItem.image, R.dimen.radius_small)
        }
    }

    fun setOnItemClickedListener(
        podcast: ViewPodcastSearchItem,
        listener: (View, ViewPodcastSearchItem) -> Unit
    ) {
        containerView.setOnClickListener { listener(image, podcast) }
    }
}
