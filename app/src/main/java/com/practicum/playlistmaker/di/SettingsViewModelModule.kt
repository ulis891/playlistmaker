package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsViewModelModule = module {
    viewModel {
        SettingsViewModel(get(),get()
        )
    }
}
