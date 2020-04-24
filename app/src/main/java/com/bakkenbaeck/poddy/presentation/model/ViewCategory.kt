package com.bakkenbaeck.poddy.presentation.model

data class ViewCategory(
    val categoryId: Int,
    val categoryName: String,
    val podcasts: List<ViewPodcast>
)