package com.bakkenbaeck.poddy.extensions

import android.content.Context
import com.bakkenbaeck.poddy.App
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode

fun ViewEpisode.getEpisodePath(context: Context = App.instance.applicationContext): String {
    return context.getPodcastDir()?.absolutePath
        .plus("/")
        .plus(id)
        .plus(".mp3")
}
