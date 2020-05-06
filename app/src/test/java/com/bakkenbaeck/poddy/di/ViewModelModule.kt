package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.presentation.queue.QueueViewModel
import org.koin.dsl.module

val viewModelModule = module {
    factory { QueueViewModel(get(), get(), get()) }
}