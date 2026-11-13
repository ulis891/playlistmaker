package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesAPI {
    @GET("/search?entity=song")
    fun getSongs(
        @Query("term") text: String,
        @Query("entity") entity: String = "song"
    ): Call<TracksSearchResponse>
}