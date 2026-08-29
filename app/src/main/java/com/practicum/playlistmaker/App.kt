package com.practicum.playlistmaker

import android.app.Application
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    companion object {
        private const val PREFS_NAME = "app_settings"
        private const val KEY_DARK_THEME = "dark_theme"
    }


    override fun onCreate() {
        super.onCreate()
        var darkTheme = getDarkThemePreference()
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        saveDarkThemePreference(darkThemeEnabled)
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun isDark(): Boolean = getDarkThemePreference()

    private fun getDarkThemePreference(): Boolean {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .getBoolean(KEY_DARK_THEME, false)
    }

    private fun saveDarkThemePreference(darkThemeEnabled: Boolean) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK_THEME, darkThemeEnabled)
            .apply()
    }
}
