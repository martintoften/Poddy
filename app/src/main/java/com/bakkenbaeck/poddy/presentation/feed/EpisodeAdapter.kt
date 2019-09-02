package com.bakkenbaeck.poddy.presentation.feed

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.layoutInflater
import com.bakkenbaeck.poddy.network.model.EpisodeItem

sealed class EpisodeViewType(val type: Int) {
    companion object {
        fun getType(index: Int): EpisodeViewType {
            return when (index) {
                0 -> Header(index)
                else -> Item(index)
            }
        }
    }
    class Header(type: Int) : EpisodeViewType(type)
    class Item(type: Int) : EpisodeViewType(type)
}

class Header(
    val image: String,
    val title: String,
    val description: String
)

class EpisodeAdapter(
    private val onItemClickListener: (EpisodeItem) -> Unit,
    private val onHeaderItemClickListener: (Header) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items by lazy { mutableListOf<Any>() }

    fun add(header: Header, episodeItems: List<EpisodeItem>) {
        items.clear()
        items.add(header)
        items.addAll(episodeItems)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return EpisodeViewType.getType(position).type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val episodeViewType = EpisodeViewType.getType(viewType)
        val inflater = parent.layoutInflater()

        return when (episodeViewType) {
            is EpisodeViewType.Header -> {
                val view = inflater.inflate(R.layout.episode_item_header, parent, false)
                EpisodeHeaderViewHolder(view)
            }
            is EpisodeViewType.Item -> {
                val view = inflater.inflate(R.layout.episode_item, parent, false)
                EpisodeViewHolder(view)
            }
        }
    }

    override fun getItemCount() = items.count()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        when (holder) {
            is EpisodeHeaderViewHolder -> {
                val castedItem = item as? Header ?: return
                holder.apply {
                    setHeader(castedItem)
                    setOnItemClickedListener(castedItem, onHeaderItemClickListener)
                }
            }
            is EpisodeViewHolder -> {
                val castedItem = item as? EpisodeItem ?: return
                holder.apply {
                    setEpisode(castedItem)
                    setOnItemClickedListener(castedItem, onItemClickListener)
                }
            }
        }
    }
}
