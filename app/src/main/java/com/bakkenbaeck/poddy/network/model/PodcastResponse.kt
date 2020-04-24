package com.bakkenbaeck.poddy.network.model

import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import org.db.Episode
import org.db.Podcast
import java.util.*

data class PodcastResponse(
    val id: String,
    val title: String,
    val publisher: String,
    val image: String,
    val thumbnail: String,
    val listennotes_url: String,
    val total_episodes: Int,
    val explicit_content: Boolean,
    val description: String,
    val itunes_id: Long,
    val rss: String,
    val latest_pub_date_ms: Long,
    val earliest_pub_date_ms: Long,
    val language: String,
    val country: String,
    val website: String,
    val email: String,
    val next_episode_pub_date: Long?,
    val episodes: List<EpisodeItem>
)

data class EpisodeItem(
    val id: String,
    val title: String,
    val description: String,
    val pub_date_ms: Long,
    val audio: String,
    val audio_length_sec: Int,
    val listennotes_url: String,
    val image: String,
    val thumbnail: String,
    val maybe_audio_invalid: Boolean,
    val listennotes_edit_url: String,
    val explicit_content: Boolean
)

fun List<PodcastResponse>.toViewModel(): List<ViewPodcast> {
    return map {
        ViewPodcast(
            id = it.id,
            title = it.title,
            description = it.description,
            image = it.image,
            totalEpisodes = it.total_episodes,
            nextEpisodePubDate = it.next_episode_pub_date,
            episodes = emptyList(),
            hasSubscribed = false
        )
    }
}

fun PodcastResponse.toDbModel(): Podcast {
    return Podcast.Impl(
        id = id,
        title = title,
        description = description,
        image = image,
        total_episodes = total_episodes.toLong()
    )
}

fun PodcastResponse.toEpisodeDbModel(): List<Episode> {
    val timestamp = Date().time
    return episodes.map {
        Episode.Impl(
            id = it.id,
            podcast_id = this.id,
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
