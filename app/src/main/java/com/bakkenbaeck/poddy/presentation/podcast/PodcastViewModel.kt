package com.bakkenbaeck.poddy.presentation.podcast

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakkenbaeck.poddy.presentation.mappers.mapToViewPodcastFromDB
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import com.bakkenbaeck.poddy.repository.PodcastRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PodcastViewModel(
    private val podcastRepository: PodcastRepository
) : ViewModel() {

    val podcasts = MutableLiveData<List<ViewPodcast>>()

    init {
        getPodcast()
    }

    private fun getPodcast() {
        viewModelScope.launch {
            podcastRepository.getPodcasts()
                .flowOn(Dispatchers.IO)
                .map { mapToViewPodcastFromDB(it) }
                .collect { handlePodcasts(it) }
        }
    }

    private fun handlePodcasts(podcastResult: List<ViewPodcast>) {
        podcasts.value = podcastResult
    }
}
