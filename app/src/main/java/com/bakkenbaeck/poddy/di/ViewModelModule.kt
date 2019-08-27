package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.presentation.feed.FeedViewModel
import com.bakkenbaeck.poddy.presentation.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SearchViewModel(get()) }
    viewModel { FeedViewModel(get()) }
}
