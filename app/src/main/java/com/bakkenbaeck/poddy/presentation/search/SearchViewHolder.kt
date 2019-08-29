package com.bakkenbaeck.poddy.presentation.search

import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.RoundedCornersTransformation
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.getDimen
import com.bakkenbaeck.poddy.model.SearchItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.search_item.*

class SearchViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    private val radius by lazy { containerView.getDimen(R.dimen.radius_small) }
    private val roundedCorners by lazy { RoundedCornersTransformation(radius) }

    fun setSearchItem(searchItem: SearchItem) {
        showName.text = searchItem.collectionName
        author.text = searchItem.artistName
        image.load(searchItem.artworkUrl600) {
            crossfade(true)
            transformations(roundedCorners)
        }
    }

    fun setOnItemClickedListener(searchItem: SearchItem, onItemClickListener: (SearchItem) -> Unit) {
        containerView.setOnClickListener { onItemClickListener(searchItem) }
    }
}