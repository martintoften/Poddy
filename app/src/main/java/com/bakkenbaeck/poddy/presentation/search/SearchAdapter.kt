package com.bakkenbaeck.poddy.presentation.search

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.layoutInflater
import com.bakkenbaeck.poddy.presentation.model.ViewPodcastSearchItem

class SearchAdapter(
    private val onItemClickedListener: (ViewPodcastSearchItem) -> Unit
) : RecyclerView.Adapter<SearchViewHolder>() {

    private val items by lazy { mutableListOf<ViewPodcastSearchItem>() }

    fun add(searchItems: List<ViewPodcastSearchItem>) {
        items.clear()
        items.addAll(searchItems)
        notifyDataSetChanged()
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
