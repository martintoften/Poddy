package com.bakkenbaeck.poddy.db.mockData

import org.db.Subscription

val replyAllSubMock = Subscription.Impl(
    channel_id = "11"
)

val radioresepsjonenSubMock = Subscription.Impl(
    channel_id = "12"
)

val serialSubMock = Subscription.Impl(
    channel_id = "13"
)

val subMockList = listOf(replyAllSubMock, radioresepsjonenSubMock, serialSubMock)