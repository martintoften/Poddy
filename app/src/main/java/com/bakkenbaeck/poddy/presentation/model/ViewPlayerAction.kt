package com.bakkenbaeck.poddy.presentation.model

sealed class ViewPlayerAction(val episode: ViewEpisode) {
    class Start(episode: ViewEpisode) : ViewPlayerAction(episode)
    class Play(episode: ViewEpisode) : ViewPlayerAction(episode)
    class Pause(episode: ViewEpisode) : ViewPlayerAction(episode)
    class Progress(
        episode: ViewEpisode,
        val progress: Int,
        val duration: Int
    ) : ViewPlayerAction(episode)
}
