package com.bakkenbaeck.poddy.presentation.feed

import android.view.View
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.toDate
import com.bakkenbaeck.poddy.extensions.toSeconds
import com.bakkenbaeck.poddy.presentation.custom.ClickableViewHolder
import com.bakkenbaeck.poddy.presentation.model.DownloadState
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import kotlinx.android.synthetic.main.episode_item.*

class EpisodeViewHolder(override val containerView: View) : ClickableViewHolder<ViewEpisode>(containerView) {
    fun setEpisode(episode: ViewEpisode) {
        date.text = episode.pubDate.toDate()
        name.text = episode.title
        length.text = "${episode.duration.toSeconds()} min"

        val icon = when (episode.isDownloaded) {
            DownloadState.DOWNLOADED -> R.drawable.ic_check
            DownloadState.IN_PROGRESS -> R.drawable.ic_cancel
            DownloadState.NOT_DOWNLOADED -> R.drawable.ic_download
        }

        isDownloaded.setImageResource(icon)
    }

    fun setOnDownloadClickListener(episode: ViewEpisode, listener: (ViewEpisode) -> Unit) {
        if (episode.isDownloaded == DownloadState.NOT_DOWNLOADED) {
            isDownloaded.setOnClickListener { listener(episode) }
        }
    }
}
