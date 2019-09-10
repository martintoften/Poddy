package com.bakkenbaeck.poddy.extensions

import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction

fun ViewPlayerAction.getPlayIcon(): Int {
    return when (this) {
        is ViewPlayerAction.Start -> R.drawable.ic_player_pause
        is ViewPlayerAction.Play -> R.drawable.ic_player_pause
        is ViewPlayerAction.Pause -> R.drawable.ic_player_play
    }
}
