package com.bakkenbaeck.poddy.presentation.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.model.Search
import com.bakkenbaeck.poddy.repository.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val channel = BroadcastChannel<String>(Channel.CONFLATED)
    val queryResult = MutableLiveData<Search>()

    init {
        initQueryObserver()
    }

    private fun initQueryObserver() {
        viewModelScope.launch {
            channel.asFlow()
                .debounce(300)
                .flatMapMerge { searchRepository.search(it) }
                .flowOn(Dispatchers.IO)
                .collect { handleSearchResult(it) }
        }
    }

    private fun handleSearchResult(searchResult: Search) {
        queryResult.value = searchResult
    }

    fun search(query: String) {
        viewModelScope.launch {
            channel.send(query)
        }
    }
}
