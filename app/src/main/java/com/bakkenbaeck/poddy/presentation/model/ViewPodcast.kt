package com.bakkenbaeck.poddy.presentation.model

import android.os.Parcelable
import com.bakkenbaeck.poddy.db.model.JoinedEpisode
import com.bakkenbaeck.poddy.presentation.mappers.mapToViewPodcastFromDB
import com.bakkenbaeck.poddy.util.Diffable
import kotlinx.android.parcel.Parcelize
import org.db.ByIdsEpisodes
import org.db.Episode
import org.db.Podcast
import java.util.*

@Parcelize
data class ViewPodcast(
    override val id: String,
    override val title: String,
    override val description: String,
    override val image: String,
    val nextEpisodePubDate: Long?,
    val episodes: List<ViewEpisode>,
    val hasSubscribed: Boolean,
    val totalEpisodes: Int
) : Diffable, ViewBasePodcast, Parcelable

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
    val progress: Long,
    val podcastTitle: String?
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
        progress = progress,
        podcastTitle = title_
    )
}

fun List<JoinedEpisode>.toPodcastEpisodeViewModel(): List<ViewEpisode> {
    return map { it.toViewModel() }
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

fun ViewPodcast.toBaseViewModel(): ViewBasePodcastImpl {
    return ViewBasePodcastImpl(
        id = id,
        title = title,
        description = description,
        image = image
    )
}

fun JoinedEpisode.toViewModel(downloadProgress: String? = null): ViewEpisode {
    return ViewEpisode(
        id = id,
        podcastId = podcastId,
        title = title,
        description = description,
        pubDate = pubDate,
        audio = audio,
        duration = duration.toInt(),
        image = image,
        isDownloaded = DownloadState.intToEnum(isDownloaded.toInt()),
        progress = progress,
        podcastTitle = podcastTitle,
        downloadProgress = downloadProgress.orEmpty()
    )
}