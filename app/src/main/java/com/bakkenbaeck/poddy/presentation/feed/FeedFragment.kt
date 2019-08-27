package com.bakkenbaeck.poddy.presentation.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.pop
import com.bakkenbaeck.poddy.model.Rss
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.feed_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

const val FEED_URL = "FEED_URL"
const val FEED_IMAGE = "FEED_IMAGE"

class FeedFragment : Fragment() {

    private val feedViewModel: FeedViewModel by viewModel()
    private lateinit var episodeAdapter: EpisodeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.feed_fragment, container, false)
    }

    override fun onViewCreated(view: View, inState: Bundle?) {
        super.onViewCreated(view, inState)
        init(arguments)
    }

    private fun init(arguments: Bundle?) {
        initAdapter()
        initView()
        getFeed(arguments)
        initObservers()
    }

    private fun initAdapter() {
        episodeAdapter = EpisodeAdapter()

        episodeList.apply {
            adapter = episodeAdapter
            layoutManager = LinearLayoutManager(this@FeedFragment.context)
        }
    }

    private fun initView() {
        backArrow.setOnClickListener { pop() }
    }

    private fun getFeed(arguments: Bundle?) {
        val feedUrl = arguments?.getString(FEED_URL) ?: return
        feedViewModel.getFeed(feedUrl)
    }

    private fun initObservers() {
        feedViewModel.feedResult.observe(this, Observer {
            handleFeedResult(it)
        })
    }

    private fun handleFeedResult(feed: Rss) {
        val channel = feed.channel
        val imageUrl = getFeedImage()
        val header = Header(
            title = channel.title,
            description = channel.description,
            image = imageUrl.orEmpty()
        )
        episodeAdapter.add(header, channel.itemList)
    }

    private fun getFeedImage(): String? {
        return arguments?.getString(FEED_IMAGE)
    }


    private fun test() {
        var bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_layout)
    }
}
