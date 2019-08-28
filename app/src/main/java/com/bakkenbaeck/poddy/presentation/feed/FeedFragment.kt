package com.bakkenbaeck.poddy.presentation.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import coil.api.load
import coil.transform.RoundedCornersTransformation
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.getDimen
import com.bakkenbaeck.poddy.model.Channel
import com.bakkenbaeck.poddy.model.Rss
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.detail_sheet.*
import kotlinx.android.synthetic.main.detail_sheet.view.*
import kotlinx.android.synthetic.main.feed_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

const val FEED_URL = "FEED_URL"
const val FEED_IMAGE = "FEED_IMAGE"

class FeedFragment : Fragment() {

    private val feedViewModel: FeedViewModel by viewModel()
    private lateinit var episodeAdapter: EpisodeAdapter
    private lateinit var sheetBehavior: BottomSheetBehavior<NestedScrollView>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.feed_fragment, container, false)
    }

    override fun onViewCreated(view: View, inState: Bundle?) {
        super.onViewCreated(view, inState)
        init(arguments)
    }

    private fun init(arguments: Bundle?) {
        initSheetView()
        initAdapter()
        getFeed(arguments)
        initObservers()
    }

    private fun initSheetView() {
        sheetBehavior = BottomSheetBehavior.from(sheet)
        sheet.play.setOnClickListener { Log.d("FeedFragment", "Play") }
        sheet.download.setOnClickListener { Log.d("FeedFragment", "Download") }
        sheet.queue.setOnClickListener { Log.d("FeedFragment", "Queue") }
    }

    private fun initAdapter() {
        episodeAdapter = EpisodeAdapter { handleEpisodeClicked(it) }

        episodeList.apply {
            adapter = episodeAdapter
            layoutManager = LinearLayoutManager(this@FeedFragment.context)
        }
    }

    private fun handleEpisodeClicked(episode: Channel.Item) {
        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            updateSheetStateToExpanded(episode)
        } else {
            updateSheetStateToCollapsed()
        }
    }

    private fun updateSheetStateToExpanded(episode: Channel.Item) {
        val imageUrl = getFeedImage()
        val radius by lazy { getDimen(R.dimen.radius_default) }
        val roundedCorners by lazy { RoundedCornersTransformation(radius) }

        sheet.image.load(imageUrl) { transformations(roundedCorners) }
        sheet.episodeName.text = episode.title.orEmpty()
        sheet.description.text = HtmlCompat.fromHtml(
            episode.description.orEmpty(),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun updateSheetStateToCollapsed() {
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
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
}
