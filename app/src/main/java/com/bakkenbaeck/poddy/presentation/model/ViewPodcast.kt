package com.bakkenbaeck.poddy.presentation.model

import android.os.Parcelable
import com.bakkenbaeck.poddy.util.Diffable
import kotlinx.android.parcel.Parcelize

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
