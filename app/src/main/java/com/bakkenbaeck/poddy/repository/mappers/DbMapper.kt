package com.bakkenbaeck.poddy.repository.mappers

import com.bakkenbaeck.poddy.network.model.PodcastResponse
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import org.db.Episode
import org.db.Podcast

fun mapPodcastFromNetworkToDB(podcast: PodcastResponse): Podcast.Impl {
    return Podcast.Impl(
        id = podcast.id,
        title = podcast.title,
        description = podcast.description,
        image = podcast.image
    )
}

fun mapEpisodesFromNetworkToDB(podcast: PodcastResponse): List<Episode.Impl> {
    return podcast.episodes.map { Episode.Impl(
        id = it.id,
        podcast_id = podcast.id,
        title = it.title,
        description = it.description,
        pub_date = it.pub_date_ms,
        duration = it.audio_length_sec.toLong(),
        image = it.image
    ) }
}


fun mapPodcastFromViewToDB(podcast: ViewPodcast): Podcast.Impl {
    return Podcast.Impl(
        id = podcast.id,
        title = podcast.title,
        description = podcast.description,
        image = podcast.image
    )
}

fun mapEpisodesFromViewToDB(podcast: ViewPodcast): List<Episode.Impl> {
    return podcast.episodes.map { mapEpisodeFromViewToDB(podcast, it) }
}

fun mapEpisodeFromViewToDB(podcast: ViewPodcast, episode: ViewEpisode): Episode.Impl {
    return Episode.Impl(
        id = episode.id,
        podcast_id = podcast.id,
        title = episode.title,
        description = episode.description,
        pub_date = episode.pubDate,
        duration = episode.duration.toLong(),
        image = episode.image
    )
}
