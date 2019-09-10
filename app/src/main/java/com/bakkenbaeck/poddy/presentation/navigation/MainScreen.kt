package com.bakkenbaeck.poddy.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.presentation.queue.QueueFragment

enum class MainScreen(
    @IdRes val menuItemId: Int,
    @DrawableRes val menuItemIconId: Int,
    @StringRes val titleStringId: Int,
    val fragment: Fragment
) {
    Search(
        R.id.search,
        R.drawable.ic_queue,
        R.string.search, NavHostFragment.create(R.navigation.nav_graph)),
    Queue(
        R.id.queue,
        R.drawable.ic_queue,
        R.string.queue, QueueFragment()),
    Podcast(
        R.id.podcasts,
        R.drawable.ic_queue,
        R.string.podcasts, NavHostFragment.create(R.navigation.podcast_graph))
}

fun getMainScreenForMenuItem(menuItemId: Int): MainScreen? {
    for (mainScreen in MainScreen.values()) {
        if (mainScreen.menuItemId == menuItemId) {
            return mainScreen
        }
    }
    return null
}
