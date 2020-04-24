package com.bakkenbaeck.poddy.presentation.search

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.layoutInflater
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import com.bakkenbaeck.poddy.util.Differ

class CategoryAdapter(
    private val itemClickListener: (View, ViewPodcast) -> Unit
) : RecyclerView.Adapter<CategoryViewHolder>() {

    private val items by lazy { mutableListOf<ViewPodcast>() }

    fun setItems(podcasts: List<ViewPodcast>) {
        val diffResult = DiffUtil.calculateDiff(Differ(items, podcasts))
        items.clear()
        items.addAll(podcasts)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = parent.layoutInflater().inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val podcast = items[position]
        holder.apply {
            setItem(podcast)
            setOnItemClickedListener(podcast, itemClickListener)
        }
    }

    override fun getItemCount(): Int = items.size
}