package com.bakkenbaeck.poddy.presentation.model

data class ViewPodcastSearch(
    val took: Double,
    val count: Int,
    val total: Int,
    val nextOffset: Int,
    val results: List<ViewPodcastSearchItem>
)

data class ViewPodcastSearchItem(
    val id: String,
    val rss: String,
    val description: String,
    val title: String,
    val publisher: String,
    val image: String,
    val thumbnail: String,
    val genreIds: List<Int>,
    val totalEpisodes: Int,
    val email: String?
)
