package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun addTrack(track: Track)
    fun getTracks(): List<Track>
    fun clear()
}
