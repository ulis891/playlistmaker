package com.practicum.playlistmaker.settings.data.repository

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {

    companion object {
        private const val KEY_DARK_THEME = "dark_theme"
    }

    override fun isDarkTheme(): Boolean =
        sharedPreferences.getBoolean(KEY_DARK_THEME, false)

    override fun setDarkTheme(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_DARK_THEME, enabled)
            .apply()
        applyTheme(enabled)
    }

    private fun applyTheme(enabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
