package com.bakkenbaeck.poddy.presentation.model

import com.bakkenbaeck.poddy.util.Diffable

data class ViewPodcastSearch(
    val took: Double,
    val count: Int,
    val total: Int,
    val nextOffset: Int,
    val results: List<ViewPodcastSearchItem>
)

data class ViewPodcastSearchItem(
    override val id: String,
    override val description: String,
    override val title: String,
    override val image: String,
    val publisher: String,
    val rss: String,
    val thumbnail: String,
    val genreIds: List<Int>,
    val totalEpisodes: Int,
    val email: String?
) : Diffable, ViewBasePodcast
