package com.bakkenbaeck.poddy.repository.mappers

import com.bakkenbaeck.poddy.network.model.PodcastResponse
import org.db.Episode
import org.db.Podcast
import java.util.*

fun mapPodcastFromNetworkToDB(podcast: PodcastResponse): Podcast {
    return Podcast.Impl(
        id = podcast.id,
        title = podcast.title,
        description = podcast.description,
        image = podcast.image,
        total_episodes = podcast.total_episodes.toLong()
    )
}

fun mapEpisodesFromNetworkToDB(podcast: PodcastResponse): List<Episode> {
    val timestamp = Date().time
    return podcast.episodes.map {
        Episode.Impl(
            id = it.id,
            podcast_id = podcast.id,
            title = it.title,
            description = it.description,
            pub_date = it.pub_date_ms,
            duration = it.audio_length_sec.toLong(),
            image = it.image,
            timestamp = timestamp,
            audio = it.audio,
            is_downloaded = 0,
            progress = 0
        )
    }
}