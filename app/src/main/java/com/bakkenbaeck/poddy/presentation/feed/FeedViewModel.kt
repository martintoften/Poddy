package com.bakkenbaeck.poddy.presentation.feed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.model.Rss
import com.bakkenbaeck.poddy.repository.FeedRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class FeedViewModel(
    private val feedRepository: FeedRepository
) : ViewModel() {

    val feedResult = MutableLiveData<Rss>()

    fun getFeed(feedUrl: String) {
        viewModelScope.launch {
            feedRepository.getFeed(feedUrl)
                .flowOn(Dispatchers.IO)
                .collect { handleFeedResult(it) }
        }

    }

    private fun handleFeedResult(any: Rss) {
        feedResult.value = any
    }
}
