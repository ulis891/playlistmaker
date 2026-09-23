package com.practicum.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.back_arrow).setOnClickListener { finish() }

        val trackId = intent.getStringExtra("track_id")
        val track = trackId?.let { id ->
            (application as App).searchHistory.readTrackList().find { it.trackId == id }
        }

        track?.let { bindTrack(it) }
    }

    private fun bindTrack(track: Track) {
        Glide.with(this)
            .load(track.getCoverArtwork())
            .centerCrop()
            .transform(RoundedCorners(Utils.dpToPx(2f, this)))
            .placeholder(R.drawable.album_image_placeholder_45)
            .error(R.drawable.album_image_placeholder_45)
            .into(findViewById(R.id.ivAlbumCover))
        findViewById<TextView>(R.id.tvSongName).text = track.trackName
        findViewById<TextView>(R.id.tvArtistName).text = track.artistName
        findViewById<TextView>(R.id.tvDurationValue).text = track.trackTime.toString()
        findViewById<TextView>(R.id.tvAlbumValue).text = track.collectionName
        findViewById<TextView>(R.id.tvYearValue).text = track.releaseDate.toString()
        findViewById<TextView>(R.id.tvGenreValue).text = track.primaryGenreName
        findViewById<TextView>(R.id.tvCountryValue).text = track.country
    }
}