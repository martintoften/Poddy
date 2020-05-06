package com.bakkenbaeck.poddy.di

import com.bakkenbaeck.poddy.useCase.DeleteQueueUseCase
import com.bakkenbaeck.poddy.useCase.QueueFlowUseCase
import com.bakkenbaeck.poddy.useCase.ReorderQueueUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { QueueFlowUseCase(get()) }
    factory { ReorderQueueUseCase(get()) }
    factory { DeleteQueueUseCase(get()) }
}