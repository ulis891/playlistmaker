package com.practicum.playlistmaker.settings.domain.impl

import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository

class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository
) : SettingsInteractor {

    override fun isDarkTheme(): Boolean = settingsRepository.isDarkTheme()

    override fun setDarkTheme(enabled: Boolean) {
        settingsRepository.setDarkTheme(enabled)
    }
}
