package com.bakkenbaeck.poddy.presentation.search

import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.layoutInflater
import com.bakkenbaeck.poddy.presentation.model.ViewCategory
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast

class CategoryListAdapter(
    private val listener: (View, ViewPodcast) -> Unit
) : RecyclerView.Adapter<CategoryListViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()
    private val items by lazy { mutableListOf<ViewCategory>() }

    fun setItems(podcasts: List<ViewCategory>) {
        items.clear()
        items.addAll(podcasts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        val view = parent.layoutInflater().inflate(R.layout.category_list, parent, false)
        return CategoryListViewHolder(viewPool, view)
    }

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
        val categories = items[position]
        holder.setItem(categories, listener)
    }

    override fun getItemCount(): Int = items.size
}