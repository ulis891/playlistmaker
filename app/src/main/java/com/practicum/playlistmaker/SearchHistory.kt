package com.practicum.playlistmaker

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val KEY_TRACK_LIST = "track_list"
        private const val MAX_HISTORY_SIZE = 10
    }

    fun addTrack(track: Track) {
        val tracks = readTrackList().toMutableList()
        tracks.removeIf { it.trackId == track.trackId }
        tracks.add(0, track)

        if (tracks.size > MAX_HISTORY_SIZE) {
            tracks.removeAt(MAX_HISTORY_SIZE)
        }
        saveTrackList(tracks)
    }

    fun readTrackList(): List<Track> {
        val json = sharedPreferences.getString(KEY_TRACK_LIST, null) ?: return emptyList()
        return Gson().fromJson(json, Array<Track>::class.java).toList()
    }

    fun clearTrackList() {
        sharedPreferences.edit {
            remove(KEY_TRACK_LIST)
        }
    }

    private fun saveTrackList(tracks: List<Track>) {
        val json = Gson().toJson(tracks)
        sharedPreferences.edit {
            putString(KEY_TRACK_LIST, json)
        }
    }
}
