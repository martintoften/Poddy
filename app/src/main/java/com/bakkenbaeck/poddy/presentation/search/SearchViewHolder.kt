package com.bakkenbaeck.poddy.presentation.search

import android.view.View
import coil.api.load
import coil.transform.RoundedCornersTransformation
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.getDimen
import com.bakkenbaeck.poddy.presentation.custom.ClickableViewHolder
import com.bakkenbaeck.poddy.presentation.model.ViewPodcastSearchItem
import kotlinx.android.synthetic.main.search_item.*

class SearchViewHolder(override val containerView: View) : ClickableViewHolder<ViewPodcastSearchItem>(containerView) {

    private val radius by lazy { containerView.getDimen(R.dimen.radius_small) }
    private val roundedCorners by lazy { RoundedCornersTransformation(radius) }

    fun setSearchItem(searchItem: ViewPodcastSearchItem) {
        showName.text = searchItem.title
        author.text = searchItem.publisher
        image.load(searchItem.image) {
            crossfade(true)
            transformations(roundedCorners)
        }
    }
}
