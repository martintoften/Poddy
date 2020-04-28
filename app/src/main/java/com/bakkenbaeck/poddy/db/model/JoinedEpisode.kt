package com.bakkenbaeck.poddy.db.model

import org.db.ByIdEpisode
import org.db.ByPodcastIdEpisodes

data class JoinedEpisode(
    val id: String,
    val podcastId: String,
    val title: String,
    val description: String,
    val pubDate: Long,
    val audio: String,
    val duration: Long,
    val image: String,
    val timestamp: Long,
    val isDownloaded: Long,
    val progress: Long,
    val podcastTitle: String
)

fun ByIdEpisode.toJoinedModel(): JoinedEpisode {
    return JoinedEpisode(
        id = id,
        podcastId = podcast_id,
        title = title,
        description = description,
        pubDate = pub_date,
        audio = audio,
        duration = duration,
        image = image,
        timestamp = timestamp,
        isDownloaded = is_downloaded,
        progress = progress,
        podcastTitle = title_
    )
}

fun List<ByPodcastIdEpisodes>.toJoinedModel(): List<JoinedEpisode> {
    return map { JoinedEpisode(
        id = it.id,
        podcastId = it.podcast_id,
        title = it.title,
        description = it.description,
        pubDate = it.pub_date,
        audio = it.audio,
        duration = it.duration,
        image = it.image,
        timestamp = it.timestamp,
        isDownloaded = it.is_downloaded,
        progress = it.progress,
        podcastTitle = it.title_
    ) }
}
