package com.practicum.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.ui.models.PlayerState
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val trackId: String,
    private val playerInteractor: PlayerInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    companion object {
        private const val UPDATE_DELAY = 300L

        fun getFactory(trackId: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                val playerInteractor = Creator.providePlayerInteractor()
                val searchHistoryInteractor = Creator.provideSearchHistoryInteractor(app)
                PlayerViewModel(trackId, playerInteractor, searchHistoryInteractor)
            }
        }
    }

    private val trackLiveData = MutableLiveData<Track>()
    fun observeTrack(): LiveData<Track> = trackLiveData

    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default)
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val handler = Handler(Looper.getMainLooper())
    private val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            if (playerStateLiveData.value is PlayerState.Playing) {
                playerStateLiveData.value = PlayerState.Playing(getCurrentPositionFormatted())
                handler.postDelayed(this, UPDATE_DELAY)
            }
        }
    }

    init {
        loadTrack()
    }

    private fun loadTrack() {
        val track = searchHistoryInteractor.getTracks().find { it.trackId == trackId }
        track?.let {
            trackLiveData.value = it
            preparePlayer(it.previewUrl)
        }
    }

    private fun preparePlayer(url: String?) {
        if (url.isNullOrEmpty()) return
        playerInteractor.preparePlayer(
            url = url,
            onPrepared = {
                playerStateLiveData.postValue(PlayerState.Prepared)
            },
            onCompletion = {
                handler.removeCallbacks(updateTimeRunnable)
                playerStateLiveData.postValue(PlayerState.Prepared)
            }
        )
    }

    fun playbackControl() {
        when (playerStateLiveData.value) {
            is PlayerState.Playing -> pausePlayer()
            is PlayerState.Prepared, is PlayerState.Paused -> startPlayer()
            else -> {}
        }
    }

    fun startPlayer() {
        playerInteractor.startPlayer()
        playerStateLiveData.value = PlayerState.Playing(getCurrentPositionFormatted())
        handler.postDelayed(updateTimeRunnable, UPDATE_DELAY)
    }

    fun pausePlayer() {
        if (playerStateLiveData.value is PlayerState.Playing) {
            playerInteractor.pausePlayer()
            handler.removeCallbacks(updateTimeRunnable)
            playerStateLiveData.value = PlayerState.Paused(getCurrentPositionFormatted())
        }
    }

    private fun getCurrentPositionFormatted(): String {
        return dateFormat.format(playerInteractor.getCurrentPosition())
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(updateTimeRunnable)
        playerInteractor.release()
    }
}
