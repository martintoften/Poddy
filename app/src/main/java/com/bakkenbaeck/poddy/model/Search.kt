package com.bakkenbaeck.poddy.model

data class Search(
    val resultCount: Int = 0,
    val results: List<SearchItem>
)

data class SearchItem(
    val wrapperType: String,
    val kind: String,
    val artistId: Int,
    val collectionId: Int,
    val trackId: Int,
    val artistName: String,
    val collectionName: String,
    val trackName: String,
    val collectionCensoredName: String,
    val trackCensoredName: String,
    val artistViewUrl: String,
    val collectionViewUrl: String,
    val feedUrl: String,
    val trackViewUrl: String,
    val artworkUrl30: String,
    val artworkUrl60: String,
    val artworkUrl100: String,
    val collectionPrice: Double,
    val trackPrice: Double,
    val trackRentalPrice: Double,
    val collectionHdPrice: Double,
    val trackHdPrice: Double,
    val trackHdRentalPrice: Double,
    val releaseDate: String,
    val collectionExplicitness: String,
    val trackExplicitness: String,
    val trackCount: Int,
    val country: String,
    val currency: String,
    val primaryGenreName: String,
    val contentAdvisoryRating: String,
    val artworkUrl600: String,
    val genreIds: List<Any>,
    val genres: List<Any>
)
