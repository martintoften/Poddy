package com.bakkenbaeck.poddy.presentation.search

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.RoundedCornersTransformation
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.getDimen
import com.bakkenbaeck.poddy.model.SearchItem
import com.bakkenbaeck.poddy.presentation.custom.ClickableViewHolder
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.search_item.*

class SearchViewHolder(override val containerView: View) : ClickableViewHolder<SearchItem>(containerView) {

    private val radius by lazy { containerView.getDimen(R.dimen.radius_small) }
    private val roundedCorners by lazy { RoundedCornersTransformation(radius) }

    fun setSearchItem(searchItem: SearchItem) {
        showName.text = searchItem.title_original
        author.text = searchItem.publisher_original
        image.load(searchItem.image) {
            crossfade(true)
            transformations(roundedCorners)
        }
    }
}
