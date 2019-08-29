package com.bakkenbaeck.poddy.presentation.queue

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.layoutInflater
import org.db.Episode

class QueueAdapter(
    private val onItemClickListener: (Episode) -> Unit
) : RecyclerView.Adapter<QueueViewHolder>() {

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
        }
    }
}
