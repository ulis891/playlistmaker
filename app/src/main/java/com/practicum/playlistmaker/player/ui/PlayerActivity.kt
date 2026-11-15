package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.Utils
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.player.ui.models.PlayerState
import com.practicum.playlistmaker.search.domain.models.Track

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_TRACK_ID = "track_id"

        fun createIntent(context: Context, trackId: String): Intent =
            Intent(context, PlayerActivity::class.java).apply {
                putExtra(EXTRA_TRACK_ID, trackId)
            }
    }

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.player) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val trackId = intent.getStringExtra(EXTRA_TRACK_ID) ?: ""

        viewModel = ViewModelProvider(this, PlayerViewModel.getFactory(trackId))[PlayerViewModel::class.java]

        binding.backArrow.setOnClickListener { finish() }
        binding.ibPlay.setOnClickListener {
            viewModel.playbackControl()
        }

        viewModel.observeTrack().observe(this) { track ->
            bindTrack(track)
        }

        viewModel.observePlayerState().observe(this) { state ->
            renderState(state)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    private fun bindTrack(track: Track) {
        Glide.with(this)
            .load(track.getCoverArtwork())
            .centerCrop()
            .transform(RoundedCorners(Utils.dpToPx(8f, this)))
            .placeholder(R.drawable.album_placeholder)
            .error(R.drawable.album_placeholder)
            .into(binding.ivAlbumCover)

        binding.tvSongName.text = track.trackName
        binding.tvArtistName.text = track.artistName
        binding.tvDurationValue.text = track.formattedDuration

        if (track.collectionName.isEmpty()) {
            binding.groupAlbum.visibility = View.GONE
        } else {
            binding.groupAlbum.visibility = View.VISIBLE
            binding.tvAlbumValue.text = track.collectionName
        }

        if (track.releaseDate.isEmpty()) {
            binding.groupYear.visibility = View.GONE
        } else {
            binding.groupYear.visibility = View.VISIBLE
            binding.tvYearValue.text = track.releaseDate.split("-")[0]
        }

        binding.tvGenreValue.text = track.primaryGenreName
        binding.tvCountryValue.text = track.country
    }

    private fun renderState(state: PlayerState) {
        when (state) {
            is PlayerState.Default -> {
                binding.ibPlay.isEnabled = false
                binding.ibPlay.setImageResource(R.drawable.ic_play_button_84)
                binding.tvPlayTime.setText(R.string.song_time_placeholder)
            }
            is PlayerState.Prepared -> {
                binding.ibPlay.isEnabled = true
                binding.ibPlay.setImageResource(R.drawable.ic_play_button_84)
                binding.tvPlayTime.setText(R.string.song_time_placeholder)
            }
            is PlayerState.Playing -> {
                binding.ibPlay.isEnabled = true
                binding.ibPlay.setImageResource(R.drawable.ic_pause_button_84)
                binding.tvPlayTime.text = state.progress
            }
            is PlayerState.Paused -> {
                binding.ibPlay.isEnabled = true
                binding.ibPlay.setImageResource(R.drawable.ic_play_button_84)
                binding.tvPlayTime.text = state.progress
            }
        }
    }
}
