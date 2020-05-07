package com.bakkenbaeck.poddy.player.mockData

import com.bakkenbaeck.poddy.presentation.model.DownloadState
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode

val replyAllMockEpisode = ViewEpisode(
    id = "1",
    podcastId = "11",
    title = "Snapchat thief",
    description = "Snapchat",
    pubDate = 1000,
    audio = "url",
    duration = 1000,
    image = "",
    isDownloaded = DownloadState.DOWNLOADED,
    progress = 0,
    podcastTitle = ""
)

val radioresepsjonenMockEpisode = ViewEpisode(
    id = "2",
    podcastId = "12",
    title = "01.05.2020",
    description = "RR",
    pubDate = 1000,
    audio = "url",
    duration = 1000,
    image = "",
    isDownloaded = DownloadState.DOWNLOADED,
    progress = 0,
    podcastTitle = ""
)

val serialMockEpisode = ViewEpisode(
    id = "3",
    podcastId = "13",
    title = "The alibi",
    description = "Baltimore 1999...",
    pubDate = 1000,
    audio = "url",
    duration = 1000,
    image = "",
    isDownloaded = DownloadState.DOWNLOADED,
    progress = 0,
    podcastTitle = ""
)

val viewEpisodeMockList = listOf(replyAllMockEpisode, radioresepsjonenMockEpisode, serialMockEpisode)