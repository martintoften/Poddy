package com.bakkenbaeck.poddy.db.mockData

import org.db.Queue

val queueItemReplyAllMock = Queue.Impl(
    episode_id = "1",
    channel_id = "11",
    queue_index = 0
)

val queueItemRRMock = Queue.Impl(
    episode_id = "2",
    channel_id = "12",
    queue_index = 0
)

val queueItemSerialMock = Queue.Impl(
    episode_id = "3",
    channel_id = "13",
    queue_index = 0
)

val queueMockList = listOf(queueItemReplyAllMock, queueItemRRMock, queueItemSerialMock)