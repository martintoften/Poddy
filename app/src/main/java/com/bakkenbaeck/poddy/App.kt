package com.bakkenbaeck.poddy

import android.app.Application
import com.bakkenbaeck.poddy.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(listOf(appModule, dbModule, repositoryModule, viewModelModule, playerModule))
        }
    }
}
