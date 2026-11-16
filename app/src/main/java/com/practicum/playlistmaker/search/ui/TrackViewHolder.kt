package com.practicum.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.Utils
import com.practicum.playlistmaker.databinding.ViewTrackBinding
import com.practicum.playlistmaker.search.domain.models.Track

class TrackViewHolder(private val binding: ViewTrackBinding) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var isClicked = true
    private val handler = Handler(Looper.getMainLooper())

    fun clickDebounce(): Boolean {
        if (isClicked) {
            isClicked = false
            handler.postDelayed({ isClicked = true }, CLICK_DEBOUNCE_DELAY)
            return true
        }
        return false
    }

    fun bind(track: Track) {
        binding.tvSongName.text = track.trackName
        binding.tvArtistName.text = track.artistName
        binding.tvSongDuration.text = track.formattedDuration

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(Utils.dpToPx(2f, itemView.context)))
            .placeholder(R.drawable.album_image_placeholder_45)
            .error(R.drawable.album_image_placeholder_45)
            .into(binding.ivAlbumCover)
    }
}
