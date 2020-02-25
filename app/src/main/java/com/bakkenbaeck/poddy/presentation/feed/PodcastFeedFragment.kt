package com.bakkenbaeck.poddy.presentation.feed

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.bakkenbaeck.poddy.util.Success
import org.koin.androidx.viewmodel.ext.android.viewModel

class PodcastFeedFragment : FeedFragment() {
    private val viewModel by viewModel<PodcastFeedViewModel>()

    override fun getFeed(podcastId: String, pubDate: Long?) {
        viewModel.getFeed(podcastId, pubDate)
    }

    override fun onViewCreated(view: View, inState: Bundle?) {
        super.onViewCreated(view, inState)
        init()
    }

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
    }

    override fun subscribe() {
        viewModel.addPodcast()
    }
}
