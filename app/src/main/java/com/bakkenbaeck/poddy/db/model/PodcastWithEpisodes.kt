package com.bakkenbaeck.poddy.db.model

import org.db.Podcast

data class PodcastWithEpisodes(
    val podcast: Podcast,
    val episodes: List<JoinedEpisode>
)