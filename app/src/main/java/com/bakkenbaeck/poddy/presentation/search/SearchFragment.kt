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
import com.bakkenbaeck.poddy.extensions.getScrollStates
import com.bakkenbaeck.poddy.extensions.hideKeyboard
import com.bakkenbaeck.poddy.extensions.navigate
import com.bakkenbaeck.poddy.presentation.BackableFragment
import com.bakkenbaeck.poddy.presentation.model.ViewBasePodcast
import com.bakkenbaeck.poddy.presentation.model.ViewCategory
import com.bakkenbaeck.poddy.presentation.model.ViewPodcastSearch
import com.bakkenbaeck.poddy.presentation.model.toViewModel
import com.bakkenbaeck.poddy.util.Failure
import com.bakkenbaeck.poddy.util.Loading
import com.bakkenbaeck.poddy.util.Resource
import com.bakkenbaeck.poddy.util.Success
import kotlinx.android.synthetic.main.search_fragment.*
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class SearchFragment : BackableFragment() {

    private val viewModel: SearchViewModel by stateViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, inState: Bundle?) {
        super.onViewCreated(view, inState)
        init(inState)
    }

    private fun init(inState: Bundle?) {
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
            adapter = CategoryListAdapter(viewModel.getScrollState()) { view, podcast ->
                goToPodcastView(view, podcast)
            }
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun goToPodcastView(view: View, podcast: ViewBasePodcast) {
        hideKeyboard()
        val extras = FragmentNavigatorExtras(view to podcast.id)
        val directions = SearchFragmentDirections.toDetailsFragment(podcast.toViewModel())
        navigate(directions, extras = extras)
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

    override fun onStop() {
        super.onStop()
        setScrollState()
    }

    private fun setScrollState() {
        val adapter = categoryList.adapter as? CategoryListAdapter ?: return
        val scrollStates = adapter.getScrollStates(categoryList)
        viewModel.setScrollState(scrollStates)
    }
}
