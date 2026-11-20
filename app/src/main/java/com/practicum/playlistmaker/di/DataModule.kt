package com.practicum.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.network.ITunesAPI
import com.practicum.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single<ITunesAPI> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesAPI::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient()
    }

    single {
        androidContext()
            .getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }

    single<SharedPreferences> {
        androidContext().getSharedPreferences("history", Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get())
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }
}