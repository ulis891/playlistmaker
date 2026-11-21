package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest

class RetrofitNetworkClient(
    private val iTunesService: ITunesAPI
) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto is TracksSearchRequest) {
            val response = iTunesService.getSongs(dto.expression).execute()
            val body = response.body() ?: Response()
            return body.apply { resultCode = response.code() }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}
