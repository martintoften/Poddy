package com.bakkenbaeck.poddy.presentation.podcast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.presentation.BackableFragment
import kotlinx.android.synthetic.main.podcast_fragment.*
import org.db.Podcast
import org.koin.androidx.viewmodel.ext.android.viewModel

class PodcastFragment : BackableFragment() {

    private val podcastViewModel: PodcastViewModel by viewModel()
    private lateinit var podcastAdapter: PodcastAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.podcast_fragment, container, false)
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
        podcastAdapter = PodcastAdapter()

        podcastList.apply {
            adapter = podcastAdapter
            layoutManager = GridLayoutManager(context, 4)
        }
    }

    private fun initObservers() {
        podcastViewModel.podcasts.observe(this, Observer {
            handlePodcasts(it)
        })
    }

    private fun handlePodcasts(podcasts: List<Podcast>) {
        podcastAdapter.addItem(podcasts)
    }
}