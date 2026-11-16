package com.practicum.playlistmaker.settings.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.sharing.domain.EmailData

class SettingsViewModel(context: Context) : ViewModel() {

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                SettingsViewModel(app)
            }
        }
    }

    private val settingsInteractor = Creator.provideSettingsInteractor(context)
    private val sharingInteractor = Creator.provideSharingInteractor(context)

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
