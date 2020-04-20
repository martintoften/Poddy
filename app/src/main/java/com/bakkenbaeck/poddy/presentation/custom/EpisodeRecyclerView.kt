package com.bakkenbaeck.poddy.presentation.custom

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.bakkenbaeck.poddy.extensions.findLastCompletelyVisibleItemPosition
import com.bakkenbaeck.poddy.extensions.isLastVisible

class EpisodeRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var lastSeenPosition: Int = -1
    var onLastElementListener: (() -> Unit)? = null

    init {
        initScrollListener()
    }

    private fun initScrollListener() {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val currentPosition = findLastCompletelyVisibleItemPosition()
                if (lastSeenPosition == currentPosition) return

                lastSeenPosition = findLastCompletelyVisibleItemPosition()
                val isLastVisible = isLastVisible()

                if (isLastVisible) {
                    onLastElementListener?.invoke()
                }
            }
        })
    }
}
