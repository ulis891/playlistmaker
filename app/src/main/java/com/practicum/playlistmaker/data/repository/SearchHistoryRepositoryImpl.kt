package com.practicum.playlistmaker.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SearchHistoryRepository {

    companion object {
        private const val KEY_TRACK_LIST = "track_list"
        private const val MAX_HISTORY_SIZE = 10
    }

    private val gson = Gson()
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

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
        val dtoList = gson.fromJson(json, Array<TrackDto>::class.java).toList()
        return dtoList.map { it.toTrack() }
    }

    override fun clear() {
        sharedPreferences.edit {
            remove(KEY_TRACK_LIST)
        }
    }

    private fun saveTrackList(tracks: List<Track>) {
        val dtoList = tracks.map { it.toDto() }
        val json = gson.toJson(dtoList)
        sharedPreferences.edit {
            putString(KEY_TRACK_LIST, json)
        }
    }

    private fun TrackDto.toTrack(): Track = Track(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        trackTimeMillis = trackTimeMillis,
        formattedDuration = formattedDuration ?: dateFormat.format(trackTimeMillis),
        artworkUrl100 = artworkUrl100,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl
    )

    private fun Track.toDto(): TrackDto = TrackDto(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        trackTimeMillis = trackTimeMillis,
        formattedDuration = formattedDuration,
        artworkUrl100 = artworkUrl100,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl
    )
}
