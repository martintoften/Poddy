package com.bakkenbaeck.poddy.presentation.feed

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.getColorById
import com.bakkenbaeck.poddy.extensions.loadWithRoundCorners
import com.bakkenbaeck.poddy.extensions.pop
import com.bakkenbaeck.poddy.extensions.startForegroundService
import com.bakkenbaeck.poddy.presentation.BackableFragment
import com.bakkenbaeck.poddy.presentation.modal.DetailsFragment
import com.bakkenbaeck.poddy.presentation.model.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.feed_fragment, container, false)
    }

    override fun onViewCreated(view: View, inState: Bundle?) {
        super.onViewCreated(view, inState)
        init(arguments)
    }

    private fun init(arguments: Bundle?) {
        initTransition()
        initToolbar()
        initFloatingActionButton()
        initAdapter()
        getEpisodes(arguments)
    }

    private fun initTransition() {
        postponeEnterTransition()
    }

    private fun initToolbar() {
        toolbar.setOnBackClickedListener { pop() }
        podcastImage.apply {
            transitionName = getPodcastId(arguments)
            loadWithRoundCorners(getPodcastImage(arguments))
            doOnPreDraw { startPostponedEnterTransition() }
        }
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
        episodeList.apply {
            adapter =  EpisodeAdapter(
                { view, episode -> handleEpisodeClicked(view, episode) },
                { handleDownloadClicked(it) }
            )
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
        val adapter = episodeList.adapter as? EpisodeAdapter?
        val episode = adapter?.getLastItem() ?: return
        getFeed(podcastId, episode.pubDate)
    }

    private fun handleEpisodeClicked(view: View, episode: ViewEpisode) {
        val detailsFragment = DetailsFragment.newInstance(episode)
        detailsFragment.show(parentFragmentManager, DETAIL_TAG)
    }

    private fun getEpisodes(arguments: Bundle?) {
        val podcastId = getPodcastId(arguments) ?: return
        getFeed(podcastId)
    }

    abstract fun getFeed(podcastId: String, pubDate: Long? = null)

    protected fun handleFeedResult(podcast: ViewPodcast) {
        val adapter = episodeList.adapter as? EpisodeAdapter?
        adapter?.setItems(podcast.episodes)
    }

    protected fun updateSubscriptionState(subscriptionState: SubscriptionState) {
        when (subscriptionState) {
            is Unsubscribed -> {
                subscribeButton.setImageResource(R.drawable.ic_check_24px)
                subscribeButton.backgroundTintList = ColorStateList.valueOf(getColorById(R.color.positive))
            }
            is Subscribed -> {
                subscribeButton.setImageResource(R.drawable.ic_clear_24px)
                subscribeButton.backgroundTintList = ColorStateList.valueOf(getColorById(R.color.negative))
            }
        }
    }
}
