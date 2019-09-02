package com.bakkenbaeck.poddy.presentation.model

data class ViewPodcast(
    val id: String,
    val title: String,
    val description: String,
    val image: String,
    val episodes: List<ViewEpisode>
)

data class ViewEpisode(
    val id: String,
    val title: String,
    val description: String,
    val pubDate: Long,
    val duration: Int,
    val image: String
)
