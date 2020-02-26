package com.bakkenbaeck.poddy.presentation.podcast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.navigate
import com.bakkenbaeck.poddy.presentation.BackableFragment
import com.bakkenbaeck.poddy.presentation.feed.PODCAST_DESCRIPTION
import com.bakkenbaeck.poddy.presentation.feed.PODCAST_ID
import com.bakkenbaeck.poddy.presentation.feed.PODCAST_IMAGE
import com.bakkenbaeck.poddy.presentation.feed.PODCAST_TITLE
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import kotlinx.android.synthetic.main.podcast_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PodcastFragment : BackableFragment() {

    private val podcastViewModel: PodcastViewModel by viewModel()

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
        podcastList.apply {
            adapter = PodcastAdapter { goTo(it) }
            layoutManager = GridLayoutManager(context, 4)
        }
    }

    private fun goTo(podcast: ViewPodcast) {
        val bundle = Bundle().apply {
            putString(PODCAST_ID, podcast.id)
            putString(PODCAST_IMAGE, podcast.image)
            putString(PODCAST_TITLE, podcast.title)
            putString(PODCAST_DESCRIPTION, podcast.description)
        }
        navigate(R.id.to_details_fragment, bundle)
    }

    private fun initObservers() {
        podcastViewModel.podcasts.observe(viewLifecycleOwner, Observer {
            handlePodcasts(it)
        })
    }

    private fun handlePodcasts(podcasts: List<ViewPodcast>) {
        val adapter = podcastList.adapter as? PodcastAdapter?
        adapter?.addItems(podcasts)
    }
}
