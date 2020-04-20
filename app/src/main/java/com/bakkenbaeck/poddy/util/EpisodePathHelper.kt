package com.bakkenbaeck.poddy.util

import com.bakkenbaeck.poddy.extensions.getEpisodePath
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode

interface EpisodePathHelper {
    fun getPath(episode: ViewEpisode): String
}

class EpisodePathHelperImpl : EpisodePathHelper {
    override fun getPath(episode: ViewEpisode): String {
        return episode.getEpisodePath()
    }
}