package com.bakkenbaeck.poddy.presentation.model

import com.bakkenbaeck.poddy.util.Diffable

data class ViewCategory(
    override val id: String,
    val categoryName: String,
    val podcasts: List<ViewPodcast>
) : Diffable