package com.bakkenbaeck.poddy.presentation.search

import android.os.Bundle
import android.util.Log
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
import com.bakkenbaeck.poddy.presentation.model.*
import com.bakkenbaeck.poddy.util.Failure
import com.bakkenbaeck.poddy.util.Loading
import com.bakkenbaeck.poddy.util.Resource
import com.bakkenbaeck.poddy.util.Success
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
            adapter = SearchAdapter { view, podcast -> goToPodcastView(view, podcast) }
            layoutManager = LinearLayoutManager(context)
        }

        categoryList.apply {
            adapter = CategoryListAdapter { view, podcast -> goToPodcastView(view, podcast) }
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun goToPodcastView(view: View, podcast: ViewBasePodcast) {
        val bundle = Bundle().apply {
            putString(PODCAST_ID, podcast.id)
            putString(PODCAST_IMAGE, podcast.image)
            putString(PODCAST_TITLE, podcast.title)
            putString(PODCAST_DESCRIPTION, podcast.description)
        }
        goTo(view, podcast.id, bundle)
    }

    private fun goTo(view: View, id: String, bundle: Bundle) {
        val extras = FragmentNavigatorExtras(view to id)
        hideKeyboard()
        navigate(id = R.id.to_details_fragment, args = bundle, extras = extras)
    }

    private fun initView() {
        search.addTextChangedListener { handleSearch(it) }
        search.setOnClearClickedListener { it.setText("") }
    }

    private fun handleSearch(value: String) {
        if (value.isNotEmpty()) {
            searchList.visibility = View.VISIBLE
            categoryList.visibility = View.GONE
            loadingIndicator.visibility = View.GONE
        } else {
            searchList.visibility = View.GONE
            categoryList.visibility = View.VISIBLE
        }

        viewModel.search(value)
    }

    private fun initObservers() {
        viewModel.queryResult.observe(viewLifecycleOwner, Observer {
            handleQueryResult(it)
        })

        viewModel.categoriesResult.observe(viewLifecycleOwner, Observer {
            handleCategoryResult(it)
        })
    }

    private fun handleQueryResult(searchResult: ViewPodcastSearch) {
        val adapter = searchList.adapter as? SearchAdapter
        adapter?.add(searchResult.results)
    }

    private fun handleCategoryResult(result: Resource<List<ViewCategory>>) {
        when (result) {
            is Success -> {
                loadingIndicator.visibility = View.GONE
                val adapter = categoryList.adapter as? CategoryListAdapter
                adapter?.setItems(result.data)
            }
            is Loading -> loadingIndicator.visibility = View.VISIBLE
            is Failure -> {
                loadingIndicator.visibility = View.GONE
                Log.e("SearchFragment", result.throwable?.message.orEmpty())
            }
        }
    }
}
