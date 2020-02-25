package com.bakkenbaeck.poddy.presentation.search

import android.view.View
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.loadWithRoundCorners
import com.bakkenbaeck.poddy.presentation.custom.ClickableViewHolder
import com.bakkenbaeck.poddy.presentation.model.ViewPodcastSearchItem
import kotlinx.android.synthetic.main.search_item.*

class SearchViewHolder(override val containerView: View) : ClickableViewHolder<ViewPodcastSearchItem>(containerView) {
    fun setSearchItem(searchItem: ViewPodcastSearchItem) {
        showName.text = searchItem.title
        author.text = searchItem.publisher
        image.loadWithRoundCorners(searchItem.image, R.dimen.radius_small)
    }
}
