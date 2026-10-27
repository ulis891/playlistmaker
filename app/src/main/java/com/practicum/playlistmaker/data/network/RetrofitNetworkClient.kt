package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {
    private val iTunesBaseURL = "https://itunes.apple.com/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesAPI:: class.java)

    override fun doRequest(dto: Any): Response {
        val result = Response()
        if (dto is TracksSearchRequest) {
            val response = iTunesService.getSongs(dto.expression).execute()
            result.resultCode = if (response.isSuccessful) 200 else 400
            return result
        }
        else{
            return Response().apply { resultCode = 400 }
        }
    }
}