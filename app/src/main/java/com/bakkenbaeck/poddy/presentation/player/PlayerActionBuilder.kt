package com.bakkenbaeck.poddy.presentation.player

import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction

class PlayerActionBuilder(
    private val playerQueue: PlayerQueue
) {
    fun getStartAction(episode: ViewEpisode?, isPlaying: Boolean): ViewPlayerAction {
        if (episode == null) throw IllegalStateException("Must pass an episode with the start action")

        val isTheSameEpisodeAsCurrent = episode.id == playerQueue.current()?.id
        return if (isTheSameEpisodeAsCurrent) {
            if (isPlaying) ViewPlayerAction.Pause(episode)
            else ViewPlayerAction.Play(episode)
        } else {
            ViewPlayerAction.Start(episode)
        }
    }

    fun getPlayAction(): ViewPlayerAction.Play? {
        val episode = playerQueue.current() ?: return null
        return ViewPlayerAction.Play(episode)
    }

    fun getPauseAction(): ViewPlayerAction.Pause? {
        val episode = playerQueue.current() ?: return null
        return ViewPlayerAction.Pause(episode)
    }
}
