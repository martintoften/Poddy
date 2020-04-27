package com.bakkenbaeck.poddy.presentation.feed

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.layoutInflater
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import com.bakkenbaeck.poddy.util.Differ

val placeholderPodcast = ViewPodcast(
    id = "-1",
    title = "Placeholder",
    description = "",
    image = "",
    episodes = emptyList(),
    hasSubscribed = false,
    totalEpisodes = 0,
    nextEpisodePubDate = 0L
)

val placeholderList = MutableList(8) { placeholderPodcast }

class RecommendationAdapter : RecyclerView.Adapter<RecommendationViewHolder>() {

    private val items by lazy(mode = LazyThreadSafetyMode.NONE) { placeholderList }

    fun setItems(podcasts: List<ViewPodcast>) {
        val diffResult = DiffUtil.calculateDiff(Differ(items, podcasts))
        items.clear()
        items.addAll(podcasts)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationViewHolder {
        val view = parent.layoutInflater().inflate(R.layout.recommendation_item, parent, false)
        return RecommendationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecommendationViewHolder, position: Int) {
        val podcast = items[position]
        holder.setItem(podcast)
    }

    override fun getItemCount() = items.size
}