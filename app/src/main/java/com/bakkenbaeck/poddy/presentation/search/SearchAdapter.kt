package com.bakkenbaeck.poddy.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.model.SearchItem

class SearchAdapter(
    private val onItemClickedListener: (SearchItem) -> Unit
) : RecyclerView.Adapter<SearchViewHolder>() {

    private val items by lazy { mutableListOf<SearchItem>() }

    fun add(searchItems: List<SearchItem>) {
        items.clear()
        items.addAll(searchItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
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
