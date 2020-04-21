package com.bakkenbaeck.poddy.presentation.feed

import androidx.recyclerview.widget.DiffUtil
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode

class EpisodeDiffer(
    private val old: List<ViewEpisode>,
    private val new: List<ViewEpisode>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = old.size
    override fun getNewListSize(): Int = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] == new[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = old[oldItemPosition]
        val newItem = new[newItemPosition]
        return oldItem.id == newItem.id
                && oldItem.isDownloaded == newItem.isDownloaded
                && oldItem.progress == newItem.progress
    }
}