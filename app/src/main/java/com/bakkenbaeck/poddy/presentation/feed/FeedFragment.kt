package com.bakkenbaeck.poddy.presentation.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bakkenbaeck.poddy.R
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
import kotlinx.android.synthetic.main.feed_fragment.*

const val PODCAST_ID = "PODCAST_ID"
const val PODCAST_IMAGE = "PODCAST_IMAGE"
const val PODCAST_TITLE = "PODCAST_TITLE"
const val PODCAST_DESCRIPTION = "PODCAST_DESCRIPTION"
const val DETAIL_TAG = "DETAIL_TAG"

abstract class FeedFragment : BackableFragment() {

    private fun getPodcastId(arguments: Bundle?): String? = arguments?.getString(PODCAST_ID)
    private fun getPodcastImage(arguments: Bundle?): String? = arguments?.getString(PODCAST_IMAGE)
    private fun getPodcastTitle(arguments: Bundle?): String? = arguments?.getString(PODCAST_TITLE)
    private fun getPodcastDescription(arguments: Bundle?): String? = arguments?.getString(PODCAST_DESCRIPTION)

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
        initFloatingActionButton()
        initAdapter()
        getEpisodes(arguments)
    }

    private fun initToolbar() {
        toolbar.setOnBackClickedListener { pop() }
        podcastImage.loadWithRoundCorners(getPodcastImage(arguments))
        toolbar.setText(getPodcastTitle(arguments).orEmpty())
        val description = HtmlCompat.fromHtml(
            getPodcastDescription(arguments).orEmpty(),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        podcastDescription.text = description
    }

    private fun initFloatingActionButton() {
        subscribeButton.setOnClickListener { subscribe() }
    }

    abstract fun subscribe()

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
        getFeed(podcastId, episode.pubDate)
    }

    private fun handleEpisodeClicked(episode: ViewEpisode) {
        val detailsFragment = DetailsFragment.newInstance(episode)
        detailsFragment.show(parentFragmentManager, DETAIL_TAG)
    }

    private fun getEpisodes(arguments: Bundle?) {
        val podcastId = getPodcastId(arguments) ?: return
        getFeed(podcastId)
    }

    abstract fun getFeed(podcastId: String, pubDate: Long? = null)

    protected fun handleFeedResult(podcast: ViewPodcast) {
        episodeAdapter.setItems(podcast.episodes)
    }
}
