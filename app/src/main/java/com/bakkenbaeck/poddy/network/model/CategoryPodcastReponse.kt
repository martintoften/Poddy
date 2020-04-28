package com.bakkenbaeck.poddy.network.model

import com.bakkenbaeck.poddy.presentation.model.ViewCategory
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast

data class CategoryPodcastReponse(
    val id: Int,
    val name: String,
    val total: Int,
    val has_next: Boolean,
    val podcasts: List<PodcastResponse>,
    val parent_id: Int?,
    val page_number: Int,
    val has_previous: Boolean,
    val listennotes_url: String,
    val next_page_number: Int,
    val previous_page_number: Int
)

fun List<CategoryPodcastReponse>.toViewModel(): List<ViewCategory> {
    return map {  ViewCategory(it.id.toString(), it.name, it.toViewModel()) }
}

fun CategoryPodcastReponse.toViewModel(): List<ViewPodcast> {
    return podcasts.map {
        ViewPodcast(
            id = it.id,
            title = it.title,
            description = it.description,
            image = it.image,
            nextEpisodePubDate = it.next_episode_pub_date,
            episodes = emptyList(),
            hasSubscribed = false,
            totalEpisodes = it.total_episodes
        )
    }
}