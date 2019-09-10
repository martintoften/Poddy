package com.bakkenbaeck.poddy.util

import com.bakkenbaeck.poddy.presentation.model.ViewEpisode

class PlayerQueue {
    private val queue = mutableListOf<ViewEpisode>()
    private var currentEpisode: ViewEpisode? = null

    fun updateQueue(episodes: List<ViewEpisode>) {
        queue.clear()
        queue.addAll(episodes)
    }

    fun first() = queue.first()

    fun current(): ViewEpisode? = currentEpisode

    fun setCurrent(episode: ViewEpisode) {
        currentEpisode = episode
    }
}
