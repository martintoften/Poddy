package com.bakkenbaeck.poddy.presentation.search

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bakkenbaeck.poddy.presentation.model.ViewCategory
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.category_list.*

class CategoryListViewHolder(
    private val viewPool: RecyclerView.RecycledViewPool,
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun setItem(category: ViewCategory, listener: (View, ViewPodcast) -> Unit) {
        text.text = category.categoryName
        list.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            setRecycledViewPool(viewPool)
            adapter = CategoryAdapter(listener).apply {
                setItems(category.podcasts)
            }
        }
    }
}