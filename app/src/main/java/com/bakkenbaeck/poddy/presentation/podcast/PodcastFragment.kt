package com.bakkenbaeck.poddy.presentation.podcast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.GridLayoutManager
import coil.Coil
import coil.api.get
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.navigate
import com.bakkenbaeck.poddy.presentation.BackableFragment
import com.bakkenbaeck.poddy.presentation.feed.PODCAST_DESCRIPTION
import com.bakkenbaeck.poddy.presentation.feed.PODCAST_ID
import com.bakkenbaeck.poddy.presentation.feed.PODCAST_IMAGE
import com.bakkenbaeck.poddy.presentation.feed.PODCAST_TITLE
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import kotlinx.android.synthetic.main.podcast_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PodcastFragment : BackableFragment() {

    private val podcastViewModel: PodcastViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.podcast_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initTransition()
        initAdapter()
        initObservers()
    }

    private fun initTransition() {
        postponeEnterTransition()
        podcastList.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    private fun initAdapter() {
        podcastList.apply {
            adapter = PodcastAdapter { view, podcast -> goTo(view, podcast) }
            layoutManager = GridLayoutManager(context, 4)
        }
    }

    private fun goTo(view: View, podcast: ViewPodcast) {
        val bundle = Bundle().apply {
            putString(PODCAST_ID, podcast.id)
            putString(PODCAST_IMAGE, podcast.image)
            putString(PODCAST_TITLE, podcast.title)
            putString(PODCAST_DESCRIPTION, podcast.description)
        }
        val extras = FragmentNavigatorExtras(view to podcast.id)
        navigate(id = R.id.to_details_fragment, args = bundle, extras = extras)
    }

    private fun initObservers() {
        podcastViewModel.podcasts.observe(viewLifecycleOwner, Observer {
            handlePodcasts(it)
        })
    }

    private fun handlePodcasts(podcasts: List<ViewPodcast>) {
        val adapter = podcastList.adapter as? PodcastAdapter?
        adapter?.addItems(podcasts)

        loadImages(podcasts)
    }

    private fun loadImages(podcasts: List<ViewPodcast>) {
        lifecycleScope.launch(Dispatchers.IO) {
            podcasts.forEach {
                Coil.get(it.image)
            }
        }
    }
}
