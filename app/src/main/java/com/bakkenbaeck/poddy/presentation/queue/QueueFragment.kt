package com.bakkenbaeck.poddy.presentation.queue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bakkenbaeck.poddy.presentation.player.ACTION_START
import com.bakkenbaeck.poddy.presentation.player.EPISODE
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.startForegroundService
import com.bakkenbaeck.poddy.presentation.BackableFragment
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.presentation.service.PlayerService
import com.bakkenbaeck.poddy.util.OnStartDragListener
import com.bakkenbaeck.poddy.util.SimpleItemTouchHelperCallback
import kotlinx.android.synthetic.main.queue_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class QueueFragment : BackableFragment(), OnStartDragListener {

    private val queueViewModel: QueueViewModel by viewModel()
    private var itemTouchHelper: ItemTouchHelper? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.queue_fragment, container, false)
    }

    override fun onViewCreated(view: View, inState: Bundle?) {
        super.onViewCreated(view, inState)
        init()
    }

    private fun init() {
        initAdapter()
        initObservers()
        initItemTouchHelper()
    }

    private fun initAdapter() {
        queueList.apply {
            adapter = QueueAdapter(
                this@QueueFragment,
                { queueViewModel.reorderQueue(it.map { ep -> ep.id }) },
                { queueViewModel.deleteEpisode(it.id) },
                { handleEpisodeClicked(it) }
            )

            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun handleEpisodeClicked(episode: ViewEpisode) {
        startForegroundService<PlayerService> {
            action = ACTION_START
            putExtra(EPISODE, episode)
        }
    }

    private fun initObservers() {
        queueViewModel.queue.observe(viewLifecycleOwner, Observer {
            handleQueue(it)
        })
    }

    private fun handleQueue(episodes: List<ViewEpisode>) {
        if (episodes.isNotEmpty()) {
            val adapter = queueList.adapter as? QueueAdapter?
            adapter?.addItems(episodes)
            emptyState.visibility = View.GONE
            queueList.visibility = View.VISIBLE
        } else {
            emptyState.visibility = View.VISIBLE
            queueList.visibility = View.GONE
        }
    }

    private fun initItemTouchHelper() {
        val adapter = queueList.adapter as? QueueAdapter? ?: return
        val callback = SimpleItemTouchHelperCallback(adapter)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper?.attachToRecyclerView(queueList)
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper?.startDrag(viewHolder)
    }

    override fun onDestroyView() {
        itemTouchHelper = null
        super.onDestroyView()
    }
}
