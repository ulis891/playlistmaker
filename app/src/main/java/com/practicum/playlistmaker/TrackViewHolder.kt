package com.practicum.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val rootLayout: LinearLayout = itemView.findViewById(R.id.rootLayout)
    private val ivTrackCover: ImageView = itemView.findViewById(R.id.ivAlbumCover)
    private val tvTrackName: TextView = itemView.findViewById(R.id.tvSongName)
    private val tvArtistName: TextView = itemView.findViewById(R.id.tvArtistName)
    private val tvDuration: TextView = itemView.findViewById(R.id.tvSongDuration)

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    fun bind(track: Track) {
        val trackName = track.trackName
        val artistName = track.artistName
        val duration = track.trackTime
        val albumArt = track.artworkUrl100

        val context = itemView.context

        Glide.with(itemView)
            .load(albumArt)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2f, context)))
            .placeholder(R.drawable.album_image_placeholder_45)
            .error(R.drawable.album_image_placeholder_45)
            .into(ivTrackCover)
        tvTrackName.text = trackName
        tvArtistName.text = artistName
        tvDuration.text = dateFormat.format(duration)
    }
}