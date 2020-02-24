package com.bakkenbaeck.poddy.presentation.queue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bakkenbaeck.poddy.ACTION_START
import com.bakkenbaeck.poddy.EPISODE
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.startForegroundService
import com.bakkenbaeck.poddy.presentation.BackableFragment
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.service.PlayerService
import com.bakkenbaeck.poddy.util.OnStartDragListener
import com.bakkenbaeck.poddy.util.SimpleItemTouchHelperCallback
import kotlinx.android.synthetic.main.queue_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class QueueFragment : BackableFragment(), OnStartDragListener {

    private val queueViewModel: QueueViewModel by viewModel()
    private lateinit var queueAdapter: QueueAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.queue_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initAdapter()
        initObservers()
        initItemTouchHelper()
    }

    private fun initAdapter() {
        queueAdapter = QueueAdapter(
            this,
            { queueViewModel.reorderQueue(it) },
            { queueViewModel.deleteEpisode(it) },
            { handleEpisodeClicked(it) }
        )

        queueList.apply {
            adapter = queueAdapter
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
        queueAdapter.addItems(episodes)
    }

    private fun initItemTouchHelper() {
        val callback = SimpleItemTouchHelperCallback(queueAdapter)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(queueList)
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }
}
