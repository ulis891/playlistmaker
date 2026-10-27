package com.practicum.playlistmaker.domain.impl

import android.content.SharedPreferences
import com.practicum.playlistmaker.domain.api.SettingsInteractor

class SettingsInteractorImpl(private val sharedPreferences: SharedPreferences): SettingsInteractor {
    companion object {
        private const val PREFS_NAME = "app_settings"
        private const val KEY_DARK_THEME = "dark_theme"
    }
    override fun isDarkTheme(): Boolean =
        sharedPreferences.getBoolean(KEY_DARK_THEME, false)

    override fun setDarkTheme(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_DARK_THEME, enabled)
            .apply()
    }
}