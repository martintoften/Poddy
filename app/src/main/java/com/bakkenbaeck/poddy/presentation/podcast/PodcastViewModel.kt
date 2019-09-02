package com.bakkenbaeck.poddy.presentation.podcast

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.presentation.mappers.mapToViewPodcastFromDB
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import com.bakkenbaeck.poddy.repository.FeedRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PodcastViewModel(
    private val feedRepository: FeedRepository
) : ViewModel() {

    val podcasts = MutableLiveData<List<ViewPodcast>>()

    init {
        getPodcast()
    }

    private fun getPodcast() {
        viewModelScope.launch {
            feedRepository.getPodcasts()
                .flowOn(Dispatchers.IO)
                .map { mapToViewPodcastFromDB(it) }
                .collect { handlePodcasts(it) }
        }
    }

    private fun handlePodcasts(podcastResult: List<ViewPodcast>) {
        val newPodcast = mutableListOf<ViewPodcast>().apply {
            addAll(podcastResult)
            addAll(podcasts.value ?: emptyList())
        }

        podcasts.value = newPodcast
    }
}
