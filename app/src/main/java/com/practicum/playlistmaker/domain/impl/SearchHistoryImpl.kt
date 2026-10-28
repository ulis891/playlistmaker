package com.practicum.playlistmaker.domain.impl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class SearchHistoryImpl(private val sharedPreferences: SharedPreferences) : SearchHistoryRepository {

    companion object {
        private const val KEY_TRACK_LIST = "track_list"
        private const val MAX_HISTORY_SIZE = 10
    }

    private val gson = Gson()

    override fun addTrack(track: Track) {
        val tracks = getTracks().toMutableList()
        tracks.removeIf { it.trackId == track.trackId }
        tracks.add(0, track)

        if (tracks.size > MAX_HISTORY_SIZE) {
            tracks.removeAt(MAX_HISTORY_SIZE)
        }
        saveTrackList(tracks)
    }

    override fun getTracks(): List<Track> {
        val json = sharedPreferences.getString(KEY_TRACK_LIST, null) ?: return emptyList()
        return gson.fromJson(json, Array<Track>::class.java).toList()
    }

    override fun clear() {
        sharedPreferences.edit {
            remove(KEY_TRACK_LIST)
        }
    }

    private fun saveTrackList(tracks: List<Track>) {
        val json = gson.toJson(tracks)
        sharedPreferences.edit {
            putString(KEY_TRACK_LIST, json)
        }
    }
}
