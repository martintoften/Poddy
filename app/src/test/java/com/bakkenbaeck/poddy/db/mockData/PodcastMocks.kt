package com.bakkenbaeck.poddy.db.mockData

import org.db.Podcast

val replyAllMock = Podcast.Impl(
    id = "11",
    title = "Reply all",
    description = "A podcast about the internet",
    image = "",
    total_episodes = 0
)

val radioresepsjonenMock = Podcast.Impl(
    id = "12",
    title = "Radioresepsjonen",
    description = "RR",
    image = "",
    total_episodes = 0
)

val serialMock = Podcast.Impl(
    id = "13",
    title = "Serial",
    description = "A case about Adnan Syed",
    image = "",
    total_episodes = 0
)

val podcastMockList = listOf(replyAllMock, radioresepsjonenMock, serialMock)