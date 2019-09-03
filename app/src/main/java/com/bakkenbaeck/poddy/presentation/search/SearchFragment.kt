package com.bakkenbaeck.poddy.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.extensions.navigate
import com.bakkenbaeck.poddy.presentation.BackableFragment
import com.bakkenbaeck.poddy.presentation.feed.PODCAST_ID
import com.bakkenbaeck.poddy.presentation.feed.PODCAST_IMAGE
import com.bakkenbaeck.poddy.presentation.model.ViewPodcastSearch
import com.bakkenbaeck.poddy.presentation.model.ViewPodcastSearchItem
import com.bakkenbaeck.poddy.util.TextListener
import kotlinx.android.synthetic.main.search_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : BackableFragment() {

    private val viewModel: SearchViewModel by viewModel()
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initView()
        initAdapter()
        initObservers()
    }

    private fun initAdapter() {
        searchAdapter = SearchAdapter { goTo(it) }

        searchList.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun goTo(searchItem: ViewPodcastSearchItem) {
        val bundle = Bundle().apply {
            putString(PODCAST_ID, searchItem.id)
            putString(PODCAST_IMAGE, searchItem.image)
        }
        navigate(R.id.to_details_fragment, bundle)
    }

    private fun initView() {
        search.addTextChangedListener(object : TextListener() {
            override fun onTextChanged(value: String) {
                viewModel.search(value)
            }
        })
    }

    private fun initObservers() {
        viewModel.queryResult.observe(this, Observer {
            handleQueryResult(it)
        })
    }

    private fun handleQueryResult(searchResult: ViewPodcastSearch) {
        searchAdapter.add(searchResult.results)
    }
}
