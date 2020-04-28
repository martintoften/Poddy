package com.bakkenbaeck.poddy.presentation.search

import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.layoutInflater
import com.bakkenbaeck.poddy.presentation.model.ViewCategory
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast

class CategoryListAdapter(
    private val listener: (View, ViewPodcast) -> Unit
) : RecyclerView.Adapter<CategoryListViewHolder>() {

    private val scrollStates = hashMapOf<Int, Parcelable>()
    private val viewPool = RecyclerView.RecycledViewPool()
    private val items by lazy(mode = LazyThreadSafetyMode.NONE) {
        mutableListOf<ViewCategory>()
    }

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
        val category = items[position]
        holder.setItem(category, listener)
        restoreScrollPosition(category, holder)
    }

    private fun restoreScrollPosition(category: ViewCategory, holder: CategoryListViewHolder) {
        val savedState = scrollStates[category.categoryId]
        if (savedState != null) holder.getLayoutManager()?.onRestoreInstanceState(savedState)
        else holder.getLayoutManager()?.scrollToPosition(0)
    }

    override fun onViewRecycled(holder: CategoryListViewHolder) {
        super.onViewRecycled(holder)
        val key = items[holder.adapterPosition].categoryId
        val savedState = holder.getLayoutManager()?.onSaveInstanceState() ?: return
        scrollStates[key] = savedState
    }

    override fun getItemCount(): Int = items.size
}