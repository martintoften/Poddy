package com.bakkenbaeck.poddy.presentation.model

import android.os.Parcelable
import com.bakkenbaeck.poddy.presentation.mappers.mapToViewPodcastFromDB
import com.bakkenbaeck.poddy.util.Diffable
import kotlinx.android.parcel.Parcelize
import org.db.*
import java.util.*

@Parcelize
data class ViewPodcast(
    override val id: String,
    val title: String,
    val description: String,
    val image: String,
    val nextEpisodePubDate: Long?,
    val episodes: List<ViewEpisode>,
    val hasSubscribed: Boolean,
    val totalEpisodes: Int
) : Diffable, Parcelable

@Parcelize
data class ViewEpisode(
    override val id: String,
    val podcastId: String,
    val title: String,
    val description: String,
    val pubDate: Long,
    val duration: Int,
    val image: String,
    val audio: String,
    val isDownloaded: DownloadState,
    val downloadProgress: String = "",
    val progress: Long
) : Diffable, Parcelable

enum class DownloadState(val value: Int) {
    NOT_DOWNLOADED(0),
    IN_PROGRESS(1),
    DOWNLOADED(2);

    companion object {
        fun intToEnum(value: Int): DownloadState {
            return when (value) {
                0 -> NOT_DOWNLOADED
                1 -> IN_PROGRESS
                2 -> DOWNLOADED
                else -> NOT_DOWNLOADED
            }
        }
    }
}

fun List<Podcast>.toPodcastViewModel(): List<ViewPodcast> {
    return map {
        mapToViewPodcastFromDB(
            it,
            emptyList(),
            true
        )
    }
}

fun List<Episode>.toViewModel(): List<ViewEpisode> {
    return map { it.toViewModel() }
}

fun ViewEpisode.toDbModel(timestamp: Long = Date().time): Episode {
    return Episode.Impl(
        id = id,
        podcast_id = podcastId,
        title = title,
        description = description,
        pub_date = pubDate,
        duration = duration.toLong(),
        image = image,
        timestamp = timestamp,
        audio = audio,
        is_downloaded = isDownloaded.value.toLong(),
        progress = progress
    )
}

fun Episode.toViewModel(downloadProgress: String = ""): ViewEpisode {
    return ViewEpisode(
        id = id,
        podcastId = podcast_id,
        title = title,
        description = description,
        pubDate = pub_date,
        duration = duration.toInt(),
        image = image,
        audio = audio,
        isDownloaded = DownloadState.intToEnum(is_downloaded.toInt()),
        downloadProgress = downloadProgress,
        progress = progress
    )
}

fun ByIdEpisode.toViewModel(downloadProgress: String = ""): ViewEpisode {
    return ViewEpisode(
        id = id,
        podcastId = podcast_id,
        title = title,
        description = description,
        pubDate = pub_date,
        duration = duration.toInt(),
        image = image,
        audio = audio,
        isDownloaded = DownloadState.intToEnum(is_downloaded.toInt()),
        downloadProgress = downloadProgress,
        progress = progress
    )
}

fun List<ByIdsEpisodes>.toByIdsViewModel(): List<ViewEpisode> {
    return map { it.toViewModel() }
}

fun ByIdsEpisodes.toViewModel(downloadProgress: String = ""): ViewEpisode {
    return ViewEpisode(
        id = id,
        podcastId = podcast_id,
        title = title,
        description = description,
        pubDate = pub_date,
        duration = duration.toInt(),
        image = image,
        audio = audio,
        isDownloaded = DownloadState.intToEnum(is_downloaded.toInt()),
        downloadProgress = downloadProgress,
        progress = progress
    )
}

fun AllEpisodes.toViewModel(downloadProgress: String = ""): ViewEpisode {
    return ViewEpisode(
        id = id,
        podcastId = podcast_id,
        title = title,
        description = description,
        pubDate = pub_date,
        duration = duration.toInt(),
        image = image,
        audio = audio,
        isDownloaded = DownloadState.intToEnum(is_downloaded.toInt()),
        downloadProgress = downloadProgress,
        progress = progress
    )
}

fun List<ByPodcastIdEpisodes>.toPodcastEpisodeViewModel(): List<Episode> {
    return map { it.toViewModel() }
}

fun ByPodcastIdEpisodes.toViewModel(): Episode {
    return Episode.Impl(
        id = id,
        podcast_id = podcast_id,
        title = title,
        description = description,
        pub_date = pub_date,
        duration = duration,
        image = image,
        timestamp = timestamp,
        audio = audio,
        is_downloaded = is_downloaded,
        progress = progress
    )
}

fun ViewPodcast.toDbModel(): Podcast {
    return Podcast.Impl(
        id = id,
        title = title,
        description = description,
        image = image,
        total_episodes = totalEpisodes.toLong()
    )
}