package com.bakkenbaeck.poddy.presentation.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.getPlayIcon
import com.bakkenbaeck.poddy.extensions.loadWithRoundCorners
import com.bakkenbaeck.poddy.extensions.pop
import com.bakkenbaeck.poddy.extensions.startForegroundService
import com.bakkenbaeck.poddy.network.ProgressEvent
import com.bakkenbaeck.poddy.presentation.BackableFragment
import com.bakkenbaeck.poddy.presentation.modal.DetailsFragment
import com.bakkenbaeck.poddy.presentation.model.ViewEpisode
import com.bakkenbaeck.poddy.presentation.model.ViewPlayerAction
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import com.bakkenbaeck.poddy.service.DownloadService
import com.bakkenbaeck.poddy.service.ID
import com.bakkenbaeck.poddy.service.NAME
import com.bakkenbaeck.poddy.service.URL
import com.bakkenbaeck.poddy.util.Success
import kotlinx.android.synthetic.main.detail_sheet.*
import kotlinx.android.synthetic.main.detail_sheet.view.*
import kotlinx.android.synthetic.main.feed_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

const val PODCAST_ID = "PODCAST_ID"
const val PODCAST_IMAGE = "PODCAST_IMAGE"
const val PODCAST_TITLE = "PODCAST_TITLE"
const val DETAIL_TAG = "DETAIL_TAG"

class FeedFragment : BackableFragment() {

    private fun getPodcastId(arguments: Bundle?): String? = arguments?.getString(PODCAST_ID)
    private fun getPodcastImage(arguments: Bundle?): String? = arguments?.getString(PODCAST_IMAGE)
    private fun getPodcastTitle(arguments: Bundle?): String? = arguments?.getString(PODCAST_TITLE)

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
        initToolbar()
        initAdapter()
        getEpisodes(arguments)
        initObservers()
    }

    private fun initToolbar() {
        toolbar.setOnBackClickedListener { pop() }
        podcastImage.loadWithRoundCorners(getPodcastImage(arguments))
        title.text = getPodcastTitle(arguments)
    }

    private fun initAdapter() {
        episodeAdapter = EpisodeAdapter(
            { handleEpisodeClicked(it) },
            { handleDownloadClicked(it) }
        )

        episodeList.apply {
            adapter = episodeAdapter
            layoutManager = LinearLayoutManager(context)
            onLastElementListener = { handleOnLastElement() }
        }
    }

    private fun handleDownloadClicked(episode: ViewEpisode) {
        startForegroundService<DownloadService> {
            putExtra(ID, episode.id)
            putExtra(URL, episode.audio)
            putExtra(NAME, episode.title)
        }
    }

    private fun handleOnLastElement() {
        val podcastId = getPodcastId(arguments) ?: return
        val episode = episodeAdapter.getLastItem() ?: return
        feedViewModel.getFeed(podcastId, episode.pubDate)
    }

    private fun handleEpisodeClicked(episode: ViewEpisode) {
        val detailsFragment = DetailsFragment.newInstance(episode)
        detailsFragment.show(parentFragmentManager, DETAIL_TAG)
    }

    private fun getEpisodes(arguments: Bundle?) {
        val podcastId = getPodcastId(arguments) ?: return
        feedViewModel.getFeed(podcastId)
    }

    private fun initObservers() {
        feedViewModel.feedResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Success -> handleFeedResult(it.data)
            }
        })
        feedViewModel.downloadUpdates.observe(viewLifecycleOwner, Observer {
            handleDownloadUpdates(it)
        })
        feedViewModel.playerUpdates.observe(viewLifecycleOwner, Observer {
            handlePlayerUpdates(it)
        })
    }

    private fun handleFeedResult(podcast: ViewPodcast) {
        episodeAdapter.add(podcast.episodes)
    }

    private fun handleDownloadUpdates(progressEvent: ProgressEvent) {
        val selectedEpisode = feedViewModel.selectedEpisode ?: return

        if (selectedEpisode.id == progressEvent.identifier) {
            sheet.downloadProgress.visibility = View.VISIBLE
            sheet.downloadProgress.text = progressEvent.getFormattedProgress()
        }
    }

    private fun handlePlayerUpdates(action: ViewPlayerAction) {
        val drawable = action.getPlayIcon() ?: return
        sheet.play.setImageResource(drawable)
    }
}
