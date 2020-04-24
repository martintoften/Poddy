package com.bakkenbaeck.poddy.network.model

data class GenreResponse(
    val genres: List<Genre>
)

data class Genre(
    val id: String,
    val name: String,
    val parent_id: Int
)