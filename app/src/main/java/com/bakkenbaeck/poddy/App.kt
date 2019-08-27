package com.bakkenbaeck.poddy

import android.app.Application
import com.bakkenbaeck.poddy.di.appModule
import com.bakkenbaeck.poddy.di.repositoryModule
import com.bakkenbaeck.poddy.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()


        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(listOf(appModule, repositoryModule, viewModelModule))
        }
    }
}
