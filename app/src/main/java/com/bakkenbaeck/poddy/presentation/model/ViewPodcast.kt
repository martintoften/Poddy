package com.bakkenbaeck.poddy.presentation.model

import com.bakkenbaeck.poddy.util.Diffable

data class ViewPodcast(
    override val id: String,
    val title: String,
    val description: String,
    val image: String,
    val nextEpisodePubDate: Long?,
    val episodes: List<ViewEpisode>,
    val hasSubscribed: Boolean,
    val totalEpisodes: Int
) : Diffable

data class ViewEpisode(
    override val id: String,
    val title: String,
    val description: String,
    val pubDate: Long,
    val duration: Int,
    val image: String,
    val audio: String
) : Diffable
