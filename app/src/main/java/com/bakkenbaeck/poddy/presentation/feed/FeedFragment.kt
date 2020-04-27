package com.bakkenbaeck.poddy.presentation.feed

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.transition.doOnEnd
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.LinearLayoutManager
import coil.api.load
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.*
import com.bakkenbaeck.poddy.presentation.BackableFragment
import com.bakkenbaeck.poddy.presentation.model.*
import com.bakkenbaeck.poddy.service.DownloadService
import com.bakkenbaeck.poddy.service.ID
import com.bakkenbaeck.poddy.service.NAME
import com.bakkenbaeck.poddy.service.URL
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.android.synthetic.main.feed_fragment.*

abstract class FeedFragment : BackableFragment() {

    private val basePodcast: ViewBasePodcast? by lazy(mode = LazyThreadSafetyMode.NONE) {
        val arguments = arguments ?: return@lazy null
        val args = PodcastFeedFragmentArgs.fromBundle(arguments)
        return@lazy args.podcast
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = 500
            fadeMode = MaterialContainerTransform.FADE_MODE_OUT
            startShapeAppearanceModel = ShapeAppearanceModel().withCornerSize(0f)
            endShapeAppearanceModel =
                ShapeAppearanceModel().withCornerSize(getDimen(R.dimen.radius_default))
            scrimColor = Color.TRANSPARENT

            // Load the cached image with corner radius when the transition is done
            doOnEnd {
                podcastImage.loadWithRoundCorners(basePodcast?.image, R.dimen.radius_default_coil)
            }
        }

        sharedElementReturnTransition = MaterialContainerTransform().apply {
            duration = 500
            startShapeAppearanceModel =
                ShapeAppearanceModel().withCornerSize(getDimen(R.dimen.radius_default))
            endShapeAppearanceModel = ShapeAppearanceModel().withCornerSize(0f)
            scrimColor = Color.TRANSPARENT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.feed_fragment, container, false)
    }

    override fun onViewCreated(view: View, inState: Bundle?) {
        super.onViewCreated(view, inState)
        init()
    }

    private fun init() {
        initTransition()
        initClickListener()
        initToolbar()
        initFloatingActionButton()
        initAdapter()
        getEpisodes()
    }

    private fun initTransition() {
        postponeEnterTransition()
    }

    private fun initClickListener() {
        podcastDescription.setOnClickListener { showPodcastDetailFragment() }
        podcastImage.setOnClickListener { showPodcastDetailFragment() }
    }

    private fun showPodcastDetailFragment() {
        val podcast = getPodcast() ?: return
        showPodcastDetails(podcast)
    }

    abstract fun showPodcastDetails(podcast: ViewPodcast)
    abstract fun showEpisodeDetails(episode: ViewEpisode)
    abstract fun getPodcast(): ViewPodcast?

    private fun initToolbar() {
        loadImage()
        toolbar.apply {
            setOnBackClickedListener { pop() }
            setText(basePodcast?.title.orEmpty())
        }
        val description = HtmlCompat.fromHtml(
            basePodcast?.description.orEmpty(),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        podcastDescription.text = description
    }

    private fun initFloatingActionButton() {
        if (!hasAnimatedFab) {
            subscribeButton.scaleX = 0.0f
            subscribeButton.scaleY = 0.0f
        }

        subscribeButton.setOnClickListener { subscribe() }
    }

    private fun loadImage() {
        podcastImage.apply {
            transitionName = basePodcast?.id
            load(basePodcast?.image)
            doOnPreDraw {
                startPostponedEnterTransition()
            }
        }

        // Create a invisible image to load the image into. We need the cached image with corner radius
        podcastImagePlaceHolder.apply {
            loadWithRoundCorners(basePodcast?.image, R.dimen.radius_default_coil)
        }
    }

    abstract fun subscribe()

    private fun initAdapter() {
        episodeList.apply {
            adapter = EpisodeAdapter(
                { _, episode -> showEpisodeDetails(episode) },
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
        val podcastId = basePodcast?.id ?: return
        val adapter = episodeList.adapter as? EpisodeAdapter?
        val episode = adapter?.getLastItem() ?: return
        getFeed(podcastId, episode.pubDate)
    }

    private fun getEpisodes() {
        val podcastId = basePodcast?.id ?: return
        getFeed(podcastId)
    }

    abstract fun getFeed(podcastId: String, pubDate: Long? = null)

    protected fun handleFeedResult(podcast: ViewPodcast) {
        val adapter = episodeList.adapter as? EpisodeAdapter?
        adapter?.setItems(podcast.episodes)
    }

    protected fun handleEpisodeUpdate(episode: ViewEpisode) {
        val adapter = episodeList.adapter as? EpisodeAdapter?
        adapter?.setItem(episode)
    }

    protected fun updateSubscriptionState(subscriptionState: SubscriptionState) {
        when (subscriptionState) {
            is Unsubscribed -> {
                subscribeButton.setImageResource(R.drawable.ic_check_24px)
                subscribeButton.backgroundTintList =
                    ColorStateList.valueOf(getColorById(R.color.positive))
            }
            is Subscribed -> {
                subscribeButton.setImageResource(R.drawable.ic_clear_24px)
                subscribeButton.backgroundTintList =
                    ColorStateList.valueOf(getColorById(R.color.negative))
            }
        }

        subscribeButton.show()
        animateSubscribeButton()
    }

    private var hasAnimatedFab = false

    private fun animateSubscribeButton() {
        if (hasAnimatedFab) return

        AnimatorSet().apply {
            duration = 300
            playTogether(
                ObjectAnimator.ofFloat(subscribeButton, "scaleX", 1f),
                ObjectAnimator.ofFloat(subscribeButton, "scaleY", 1f)
            )
        }.start()

        hasAnimatedFab = true
    }
}
