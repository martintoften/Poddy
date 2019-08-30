package com.bakkenbaeck.poddy.presentation.podcast

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.repository.FeedRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.db.Podcast

class PodcastViewModel(
    private val feedRepository: FeedRepository
) : ViewModel() {

    val podcasts = MutableLiveData<List<Podcast>>()

    init {
        getPodcast()
    }

    private fun getPodcast() {
        viewModelScope.launch {
            feedRepository.getPodcasts()
                .flowOn(Dispatchers.IO)
                .collect { handlePodcasts(it) }
        }
    }

    private fun handlePodcasts(podcastsResult: List<Podcast>) {
        podcasts.value = podcastsResult
    }
}
