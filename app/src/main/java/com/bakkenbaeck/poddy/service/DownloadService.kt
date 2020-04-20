package com.bakkenbaeck.poddy.service

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.createNotificationChannel
import com.bakkenbaeck.poddy.extensions.getPodcastDir
import com.bakkenbaeck.poddy.extensions.notifyNotification
import com.bakkenbaeck.poddy.network.ProgressEvent
import com.bakkenbaeck.poddy.presentation.MainActivity
import com.bakkenbaeck.poddy.repository.DownloadRepository
import com.bakkenbaeck.poddy.util.DownloadTask
import com.bakkenbaeck.poddy.util.SafeQueue
import com.bakkenbaeck.poddy.util.createNewFile
import java.io.File
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

const val NOTIFICATION_ID = 1
const val DEFAULT_NOTIFICATION_TITLE = "Downloading..."
const val DOWNLOAD_CHANNEL_ID = "101"
const val DOWNLOAD_CHANNEL_NAME = "Download Channel"

const val URL = "URL"
const val ID = "ID"
const val NAME = "NAME"

class DownloadService : Service() {

    private val downloadRepository: DownloadRepository by inject()
    private val progressChannel: ConflatedBroadcastChannel<ProgressEvent> by inject(named("progressChannel"))
    private val scope = CoroutineScope(Dispatchers.Main)
    private val workQueue = SafeQueue<DownloadTask>()

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        listenForProgressUpdates()
    }

    private fun listenForProgressUpdates() {
        scope.launch {
            progressChannel.asFlow()
                .collect {
                    updateNotification(it)
                }
        }
    }

    private fun updateNotification(progressEvent: ProgressEvent) {
        val topJob = workQueue.getTopTask() ?: return
        if (topJob.id != progressEvent.identifier) return

        val title = DEFAULT_NOTIFICATION_TITLE.plus("(${workQueue.size()})")
        val notification = buildNotification(title, topJob.name, progressEvent.getProgress())
        notifyNotification(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleStartCommand(intent)
        return START_NOT_STICKY
    }

    private fun handleStartCommand(intent: Intent?) {
        val url = intent?.extras?.getString(URL)
        val id = intent?.extras?.getString(ID)
        val name = intent?.extras?.getString(NAME).orEmpty()
        val dir = getPodcastDir()

        val notification = createNotification(name)
        startForeground(NOTIFICATION_ID, notification)

        if (url != null && id != null && dir != null) downloadFile(id, url, name, dir)
        else stopSelf()
    }

    private fun downloadFile(episodeId: String, url: String, name: String, dir: File) {
        val podcastFile = createNewFile(dir, episodeId.plus(".mp3"))

        scope.launch {
            workQueue.addTask(DownloadTask(episodeId, name, url))
            val downloadedPodcastId = async(Dispatchers.IO) {
                downloadRepository.downloadPodcast(episodeId, url, podcastFile)
            }
            workQueue.removeTask(downloadedPodcastId.await())

            if (workQueue.isEmpty()) stopSelf()
        }
    }

    private fun createNotification(podcastName: String): Notification {
        createChannel()
        return buildNotification(DEFAULT_NOTIFICATION_TITLE, podcastName, 0)
    }

    private fun buildNotification(title: String, podcastName: String, progress: Int): Notification {
        return NotificationCompat.Builder(this, DOWNLOAD_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(podcastName)
            .setSmallIcon(R.drawable.ic_queue)
            .setContentIntent(getPendingIntent())
            .setProgress(100, progress, false)
            .setSound(null)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun getPendingIntent(): PendingIntent? {
        val notificationIntent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(this, 0, notificationIntent, 0)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                DOWNLOAD_CHANNEL_ID,
                DOWNLOAD_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                setSound(null, null)
            }

            createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
