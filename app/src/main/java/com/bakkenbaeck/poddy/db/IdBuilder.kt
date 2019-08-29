package com.bakkenbaeck.poddy.db

import com.bakkenbaeck.poddy.model.Channel
import com.bakkenbaeck.poddy.util.HashUtil

class IdBuilder {
    fun buildQueueId(episode: Channel.Item, channel: Channel): String {
        val episodeGuid = episode.guid
        val episodeTitle = episode.title
        val channelTitle = channel.title

        if (episodeGuid != null) return episodeGuid
        if (episodeTitle == null && channelTitle == null) {
            throw IllegalStateException("No way to create a ID for queue item")
        }
        return HashUtil.sha1("$episodeTitle|$channelTitle")
    }

    fun buildChannelId(channel: Channel): String {
        val title = channel.title
        val description = channel.description

        if (title == null && description == null) {
            throw IllegalStateException("No way to create a ID channel item")
        }
        return HashUtil.sha1("$title|$description")
    }
}
