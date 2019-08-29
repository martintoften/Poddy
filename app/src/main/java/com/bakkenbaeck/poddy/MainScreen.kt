package com.bakkenbaeck.poddy

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.bakkenbaeck.poddy.presentation.queue.QueueFragment
import com.bakkenbaeck.poddy.presentation.search.SearchFragment

enum class MainScreen(
    @IdRes val menuItemId: Int,
    @DrawableRes val menuItemIconId: Int,
    @StringRes val titleStringId: Int,
    val fragment: Fragment
) {
    Search(R.id.search, R.drawable.ic_queue, R.string.search, NavHostFragment.create(R.navigation.nav_graph)),
    Queue(R.id.queue, R.drawable.ic_queue, R.string.queue, QueueFragment())
}

fun getMainScreenForMenuItem(menuItemId: Int): MainScreen? {
    for (mainScreen in MainScreen.values()) {
        if (mainScreen.menuItemId == menuItemId) {
            return mainScreen
        }
    }
    return null
}
