package com.bakkenbaeck.poddy.presentation.search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.network.Result
import com.bakkenbaeck.poddy.presentation.mappers.mapFromNetworkToView
import com.bakkenbaeck.poddy.presentation.model.ViewPodcastSearch
import com.bakkenbaeck.poddy.repository.PodcastRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

const val DEBOUNCE_DELAY = 500L

class SearchViewModel(
    private val podcastRepository: PodcastRepository
) : ViewModel() {

    private val channel = BroadcastChannel<String>(Channel.CONFLATED)
    val queryResult = MutableLiveData<ViewPodcastSearch>()

    init {
        initQueryObserver()
    }

    private fun initQueryObserver() {
        viewModelScope.launch {
            channel.asFlow()
                .debounce(DEBOUNCE_DELAY)
                .map { podcastRepository.search(it) }
                .flowOn(Dispatchers.IO)
                .collect { handleSearchResult(it) }
        }
    }

    private fun handleSearchResult(searchResult: Result<ViewPodcastSearch>) {
        when (searchResult) {
            is Result.Success -> { queryResult.value = searchResult.value }
            is Result.Error -> Log.e("Error while searching", searchResult.throwable.message.orEmpty())
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            channel.send(query)
        }
    }
}
