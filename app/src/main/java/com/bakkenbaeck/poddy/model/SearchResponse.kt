package com.bakkenbaeck.poddy.model

data class SearchResponse(
    val took: Double,
    val count: Int,
    val total: Int,
    val next_offset: Int,
    val results: List<SearchItem>
)

data class SearchItem(
    val rss: String,
    val description_highlighted: String,
    val description_original: String,
    val title_highlighted: String,
    val title_original: String,
    val publisher_highlighted: String,
    val publisher_original: String,
    val image: String,
    val thumbnail: String,
    val itunes_id: String,
    val latest_pub_date_ms: Long,
    val earliest_pub_date_ms: Long,
    val id: String,
    val genre_ids: List<Int>,
    val listennotes_url: String,
    val total_episodes: Int,
    val email: String,
    val explicit_content: Boolean
)

data class PodcastResponse(
    val id: String,
    val title: String,
    val publisher: String,
    val image: String,
    val thumbnail: String,
    val listennotes_url: String,
    val total_episodes: Int,
    val explicit_content: Boolean,
    val description: String,
    val itunes_id: Long,
    val rss: String,
    val latest_pub_date_ms: Long,
    val earliest_pub_date_ms: Long,
    val language: String,
    val country: String,
    val website: String,
    val email: String,
    val next_episode_pub_date: Long,
    val episodes: List<EpisodeItem>
)

data class EpisodeItem(
    val id: String,
    val title: String,
    val description: String,
    val pub_date_ms: Long,
    val audio: String,
    val audio_length_sec: Int,
    val listennotes_url: String,
    val image: String,
    val thumbnail: String,
    val maybe_audio_invalid: Boolean,
    val listennotes_edit_url: String,
    val explicit_content: Boolean
)
