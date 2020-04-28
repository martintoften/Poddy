package com.bakkenbaeck.poddy.extensions

import android.os.Parcelable
import androidx.recyclerview.widget.RecyclerView
import com.bakkenbaeck.poddy.presentation.search.CategoryListAdapter
import com.bakkenbaeck.poddy.presentation.search.CategoryListViewHolder

fun CategoryListAdapter.getScrollStates(recyclerView: RecyclerView): HashMap<String, Parcelable> {
    val scrollStates = hashMapOf<String, Parcelable>()
    getCategories().forEach {
        val position = getCategories().indexOf(it)
        val view = recyclerView.findViewHolderForAdapterPosition(position) as? CategoryListViewHolder?
        val state = view?.getLayoutManager()?.onSaveInstanceState() ?: return@forEach
        scrollStates[it.id] = state
    }
    return scrollStates
}
