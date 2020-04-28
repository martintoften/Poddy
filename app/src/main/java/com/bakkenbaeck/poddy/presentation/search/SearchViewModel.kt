package com.bakkenbaeck.poddy.presentation.search

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.network.Result
import com.bakkenbaeck.poddy.presentation.model.ViewCategory
import com.bakkenbaeck.poddy.presentation.model.ViewPodcastSearch
import com.bakkenbaeck.poddy.useCase.GetCategoriesUseCase
import com.bakkenbaeck.poddy.useCase.PodcastSearchUseCase
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
const val SCROLL_STATE = "SCROLL_STATE"

class SearchViewModel(
    private val savedState: SavedStateHandle,
    private val searchPodcastUseCase: PodcastSearchUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
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
            when (val result = getCategoriesUseCase.execute()) {
                is Result.Success -> categoriesResult.value = Success(result.value)
                is Result.Error -> categoriesResult.value = Failure(result.throwable)
            }
        }
    }

    private fun initQueryObserver() {
        viewModelScope.launch {
            channel.asFlow()
                .debounce(DEBOUNCE_DELAY)
                .map { searchPodcastUseCase.execute(it) }
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

    fun setScrollState(scrollStates: HashMap<String, Parcelable>) {
        scrollStates.forEach {
            savedState.set("$SCROLL_STATE:${it.key}", it.value)
        }
    }

    fun getScrollState(): HashMap<String, Parcelable> {
        val scrollStates = hashMapOf<String, Parcelable>()
        savedState.keys()
            .filter { it.contains(SCROLL_STATE) }
            .forEach {
                val scrollState = savedState.get<Parcelable>(it) ?: return@forEach
                val categoryId = it.split(":")[1]
                scrollStates[categoryId] = scrollState
            }

        return scrollStates
    }
}
