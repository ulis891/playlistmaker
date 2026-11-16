package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.creator.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val settingsInteractor = Creator.provideSettingsInteractor(this)
        val darkTheme = settingsInteractor.isDarkTheme()
        settingsInteractor.setDarkTheme(darkTheme)
    }
}
