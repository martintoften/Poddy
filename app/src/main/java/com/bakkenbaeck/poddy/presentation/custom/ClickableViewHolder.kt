package com.bakkenbaeck.poddy.presentation.custom

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

abstract class ClickableViewHolder<T>(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun setOnItemClickedListener(podcast: T, onItemClickedListener: (View, T) -> Unit) {
        containerView.setOnClickListener { onItemClickedListener(it, podcast) }
    }
}
