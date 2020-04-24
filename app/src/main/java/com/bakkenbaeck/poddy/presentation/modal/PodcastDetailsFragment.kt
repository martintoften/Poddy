package com.bakkenbaeck.poddy.presentation.modal

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.presentation.feed.PodcastDetailsModelFactory
import com.bakkenbaeck.poddy.presentation.feed.PodcastDetailsViewModel
import com.bakkenbaeck.poddy.presentation.feed.RecommendationAdapter
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import com.bakkenbaeck.poddy.util.Failure
import com.bakkenbaeck.poddy.util.Loading
import com.bakkenbaeck.poddy.util.Resource
import com.bakkenbaeck.poddy.util.Success
import kotlinx.android.synthetic.main.podcast_detail_sheet.*
import org.koin.android.ext.android.get

class PodcastDetailsFragment : BaseBottomDialogFragment() {

    private val viewModel: PodcastDetailsViewModel by viewModels {
        val podcast = getPodcast()
        PodcastDetailsModelFactory(get(), podcast, this)
    }

    override fun getLayout() = R.layout.podcast_detail_sheet

    override fun init(bundle: Bundle?) {
        initDescription()
        initAdapter()
        initObservers()
    }

    private fun initDescription() {
        val podcast = getPodcast()
        description.text = podcast?.description.orEmpty()
    }

    private fun getPodcast(): ViewPodcast? {
        val arguments = arguments ?: return null
        val args = PodcastDetailsFragmentArgs.fromBundle(arguments)
        return args.podcast
    }

    private fun initAdapter() {
        recommendations.apply {
            adapter = RecommendationAdapter()
            layoutManager = GridLayoutManager(context, 4)
        }
    }

    private fun initObservers() {
        viewModel.recommendationsResult.observe(viewLifecycleOwner, Observer {
            handleRecommendations(it)
        })
    }

    private fun handleRecommendations(result: Resource<List<ViewPodcast>>) {
        when (result) {
            is Success -> {
                val adapter = recommendations.adapter as? RecommendationAdapter?
                adapter?.setItems(result.data)
            }
            is Loading -> {}
            is Failure -> {}
        }
    }
}