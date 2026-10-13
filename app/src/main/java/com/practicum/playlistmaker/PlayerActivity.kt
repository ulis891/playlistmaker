package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_TRACK_ID = "track_id"

        fun createIntent(context: Context, trackId: String): Intent =
            Intent(context, PlayerActivity::class.java).apply {
                putExtra(EXTRA_TRACK_ID, trackId)
            }

        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val TRACK_DEBOUNCE_DELAY = 1000L
    }

    private var playerState = STATE_DEFAULT
    private  lateinit var play: ImageButton
    private  lateinit var playTime: TextView

    private var mediaPlayer  = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private val updateTrackDurationRunnable = object : Runnable {
        private var dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        override fun run() {
            if (playerState == STATE_PLAYING){
                playTime.text = dateFormat.format(mediaPlayer.currentPosition)
                handler.postDelayed(this, TRACK_DEBOUNCE_DELAY)
            }

        }
    }

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
        play = findViewById(R.id.ibPlay)
        play.setOnClickListener {
            playbackControl()
        }
        playTime = findViewById(R.id.tvPlayTime)
        val trackId = intent.getStringExtra(EXTRA_TRACK_ID)
        val track = trackId?.let { id ->
            (application as App).searchHistory.readTrackList().find { it.trackId == id }
        }

        track?.let { bindTrack(it) }
        preparePlayer(track?.previewUrl)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTrackDurationRunnable)
        mediaPlayer.release()
    }

    private fun bindTrack(track: Track) {
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        Glide.with(this)
            .load(track.getCoverArtwork())
            .centerCrop()
            .transform(RoundedCorners(Utils.dpToPx(8f, this)))
            .placeholder(R.drawable.album_placeholder)
            .error(R.drawable.album_placeholder)
            .into(findViewById(R.id.ivAlbumCover))
        findViewById<TextView>(R.id.tvSongName).text = track.trackName
        findViewById<TextView>(R.id.tvArtistName).text = track.artistName
        findViewById<TextView>(R.id.tvDurationValue).text = dateFormat.format(track.trackTime)
        if (track.collectionName.isEmpty()){
            findViewById<Group>(R.id.groupAlbum).visibility = View.GONE
        }
        else{
            findViewById<TextView>(R.id.tvAlbumValue).text = track.collectionName
        }
        if (track.releaseDate.isEmpty()){
            findViewById<Group>(R.id.groupYear).visibility = View.GONE
        }
        else{
            findViewById<TextView>(R.id.tvYearValue).text = track.releaseDate.split("-")[0]
        }
        findViewById<TextView>(R.id.tvGenreValue).text = track.primaryGenreName
        findViewById<TextView>(R.id.tvCountryValue).text = track.country
    }

    private fun preparePlayer(url: String?){
        mediaPlayer.setDataSource(url)
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacks(updateTrackDurationRunnable)
            playerState = STATE_PREPARED
            play.setImageResource(R.drawable.ic_play_button_84)
            playTime.setText(R.string.song_time_placeholder)
        }
        mediaPlayer.prepareAsync()
    }

    private fun startPlayer() {
        mediaPlayer.start()
        play.setImageResource(R.drawable.ic_pause_button_84)
        playerState = STATE_PLAYING
        handler.postDelayed(updateTrackDurationRunnable, TRACK_DEBOUNCE_DELAY)
    }

    private fun pausePlayer(){
        mediaPlayer.pause()
        play.setImageResource(R.drawable.ic_play_button_84)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }
}