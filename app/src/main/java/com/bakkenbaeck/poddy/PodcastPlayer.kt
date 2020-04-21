package com.bakkenbaeck.poddy

import android.media.MediaPlayer
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import java.io.FileNotFoundException

interface PodcastPlayer {
    fun load(
        episode: ViewEpisode,
        path: String,
        onStartListener: () -> Unit,
        onCompletedListener: () -> Unit
    )

    fun seekTo(progressInPercent: Int)
    fun start()
    fun pause()
    fun isPlaying(): Boolean
    fun getDuration(): Int
    fun getProgress(): Int
    fun getProgressAndDuration(): Pair<Int, Int>
    fun goBack(milliseconds: Int)
    fun goForward(milliseconds: Int)
    fun destroy()
}

class PodcastPlayerImpl(
    private val mediaPlayer: MediaPlayer = MediaPlayer()
) : PodcastPlayer {
    override fun load(
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

    override fun seekTo(progressInPercent: Int) {
        val progress = (mediaPlayer.duration) * (progressInPercent.toDouble() / 100)
        mediaPlayer.seekTo(progress.toInt())
    }

    override fun start() = mediaPlayer.start()

    override fun pause() = mediaPlayer.pause()

    override fun isPlaying(): Boolean = mediaPlayer.isPlaying

    override fun getDuration(): Int = mediaPlayer.duration

    override fun getProgress(): Int = mediaPlayer.currentPosition

    override fun getProgressAndDuration(): Pair<Int, Int> = Pair(getProgress(), getDuration())

    override fun goBack(milliseconds: Int) {
        mediaPlayer.seekTo(mediaPlayer.currentPosition - milliseconds)
    }

    override fun goForward(milliseconds: Int) {
        mediaPlayer.seekTo(mediaPlayer.currentPosition + milliseconds)
    }

    override fun destroy() {
        mediaPlayer.release()
    }
}
