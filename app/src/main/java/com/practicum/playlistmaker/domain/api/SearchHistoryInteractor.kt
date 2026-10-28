package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun addTrack(track: Track)
    fun getTracks(): List<Track>
    fun clear()
}
