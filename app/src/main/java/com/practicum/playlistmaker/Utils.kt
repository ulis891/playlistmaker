package com.practicum.playlistmaker

import android.content.Context
import android.util.TypedValue
import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

object  Utils {
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    fun TrackDto.toTrack(): Track = Track(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        formattedDuration = dateFormat.format(trackTimeMillis),
        artworkUrl100 = artworkUrl100,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl
    )
}