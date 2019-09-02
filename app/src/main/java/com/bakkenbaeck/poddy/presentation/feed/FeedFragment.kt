package com.bakkenbaeck.poddy.presentation.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import coil.api.load
import coil.transform.RoundedCornersTransformation
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.getDimen
import com.bakkenbaeck.poddy.network.model.EpisodeItem
import com.bakkenbaeck.poddy.network.model.PodcastResponse
import com.bakkenbaeck.poddy.presentation.BackableFragment
import com.bakkenbaeck.poddy.util.Failure
import com.bakkenbaeck.poddy.util.Loading
import com.bakkenbaeck.poddy.util.Success
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.detail_sheet.*
import kotlinx.android.synthetic.main.detail_sheet.view.*
import kotlinx.android.synthetic.main.feed_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

const val PODCAST_ID = "PODCAST_ID"
const val PODCAST_NAME = "PODCAST_NAME"
const val PODCAST_IMAGE = "PODCAST_IMAGE"
const val PODCAST_DESCRIPTION = "PODCAST_DESCRIPTION"

class FeedFragment : BackableFragment() {

    private fun getPodcastName(arguments: Bundle?): String? = arguments?.getString(PODCAST_NAME)
    private fun getPodcastImage(arguments: Bundle?): String? = arguments?.getString(PODCAST_IMAGE)
    private fun getPodcastDescription(arguments: Bundle?): String? = arguments?.getString(PODCAST_DESCRIPTION)
    private fun getPodcastId(arguments: Bundle?): String? = arguments?.getString(PODCAST_ID)

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
        getEpisodes(arguments)
        initObservers()
    }

    private fun initSheetView() {
        sheetBehavior = BottomSheetBehavior.from(sheet)
        sheet.play.setOnClickListener { Log.d("FeedFragment", "Play") }
        sheet.download.setOnClickListener { Log.d("FeedFragment", "Download") }
        sheet.queue.setOnClickListener { feedViewModel.addToQueue() }
    }

    private fun initAdapter() {
        episodeAdapter = EpisodeAdapter( { handleEpisodeClicked(it) }, { handleHeaderClicked() })

        episodeList.apply {
            adapter = episodeAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun handleEpisodeClicked(episode: EpisodeItem) {
        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            updateSheetStateToExpanded(episode)
        } else {
            updateSheetStateToCollapsed()
        }
    }

    private fun handleHeaderClicked() {
        feedViewModel.addPodcast()
    }

    private fun updateSheetStateToExpanded(episode: EpisodeItem) {
        val imageUrl = getPodcastImage(arguments)
        val radius by lazy { getDimen(R.dimen.radius_default) }
        val roundedCorners by lazy { RoundedCornersTransformation(radius) }

        sheet.image.load(imageUrl) { transformations(roundedCorners) }
        sheet.episodeName.text = episode.title
        sheet.description.text = HtmlCompat.fromHtml(
            episode.description,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        feedViewModel.setCurrentEpisode(episode)
    }

    private fun updateSheetStateToCollapsed() {
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun getEpisodes(arguments: Bundle?) {
        val podcastId = getPodcastId(arguments) ?: return
        feedViewModel.getFeed(podcastId)
    }

    private fun initObservers() {
        feedViewModel.feedResult.observe(this, Observer {
            when (it) {
                is Success<PodcastResponse> -> {
                    spinnerOverlay.isOverlayVisible(false)
                    handleFeedResult(it.data)
                }
                is Loading -> spinnerOverlay.isOverlayVisible(true)
                is Failure -> spinnerOverlay.isOverlayVisible(false)
            }
        })
    }

    private fun handleFeedResult(feed: PodcastResponse) {
        val imageUrl = getPodcastImage(arguments)
        val description = getPodcastDescription(arguments)
        val name = getPodcastName(arguments)
        val header = Header(
            title = name.orEmpty(),
            description = description.orEmpty(),
            image = imageUrl.orEmpty()
        )
        episodeAdapter.add(header, feed.episodes)
    }
}
