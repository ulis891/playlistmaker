package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface SearchHistoryInteractor {
    fun addTrack(track: Track)
    fun getTracks(): List<Track>
    fun clear()
}
