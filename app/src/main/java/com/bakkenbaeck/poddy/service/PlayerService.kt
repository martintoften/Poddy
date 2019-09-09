package com.bakkenbaeck.poddy.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import com.bakkenbaeck.poddy.notification.PlayerNotificationHandler
import com.bakkenbaeck.poddy.extensions.*
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode


const val PLAYER_NOTIFICATION_ID = 2
const val PLAYER_DEFAULT_NOTIFICATION_TITLE = "Player"
const val PLAYER_CHANNEL_ID = "102"
const val PLAYER_CHANNEL_NAME = "Player channel"

const val EPISODE = "EPISODE"

const val ACTION_START = "action_start"
const val ACTION_PLAY = "action_play"
const val ACTION_PAUSE = "action_pause"
const val ACTION_REWIND = "action_rewind"
const val ACTION_FAST_FORWARD = "action_fast_forward"

class PlayerService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val playerNotificationHandler =
        PlayerNotificationHandler(this)

    private val queue = mutableListOf<ViewEpisode>()

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { handleIntent(it) }
        return START_NOT_STICKY
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            ACTION_START -> {
                val episode = intent.getParcelableExtra<ViewEpisode?>(EPISODE) ?: return
                initPlayerAndNotifications(episode)
            }
            ACTION_PLAY -> onPlay()
            ACTION_PAUSE -> onPause()
            else -> Log.d("PlayerService", "Invalid intent action")
        }
    }

    private fun initPlayerAndNotifications(episode: ViewEpisode) {
        val podcastPath = episode.getEpisodePath(this)
        initMediaPlayer(podcastPath) {
            queue.add(episode)
            val action = playerNotificationHandler.generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE)
            playerNotificationHandler.buildNotification(episode.title, action)
        }
        playerNotificationHandler.createChannel()
    }

    private fun onPlay() {
        val episode = queue.firstOrNull() ?: return
        val action = playerNotificationHandler.generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE)
        playerNotificationHandler.buildNotification(episode.title, action)
        mediaPlayer?.start()
    }

    private fun onPause() {
        val episode = queue.firstOrNull() ?: return
        val action = playerNotificationHandler.generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY)
        playerNotificationHandler.buildNotification(episode.title, action)
        mediaPlayer?.pause()
    }

    private fun initMediaPlayer(path: String, onStartListener: () -> Unit) {
        if (mediaPlayer != null) return

        mediaPlayer = MediaPlayer().apply {
            setDataSource(path)
            setOnPreparedListener {
                onStartListener()
                start()
            }
            prepareAsync()
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }
}
