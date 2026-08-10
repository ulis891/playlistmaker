package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val rootLayout: LinearLayout = itemView.findViewById(R.id.rootLayout)
    private val ivTrackCover: ImageView = itemView.findViewById(R.id.ivAlbumCover)
    private val tvTrackName: TextView = itemView.findViewById(R.id.tvSongName)
    private val tvArtistName: TextView = itemView.findViewById(R.id.tvArtistName)
    private val tvDuration: TextView = itemView.findViewById(R.id.tvSongDuration)

    fun bind(track: Track) {
        val trackName = track.trackName
        val artistName = track.artistName
        val duration = track.trackTime
        val albumArt = track.artworkUrl100

        Glide.with(itemView.context).load(albumArt).into(ivTrackCover)
        tvTrackName.text = trackName
        tvArtistName.text = artistName
        tvDuration.text = duration
    }
}