package com.bakkenbaeck.poddy.db.mockData

import org.db.Episode

val replyAllMockEpisode = Episode.Impl(
    id = "1",
    podcast_id = "11",
    title = "Snapchat thief",
    description = "Snapchat",
    pub_date = 1000,
    audio = "url",
    duration = 1000,
    image = "",
    timestamp = 1000L,
    is_downloaded = 0,
    progress = 0
)

val replyAll2MockEpisode = Episode.Impl(
    id = "4",
    podcast_id = "11",
    title = "Pizza gate",
    description = "",
    pub_date = 1000,
    audio = "url",
    duration = 1000,
    image = "",
    timestamp = 1000L,
    is_downloaded = 0,
    progress = 0
)

val radioresepsjonenMockEpisode = Episode.Impl(
    id = "2",
    podcast_id = "12",
    title = "01.05.2020",
    description = "RR",
    pub_date = 1000,
    audio = "url",
    duration = 1000,
    image = "",
    timestamp = 1000L,
    is_downloaded = 0,
    progress = 0
)

val serialMockEpisode = Episode.Impl(
    id = "3",
    podcast_id = "13",
    title = "The alibi",
    description = "Baltimore 1999...",
    pub_date = 1000,
    audio = "url",
    duration = 1000,
    image = "",
    timestamp = 1000L,
    is_downloaded = 0,
    progress = 0
)

val episodeMockList = listOf(replyAllMockEpisode, radioresepsjonenMockEpisode, serialMockEpisode)
val multiplePerPodcastMockList = listOf(replyAllMockEpisode, replyAll2MockEpisode, radioresepsjonenMockEpisode, serialMockEpisode)