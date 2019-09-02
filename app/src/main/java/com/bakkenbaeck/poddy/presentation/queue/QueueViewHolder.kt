package com.bakkenbaeck.poddy.presentation.queue

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.RoundedCornersTransformation
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.getDimen
import com.bakkenbaeck.poddy.extensions.toDate
import com.bakkenbaeck.poddy.extensions.toSeconds
import com.bakkenbaeck.poddy.util.*
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.queue_item.*
import org.db.Episode

class QueueViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
    LayoutContainer, ItemTouchHelperViewHolder {

    private val radius by lazy { containerView.getDimen(R.dimen.radius_small) }
    private val roundedCorners by lazy { RoundedCornersTransformation(radius) }

    fun setItem(episode: Episode) {
        date.text = episode.pub_date.toDate()
        name.text = episode.title
        length.text = "${episode.duration.toSeconds()} min"

        image.load(episode.image) {
            crossfade(true)
            transformations(roundedCorners)
        }
    }

    fun setOnItemClickListener(item: Episode, onItemClickListener: (Episode) -> Unit) {
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
        containerView.setBackgroundResource(R.color.colorPrimary)
    }

    override fun onItemClear() {
        containerView.setBackgroundColor(0)
    }
}
