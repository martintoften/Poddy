package com.bakkenbaeck.poddy.presentation.mappers

import com.bakkenbaeck.poddy.network.model.SearchItem
import com.bakkenbaeck.poddy.network.model.SearchResponse
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import com.bakkenbaeck.poddy.presentation.model.ViewPodcastSearch
import com.bakkenbaeck.poddy.presentation.model.ViewPodcastSearchItem
import com.bakkenbaeck.poddy.presentation.model.toViewModel
import org.db.Episode
import org.db.Podcast

fun mapFromNetworkToView(network: SearchResponse): ViewPodcastSearch {
    val items = mapToViewPodcastSearchFromNetworkToView(network.results)

    return ViewPodcastSearch(
        took = network.took,
        count = network.count,
        total = network.total,
        nextOffset = network.next_offset,
        results = items
    )
}

fun mapToViewPodcastSearchFromNetworkToView(network: List<SearchItem>): List<ViewPodcastSearchItem> {
    return network.map {
        ViewPodcastSearchItem(
            id = it.id,
            rss = it.rss,
            description = it.description_original,
            title = it.title_original,
            publisher = it.publisher_original,
            image = it.image,
            thumbnail = it.thumbnail,
            genreIds = it.genre_ids,
            totalEpisodes = it.total_episodes,
            email = it.email
        )
    }
}

fun mapToViewPodcastFromDB(
    db: Podcast,
    episodes: List<Episode>,
    hasSubscribed: Boolean
): ViewPodcast {
    return ViewPodcast(
        id = db.id,
        title = db.title,
        description = db.description,
        image = db.image,
        nextEpisodePubDate = 0L,
        episodes = episodes.toViewModel(),
        hasSubscribed = hasSubscribed,
        totalEpisodes = db.total_episodes.toInt()
    )
}
