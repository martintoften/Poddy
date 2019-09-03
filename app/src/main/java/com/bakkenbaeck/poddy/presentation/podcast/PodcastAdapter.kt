package com.bakkenbaeck.poddy.presentation.podcast

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.layoutInflater
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import com.bakkenbaeck.poddy.util.Differ

class PodcastAdapter(
    private val onItemClickedListener: (ViewPodcast) -> Unit
) : RecyclerView.Adapter<PodcastViewHolder>() {

    private val items by lazy { mutableListOf<ViewPodcast>() }

    fun addItems(podcasts: List<ViewPodcast>) {
        val diffResult = DiffUtil.calculateDiff(Differ(items, podcasts))
        items.clear()
        items.addAll(podcasts)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastViewHolder {
        val view = parent.layoutInflater().inflate(R.layout.podcast_item, parent, false)
        return PodcastViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: PodcastViewHolder, position: Int) {
        val item = items[position]
        holder.apply {
            setPodcast(item)
            setOnItemClickedListener(item, onItemClickedListener)
        }
    }
}
