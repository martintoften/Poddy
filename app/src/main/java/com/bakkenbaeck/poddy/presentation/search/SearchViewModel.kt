package com.bakkenbaeck.poddy.presentation.search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.network.Result
import com.bakkenbaeck.poddy.presentation.model.ViewCategory
import com.bakkenbaeck.poddy.presentation.model.ViewPodcastSearch
import com.bakkenbaeck.poddy.repository.PodcastRepository
import com.bakkenbaeck.poddy.util.Failure
import com.bakkenbaeck.poddy.util.Loading
import com.bakkenbaeck.poddy.util.Resource
import com.bakkenbaeck.poddy.util.Success
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
    val categoriesResult = MutableLiveData<Resource<List<ViewCategory>>>()

    init {
        getCategories()
        initQueryObserver()
    }

    private fun getCategories() {
        viewModelScope.launch {
            categoriesResult.value = Loading()
            when (val result = podcastRepository.getCategories()) {
                is Result.Success -> categoriesResult.value = Success(result.value)
                is Result.Error -> categoriesResult.value = Failure(result.throwable)
            }
        }
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
