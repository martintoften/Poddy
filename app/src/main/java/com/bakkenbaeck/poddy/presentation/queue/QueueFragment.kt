package com.bakkenbaeck.poddy.presentation.queue

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.presentation.BackableFragment
import kotlinx.android.synthetic.main.queue_fragment.*
import org.db.Episode
import org.koin.androidx.viewmodel.ext.android.viewModel

class QueueFragment : BackableFragment() {

    private val queueViewModel: QueueViewModel by viewModel()
    private lateinit var queueAdapter: QueueAdapter

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
    }

    private fun initAdapter() {
        queueAdapter = QueueAdapter { handleEpisodeClicked(it) }

        queueList.apply {
            adapter = queueAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun handleEpisodeClicked(episodes: Episode) {
        Log.d("QueueFragment", episodes.toString())
    }

    private fun initObservers() {
        queueViewModel.queue.observe(this, Observer {
            handleQueue(it)
        })
    }

    private fun handleQueue(episodes: List<Episode>) {
        queueAdapter.addItems(episodes)
    }
}
