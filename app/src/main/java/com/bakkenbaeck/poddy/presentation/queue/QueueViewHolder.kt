package com.bakkenbaeck.poddy.presentation.queue

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.getColorFromAttr
import com.bakkenbaeck.poddy.extensions.loadWithRoundCorners
import com.bakkenbaeck.poddy.extensions.toDate
import com.bakkenbaeck.poddy.extensions.toSeconds
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.util.ItemTouchHelperViewHolder
import com.bakkenbaeck.poddy.util.OnStartDragListener
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.queue_item.*

class QueueViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
    LayoutContainer, ItemTouchHelperViewHolder {

    fun setItem(episode: ViewEpisode) {
        date.text = episode.pubDate.toDate()
        name.text = episode.title
        length.text = "${episode.duration.toSeconds()} min"

        image.loadWithRoundCorners(episode.image, R.dimen.radius_small)
    }

    fun setOnItemClickListener(item: ViewEpisode, onItemClickListener: (ViewEpisode) -> Unit) {
        containerView.setOnClickListener { onItemClickListener(item) }
    }

    fun setOnItemDragListener(dragStartListener: OnStartDragListener) {
        handle.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                dragStartListener.onStartDrag(this)
            }
            return@setOnTouchListener false
        }
    }

    override fun onItemSelected() {
        val color = containerView.context.getColorFromAttr(R.attr.colorPrimary)
        containerView.setBackgroundColor(color)
    }

    override fun onItemClear() {
        containerView.setBackgroundColor(0)
    }
}
