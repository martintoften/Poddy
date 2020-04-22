package com.bakkenbaeck.poddy.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.hideKeyboard
import com.bakkenbaeck.poddy.extensions.navigate
import com.bakkenbaeck.poddy.presentation.BackableFragment
import com.bakkenbaeck.poddy.presentation.feed.PODCAST_DESCRIPTION
import com.bakkenbaeck.poddy.presentation.feed.PODCAST_ID
import com.bakkenbaeck.poddy.presentation.feed.PODCAST_IMAGE
import com.bakkenbaeck.poddy.presentation.feed.PODCAST_TITLE
import com.bakkenbaeck.poddy.presentation.model.ViewPodcastSearch
import com.bakkenbaeck.poddy.presentation.model.ViewPodcastSearchItem
import com.bakkenbaeck.poddy.util.TextListener
import kotlinx.android.synthetic.main.search_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : BackableFragment() {

    private val viewModel: SearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initTransition()
        initView()
        initAdapter()
        initObservers()
    }

    private fun initTransition() {
        postponeEnterTransition()
        searchList.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    private fun initAdapter() {
        searchList.apply {
            adapter = SearchAdapter { view, podcast -> goTo(view, podcast) }
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun goTo(view: View, searchItem: ViewPodcastSearchItem) {
        val bundle = Bundle().apply {
            putString(PODCAST_ID, searchItem.id)
            putString(PODCAST_IMAGE, searchItem.image)
            putString(PODCAST_TITLE, searchItem.title)
            putString(PODCAST_DESCRIPTION, searchItem.description)
        }
        val extras = FragmentNavigatorExtras(view to searchItem.id)
        hideKeyboard()
        navigate(id = R.id.to_details_fragment, args = bundle, extras = extras)
    }

    private fun initView() {
        search.addTextChangedListener { viewModel.search(it) }
        search.setOnClearClickedListener { it.setText("") }
    }

    private fun initObservers() {
        viewModel.queryResult.observe(viewLifecycleOwner, Observer {
            handleQueryResult(it)
        })
    }

    private fun handleQueryResult(searchResult: ViewPodcastSearch) {
        val adapter = searchList.adapter as? SearchAdapter
        adapter?.add(searchResult.results)
    }
}
