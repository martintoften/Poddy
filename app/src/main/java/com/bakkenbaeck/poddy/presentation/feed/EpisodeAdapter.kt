package com.bakkenbaeck.poddy.presentation.feed

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.layoutInflater
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode

class EpisodeAdapter(
    private val onItemClickListener: (View, ViewEpisode) -> Unit,
    private val onDownloadClickListener: (ViewEpisode) -> Unit
) : RecyclerView.Adapter<EpisodeViewHolder>() {

    private val items by lazy { mutableListOf<ViewEpisode>() }

    fun setItems(episodeItems: List<ViewEpisode>) {
        items.clear()
        items.addAll(episodeItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = parent.layoutInflater().inflate(R.layout.episode_item, parent, false)
        return EpisodeViewHolder(view)
    }

    override fun getItemCount() = items.count()

    fun getLastItem(): ViewEpisode? {
        return items.last() as? ViewEpisode ?: return null
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val item = items[position]
        holder.apply {
            setEpisode(item)
            setOnItemClickedListener(item, onItemClickListener)
            setOnDownloadClickListener(item, onDownloadClickListener)
        }
    }
}
