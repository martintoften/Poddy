package com.bakkenbaeck.poddy.presentation.mappers

import com.bakkenbaeck.poddy.network.model.SearchItem
import com.bakkenbaeck.poddy.network.model.SearchResponse
import com.bakkenbaeck.poddy.presentation.model.*
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

fun mapToViewPodcastFromDB(db: List<Podcast>): List<ViewPodcast> {
    return db.map { mapToViewPodcastFromDB(it, emptyList(), true) }
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
        episodes = mapToViewEpisodeFromDB(episodes),
        hasSubscribed = hasSubscribed,
        totalEpisodes = db.total_episodes.toInt()
    )
}

fun mapToViewEpisodeFromDB(db: List<Episode>): List<ViewEpisode> {
    return db.map { mapToViewEpisodeFromDB(it) }
}

fun mapToViewEpisodeFromDB(it: Episode, downloadProgress: String = ""): ViewEpisode {
    return ViewEpisode(
        id = it.id,
        podcastId = it.podcast_id,
        title = it.title,
        description = it.description,
        pubDate = it.pub_date,
        duration = it.duration.toInt(),
        image = it.image,
        audio = it.audio,
        isDownloaded = DownloadState.intToEnum(it.is_downloaded.toInt()),
        downloadProgress = downloadProgress,
        progress = it.progress
    )
}
