package com.bakkenbaeck.poddy.presentation.feed

import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.bakkenbaeck.poddy.network.Result
import com.bakkenbaeck.poddy.presentation.model.ViewPodcast
import com.bakkenbaeck.poddy.useCase.GetPodcastRecommendationsUseCase
import com.bakkenbaeck.poddy.util.Failure
import com.bakkenbaeck.poddy.util.Loading
import com.bakkenbaeck.poddy.util.Resource
import com.bakkenbaeck.poddy.util.Success
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PodcastDetailsModelFactory(
    private val recommendationsUseCase: GetPodcastRecommendationsUseCase,
    private val podcast: ViewPodcast?,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return PodcastDetailsViewModel(podcast, recommendationsUseCase) as T
    }
}

class PodcastDetailsViewModel(
    private val podcast: ViewPodcast?,
    private val getPodcastRecommendationsUseCase: GetPodcastRecommendationsUseCase
) : ViewModel() {

    val recommendationsResult by lazy { MutableLiveData<Resource<List<ViewPodcast>>>() }

    init {
        getPodcastRecommendations()
    }

    private fun getPodcastRecommendations() {
        val podcast = podcast ?: return

        viewModelScope.launch {
            recommendationsResult.value = Loading()
            val result = async { getPodcastRecommendationsUseCase.execute(podcast.id) }
            when (val recommendations = result.await()) {
                is Result.Success -> recommendationsResult.value = Success(recommendations.value)
                is Result.Error -> recommendationsResult.value = Failure(recommendations.throwable)
            }
        }
    }
}