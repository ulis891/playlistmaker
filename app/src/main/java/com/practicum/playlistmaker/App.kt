package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.interactorModule
import com.practicum.playlistmaker.di.playerViewModelModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.searchViewModelModule
import com.practicum.playlistmaker.di.settingsViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                repositoryModule,
                interactorModule,
                searchViewModelModule,
                playerViewModelModule,
                settingsViewModelModule
            )
        }
    }
}
