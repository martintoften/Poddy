package com.bakkenbaeck.poddy.presentation.podcast

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.layoutInflater

data class PodcastFilter(
    val id: Int,
    val name: String,
    val isSelected: Boolean = false
)

class PodcastFilterAdapter(
    private val filters: MutableList<PodcastFilter>,
    private val onItemClickListener: (PodcastFilter) -> Unit
) : RecyclerView.Adapter<PodcastFilterViewHolder>() {

    private var lastPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastFilterViewHolder {
        val view = parent.layoutInflater().inflate(R.layout.podcast_filter_item, parent, false)
        return PodcastFilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: PodcastFilterViewHolder, position: Int) {
        val item = filters[position]
        holder.apply {
            bind(item)
            setOnClickListener(item) {
                updateItemState(position, it)
            }
        }
    }

    private fun updateItemState(position: Int, currentFilter: PodcastFilter) {
        if (lastPosition != -1) {
            filters[lastPosition] = filters[lastPosition].copy(isSelected = false)
            notifyItemChanged(lastPosition)
        }
        filters[position] = currentFilter.copy(isSelected = true)
        onItemClickListener(currentFilter)
        notifyItemChanged(position)
        lastPosition = position
    }

    override fun getItemCount() = filters.size
}