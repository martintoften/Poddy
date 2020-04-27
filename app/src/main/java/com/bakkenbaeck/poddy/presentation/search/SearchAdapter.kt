package com.bakkenbaeck.poddy.presentation.search

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.layoutInflater
import com.bakkenbaeck.poddy.presentation.model.ViewPodcastSearchItem
import com.bakkenbaeck.poddy.util.Differ

class SearchAdapter(
    private val onItemClickedListener: (View, ViewPodcastSearchItem) -> Unit
) : RecyclerView.Adapter<SearchViewHolder>() {

    private val items by lazy(mode = LazyThreadSafetyMode.NONE) {
        mutableListOf<ViewPodcastSearchItem>()
    }

    fun add(searchItems: List<ViewPodcastSearchItem>) {
        val diffResult = DiffUtil.calculateDiff(Differ(items, searchItems))
        items.clear()
        items.addAll(searchItems)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = parent.layoutInflater().inflate(R.layout.search_item, parent, false)
        return SearchViewHolder(view)
    }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = items[position]
        holder.apply {
            setSearchItem(item)
            setOnItemClickedListener(item, onItemClickedListener)
        }
    }
}
