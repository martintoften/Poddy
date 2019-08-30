package com.bakkenbaeck.poddy.presentation.podcast

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.layoutInflater
import org.db.Podcast

class PodcastAdapter : RecyclerView.Adapter<PodcastViewHolder>() {

    private val items by lazy { mutableListOf<Podcast>() }

    fun addItem(item: List<Podcast>) {
        items.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastViewHolder {
        val view = parent.layoutInflater().inflate(R.layout.podcast_item, parent, false)
        return PodcastViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: PodcastViewHolder, position: Int) {
        val item = items[position]
        holder.setPodcast(item)
    }
}
