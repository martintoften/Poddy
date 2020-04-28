package com.bakkenbaeck.poddy.presentation.mappers

import com.bakkenbaeck.poddy.db.model.JoinedEpisode
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import com.bakkenbaeck.poddy.presentation.model.toPodcastEpisodeViewModel
import org.db.Podcast

fun mapToViewPodcastFromDB(
    db: Podcast,
    episodes: List<JoinedEpisode>,
    hasSubscribed: Boolean
): ViewPodcast {
    return ViewPodcast(
        id = db.id,
        title = db.title,
        description = db.description,
        image = db.image,
        nextEpisodePubDate = 0L,
        episodes = episodes.toPodcastEpisodeViewModel(),
        hasSubscribed = hasSubscribed,
        totalEpisodes = db.total_episodes.toInt()
    )
}
