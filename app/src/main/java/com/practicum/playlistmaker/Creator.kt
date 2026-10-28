package com.practicum.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.data.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.SearchHistoryImpl
import com.practicum.playlistmaker.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        val prefs: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        return SearchHistoryImpl(prefs)
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryRepository {
        return getSearchHistoryRepository(context)
    }

    private fun getSettingsInteractor(context: Context): SettingsInteractor {
        val prefs: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        return SettingsInteractorImpl(prefs)
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return getSettingsInteractor(context)
    }
}
