package com.bakkenbaeck.poddy.presentation.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bakkenbaeck.poddy.extensions.navigate
import com.bakkenbaeck.poddy.presentation.feed.factory.PodcastFeedFactory
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import com.bakkenbaeck.poddy.util.Success
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class PodcastFeedFragment : FeedFragment() {
    private val factory by inject<PodcastFeedFactory> { parametersOf(basePodcast?.id) }
    private val viewModel by viewModels<PodcastFeedViewModel> { factory }

    override fun getFeed(podcastId: String, pubDate: Long?) {
        viewModel.getFeed(podcastId, pubDate)
    }

    override fun onViewCreated(view: View, inState: Bundle?) {
        super.onViewCreated(view, inState)
        init()
    }

    override fun showPodcastDetails(podcast: ViewPodcast) {
        val directions = PodcastFeedFragmentDirections.toPodcastDetailsFragment(podcast)
        navigate(directions)
    }

    override fun showEpisodeDetails(episode: ViewEpisode) {
        val directions = PodcastFeedFragmentDirections.toEpisodeDetailsFragment(episode)
        navigate(directions)
    }

    override fun getPodcast() = viewModel.getPodcast()

    private fun init() {
        initObservers()
    }

    private fun initObservers() {
        viewModel.feedResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Success -> handleFeedResult(it.data)
            }
        })

        viewModel.subscriptionState.observe(viewLifecycleOwner, Observer {
            updateSubscriptionState(it)
        })

        viewModel.downloadResult.observe(viewLifecycleOwner, Observer {
            handleEpisodeUpdate(it)
        })

        viewModel.downloadProgress.observe(viewLifecycleOwner, Observer {
            handleEpisodeUpdate(it)
        })
    }

    override fun subscribe() {
        viewModel.addPodcast()
    }
}
