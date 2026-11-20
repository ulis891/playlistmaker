package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<TracksRepository> {
        TracksRepositoryImpl(get<NetworkClient>())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<PlayerRepository> {
        PlayerRepositoryImpl()
    }
}