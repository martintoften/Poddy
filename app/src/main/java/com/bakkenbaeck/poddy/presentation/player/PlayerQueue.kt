package com.bakkenbaeck.poddy.presentation.player

import com.bakkenbaeck.poddy.presentation.model.ViewEpisode

class PlayerQueue {
    private val queue = mutableListOf<ViewEpisode>()
    private var currentEpisode: ViewEpisode? = null

    fun setQueue(episodes: List<ViewEpisode>) {
        queue.clear()
        queue.addAll(episodes)
    }

    fun first(): ViewEpisode? = queue.firstOrNull()

    fun current(): ViewEpisode? = currentEpisode

    fun hasCurrent() = currentEpisode != null

    fun setFirstAsCurrent(): ViewEpisode? {
        currentEpisode = first()
        return currentEpisode
    }

    fun setCurrent(episode: ViewEpisode) {
        currentEpisode = episode
    }

    fun clearCurrentEpisode() {
        currentEpisode = null
    }

    fun deleteFirst() = queue.removeAt(0)
}
