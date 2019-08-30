package com.bakkenbaeck.poddy.presentation.queue

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.layoutInflater
import com.bakkenbaeck.poddy.util.ItemTouchHelperAdapter
import com.bakkenbaeck.poddy.util.OnStartDragListener
import org.db.Episode
import java.util.*

class QueueAdapter(
    private val dragStartListener: OnStartDragListener,
    private val onQueueUpdated: (List<Episode>) -> Unit,
    private val onItemClickListener: (Episode) -> Unit
) : RecyclerView.Adapter<QueueViewHolder>(), ItemTouchHelperAdapter {

    private val items by lazy { mutableListOf<Episode>() }

    fun addItems(episodes: List<Episode>) {
        items.clear()
        items.addAll(episodes)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueViewHolder {
        val view = parent.layoutInflater().inflate(R.layout.queue_item, parent, false)
        return QueueViewHolder(view)
    }

    override fun getItemCount() = items.count()

    override fun onBindViewHolder(holder: QueueViewHolder, position: Int) {
       val item = items[position]

        holder.apply {
            setItem(item)
            setOnItemClickListener(item, onItemClickListener)
            setOnItemDragListener(dragStartListener)
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(items, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        onQueueUpdated(items)
        return true
    }

    override fun onItemDismiss(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }
}