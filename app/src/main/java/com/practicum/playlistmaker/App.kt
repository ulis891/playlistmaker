package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.domain.impl.SearchHistoryImpl

class App : Application() {

    companion object {
        private const val PREFS_NAME = "app_settings"
        private const val KEY_DARK_THEME = "dark_theme"
        private const val PREFS_HISTORY = "history"
    }

    val searchHistory: SearchHistoryImpl by lazy {
        SearchHistoryImpl(getSharedPreferences(PREFS_HISTORY, MODE_PRIVATE))
    }


    override fun onCreate() {
        super.onCreate()
        val darkTheme = getDarkThemePreference()
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
