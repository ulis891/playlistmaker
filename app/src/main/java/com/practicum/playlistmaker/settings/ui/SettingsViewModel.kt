package com.practicum.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.EmailData
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val darkModeLiveData = MutableLiveData<Boolean>()
    fun observeDarkMode(): LiveData<Boolean> = darkModeLiveData

    init {
        darkModeLiveData.value = settingsInteractor.isDarkTheme()
    }

    fun toggleDarkMode(enabled: Boolean) {
        settingsInteractor.setDarkTheme(enabled)
        darkModeLiveData.value = enabled
    }

    fun shareApp(link: String) {
        sharingInteractor.shareApp(link)
    }

    fun openTerms(link: String) {
        sharingInteractor.openTerms(link)
    }

    fun openSupport(emailData: EmailData) {
        sharingInteractor.openSupport(emailData)
    }
}
