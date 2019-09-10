package com.bakkenbaeck.poddy.presentation.mappers

import com.bakkenbaeck.poddy.network.model.EpisodeItem
import com.bakkenbaeck.poddy.network.model.PodcastResponse
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

fun mapFromNetworkToView(network: PodcastResponse, hasSubscribed: Boolean): ViewPodcast {
    val items = mapToViewEpisodeFromNetwork(network.episodes)

    return ViewPodcast(
        id = network.id,
        title = network.title,
        description = network.description,
        image = network.image,
        nextEpisodePubDate = network.next_episode_pub_date,
        episodes = items,
        hasSubscribed = hasSubscribed,
        totalEpisodes = network.total_episodes
    )
}

fun mapToViewEpisodeFromNetwork(network: List<EpisodeItem>): List<ViewEpisode> {
    return network.map {
        ViewEpisode(
            id = it.id,
            description = it.description,
            title = it.title,
            image = it.image,
            duration = it.audio_length_sec,
            pubDate = it.pub_date_ms,
            audio = it.audio,
            isDownloaded = DownloadState.NOT_DOWNLOADED
        )
    }
}

fun mapToViewPodcastFromDB(db: List<Podcast>): List<ViewPodcast> {
    return db.map { mapToViewPodcastFromDB(it, emptyList(), true) }
}

fun mapToViewPodcastFromDB(db: Podcast, episodes: List<Episode>, hasSubscribed: Boolean): ViewPodcast {
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
    return db.map {
        ViewEpisode(
            id = it.id,
            title = it.title,
            description = it.description,
            pubDate = it.pub_date,
            duration = it.duration.toInt(),
            image = it.image,
            audio = it.audio,
            isDownloaded = DownloadState.intToEnum(it.is_downloaded.toInt())
        )
    }
}