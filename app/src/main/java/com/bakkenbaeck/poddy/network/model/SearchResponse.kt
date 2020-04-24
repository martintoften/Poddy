package com.bakkenbaeck.poddy.network.model

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