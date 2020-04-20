package com.bakkenbaeck.poddy

import android.media.MediaPlayer
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import java.io.FileNotFoundException
import java.lang.Exception

class PodcastPlayer(
    private val mediaPlayer: MediaPlayer = MediaPlayer()
) {
    fun load(
        episode: ViewEpisode,
        path: String,
        onStartListener: () -> Unit,
        onCompletedListener: () -> Unit
    ) {
        mediaPlayer.reset()
        try {
            mediaPlayer.setDataSource(path)
        } catch (ex: FileNotFoundException) {
            try {
                mediaPlayer.setDataSource(episode.audio)
            } catch (e: Exception) {
               // Log and show error somehow
            }
        }

        mediaPlayer.setOnPreparedListener {
            onStartListener()
            mediaPlayer.seekTo(episode.progress.toInt())
            mediaPlayer.start()
        }
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnCompletionListener { onCompletedListener() }
    }

    fun seekTo(progressInPercent: Int) {
        val progress = (mediaPlayer.duration) * (progressInPercent.toDouble() / 100)
        mediaPlayer.seekTo(progress.toInt())
    }

    fun start() = mediaPlayer.start()

    fun pause() = mediaPlayer.pause()

    fun isPlaying() = mediaPlayer.isPlaying

    fun getDuration() = mediaPlayer.duration

    fun getProgress() = mediaPlayer.currentPosition

    fun getProgressAndDuration(): Pair<Int, Int> = Pair(getProgress(), getDuration())

    fun destroy() {
        mediaPlayer.release()
    }
}
