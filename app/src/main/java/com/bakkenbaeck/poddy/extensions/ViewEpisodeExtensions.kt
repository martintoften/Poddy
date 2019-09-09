package com.bakkenbaeck.poddy.extensions

import android.content.Context
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode

fun ViewEpisode.getEpisodePath(context: Context): String {
    return context.getPodcastDir()?.absolutePath
        .plus("/")
        .plus(id)
        .plus(".mp3")
}
