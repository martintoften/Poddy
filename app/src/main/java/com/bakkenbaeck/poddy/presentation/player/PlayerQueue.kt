package com.bakkenbaeck.poddy.presentation.player

import com.bakkenbaeck.poddy.presentation.model.ViewEpisode

class PlayerQueue {
    private val queue = mutableListOf<ViewEpisode>()
    private var currentEpisode: ViewEpisode? = null

    fun updateQueue(episodes: List<ViewEpisode>) {
        setQueue(episodes)
        clearCurrentIfNeeded()
    }

    private fun setQueue(episodes: List<ViewEpisode>) {
        queue.clear()
        queue.addAll(episodes)
    }

    private fun clearCurrentIfNeeded() {
        val current = current() ?: return
        val currentExist = queue.find { it.id == current.id } != null
        if (!currentExist) {
            clearCurrentEpisode()
        }
    }

    fun first(): ViewEpisode? = queue.firstOrNull()

    fun current(): ViewEpisode? = currentEpisode

    fun hasCurrent() = currentEpisode != null

    fun setCurrent(episode: ViewEpisode) {
        currentEpisode = episode
    }

    fun clearCurrentEpisode() {
        currentEpisode = null
    }
}
