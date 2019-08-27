package com.bakkenbaeck.poddy.presentation.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bakkenbaeck.poddy.R
import com.bakkenbaeck.poddy.model.Search
import com.bakkenbaeck.poddy.util.TextListener
import kotlinx.android.synthetic.main.search_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

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
        searchAdapter = SearchAdapter()

        searchList.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(this@SearchFragment.context)
        }
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
        viewModel.feedResult.observe(this, Observer {
            handleFeedResult(it)
        })
    }

    private fun handleQueryResult(searchResult: Search) {
        searchAdapter.add(searchResult.results)
    }

    private fun handleFeedResult(feedResult: Any) {
        Log.d("result feed", feedResult.toString())
    }
}
