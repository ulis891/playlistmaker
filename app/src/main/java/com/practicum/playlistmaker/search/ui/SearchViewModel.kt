package com.practicum.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.models.SearchState

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                val tracksInteractor = Creator.provideTracksInteractor()
                val searchHistoryInteractor = Creator.provideSearchHistoryInteractor(app)
                SearchViewModel(tracksInteractor, searchHistoryInteractor)
            }
        }
    }

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val handler = Handler(Looper.getMainLooper())
    private var latestSearchText: String? = null

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)

            tracksInteractor.searchTracks(newSearchText, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>) {
                    handler.post {
                        if (foundTracks.isNotEmpty()) {
                            renderState(SearchState.Content(foundTracks))
                        } else {
                            renderState(SearchState.Empty(""))
                        }
                    }
                }

                override fun onError(error: Throwable) {
                    handler.post {
                        renderState(SearchState.Error(error.message ?: ""))
                    }
                }
            })
        }
    }

    fun onTextChanged(changedText: String, hasFocus: Boolean) {
        if (changedText.isEmpty()) {
            handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
            latestSearchText = ""
            if (hasFocus) {
                showHistoryIfNotEmpty()
            } else {
                renderState(SearchState.Clear)
            }
        } else {
            searchDebounce(changedText)
        }
    }

    fun onFocusChanged(hasFocus: Boolean, text: String) {
        if (hasFocus && text.isEmpty()) {
            showHistoryIfNotEmpty()
        }
    }

    fun onClearInput() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        latestSearchText = ""
        showHistoryIfNotEmpty()
    }

    fun onTrackClick(track: Track) {
        searchHistoryInteractor.addTrack(track)
        if (stateLiveData.value is SearchState.History) {
            val updatedHistory = searchHistoryInteractor.getTracks()
            if (updatedHistory.isNotEmpty()) {
                renderState(SearchState.History(updatedHistory))
            } else {
                renderState(SearchState.Clear)
            }
        }
    }

    fun clearHistory() {
        searchHistoryInteractor.clear()
        renderState(SearchState.Clear)
    }

    private fun showHistoryIfNotEmpty() {
        val history = searchHistoryInteractor.getTracks()
        if (history.isNotEmpty()) {
            renderState(SearchState.History(history))
        } else {
            renderState(SearchState.Clear)
        }
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }
}
