package com.practicum.playlistmaker.ui.search

import android.view.inputmethod.InputMethodManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.Utils.toTrack
import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.data.network.ITunesAPI
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.player.PlayerActivity
import com.practicum.playlistmaker.ui.tracks.TrackAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var editText: EditText

    companion object {
        const val KEY_SEARCH_TEXT = "KEY_SEARCH_TEXT"
        const val EMPTY_TEXT = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    val trackList = mutableListOf<Track>()
    private val historyTrackList = mutableListOf<Track>()
    private lateinit var backArrow :ImageView
    private lateinit var clearButton:ImageButton
    private lateinit var problemsIconPlaceholder: ImageView
    private lateinit var problemsTextPlaceholder: TextView
    private lateinit var problemsButtonPlaceholder: Button
    private lateinit var rvTracks :RecyclerView
    private lateinit var tracksAdapter: TrackAdapter
    private lateinit var rvTrackHistory: LinearLayout
    private lateinit var historyTracks: RecyclerView
    private lateinit var tracksHistoryAdapter: TrackAdapter
    private lateinit var clearHistoryButton: MaterialButton
    private lateinit var progressBar: ProgressBar
    private val iTunesBaseURL = "https://itunes.apple.com/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesAPI:: class.java)
    private var currentText: String = EMPTY_TEXT

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val searchHistory = (application as App).searchHistory

        tracksAdapter = TrackAdapter(trackList) { track ->
            searchHistory.addTrack(track)
            startActivity(PlayerActivity.createIntent(this, track.trackId))
        }

        historyTrackList.addAll(searchHistory.getTracks())
        tracksHistoryAdapter = TrackAdapter(historyTrackList) { track ->
            startActivity(PlayerActivity.createIntent(this, track.trackId))
            searchHistory.addTrack(track)
            val history = searchHistory.getTracks()
            historyTrackList.clear()
            historyTrackList.addAll(history)
            tracksHistoryAdapter.notifyDataSetChanged()
        }

        backArrow = findViewById(R.id.back_arrow)
        backArrow.setOnClickListener { finish() }

        clearButton = findViewById(R.id.button_clear)
        clearButton.setOnClickListener {
            editText.text.clear()
            trackList.clear()
            tracksAdapter.notifyDataSetChanged()
            rvTracks.visibility = View.GONE
            goneProblemsPlaceholders()
            
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)
        }

        problemsIconPlaceholder = findViewById(R.id.problem_icon)
        problemsTextPlaceholder = findViewById(R.id.problem_text)
        problemsButtonPlaceholder= findViewById(R.id.problem_button)

        rvTrackHistory = findViewById(R.id.history_view)
        historyTracks = findViewById(R.id.rvHistoryTracks)
        historyTracks.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        historyTracks.adapter = tracksHistoryAdapter

        clearHistoryButton = findViewById(R.id.clear_history_button)
        clearHistoryButton.setOnClickListener {
            searchHistory.clear()
            historyTrackList.clear()
            tracksHistoryAdapter.notifyDataSetChanged()
            rvTrackHistory.visibility = View.GONE
        }

        rvTracks = findViewById(R.id.rvTracks)
        rvTracks.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvTracks.adapter = tracksAdapter
        rvTracks.visibility = View.GONE

        progressBar = findViewById(R.id.progressBar)

        val tracksSearch: (String) -> Unit = { requestText ->
            progressBar.visibility = View.VISIBLE
            tracksInteractor.searchTracks(requestText, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>) {
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        trackList.clear()
                        if (foundTracks.isNotEmpty()) {
                            goneProblemsPlaceholders()
                            trackList.addAll(foundTracks)
                            tracksAdapter.notifyDataSetChanged()
                            rvTracks.visibility = View.VISIBLE
                        } else {
                            showMessage("")
                        }
                    }
                }

                override fun onError(error: Throwable) {
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        showMessage(getString(R.string.something_went_wrong_placeholder))
                    }
                }
            })
        }

        val searchRunnable = Runnable { tracksSearch(currentText) }

        fun searchDebounce() {
            handler.removeCallbacks(searchRunnable)
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }

        fun showHistory(history: List<Track>){
            historyTrackList.clear()
            historyTrackList.addAll(history)
            tracksHistoryAdapter.notifyDataSetChanged()
            rvTrackHistory.visibility = View.VISIBLE
            rvTracks.visibility = View.GONE
            goneProblemsPlaceholders()
        }


        editText = findViewById(R.id.search_text_input)
        editText.setOnFocusChangeListener { _, hasFocus ->
            val history = searchHistory.getTracks()
            if (hasFocus && editText.text.isEmpty() && history.isNotEmpty()) {
                showHistory(history)
            } else {
                rvTrackHistory.visibility = View.GONE
            }
        }


        editText.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                clearButton.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE

                val history = searchHistory.getTracks()
                if (editText.hasFocus() && text?.isEmpty() == true && history.isNotEmpty()) {
                    showHistory(history)
                } else {
                    rvTrackHistory.visibility = View.GONE
                }

                if (text?.isEmpty() == true) {
                    handler.removeCallbacks(searchRunnable)
                    trackList.clear()
                    tracksAdapter.notifyDataSetChanged()
                    rvTracks.visibility = View.GONE
                    goneProblemsPlaceholders()
                } else {
                    searchDebounce()
                }
            },

            afterTextChanged = { editable -> currentText = editable?.toString() ?: "" }
        )
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (editText.text.isNotEmpty()){
                    currentText = editText.text.toString()
                    tracksSearch(currentText)
                    rvTrackHistory.visibility = View.GONE
                }
            }
            false
        }

        problemsButtonPlaceholder.setOnClickListener {
            if (currentText.isNotEmpty()) {
                tracksSearch(currentText)
            }
        }
    }

    private fun showMessage(message: String) {
        trackList.clear()
        tracksAdapter.notifyDataSetChanged()
        rvTracks.visibility = View.GONE
        progressBar.visibility = View.GONE
        if (message.isEmpty()){
            problemsIconPlaceholder.setImageResource(R.drawable.ic_nothing_result_placeholder_120)
            problemsTextPlaceholder.text = getString(R.string.nothing_result_placeholder)
        }
        else{
            problemsIconPlaceholder.setImageResource(R.drawable.ic_connection_problem_120)
            problemsTextPlaceholder.text = getString(R.string.something_went_wrong_placeholder)
            problemsButtonPlaceholder.visibility = View.VISIBLE
        }
        problemsIconPlaceholder.visibility = View.VISIBLE
        problemsTextPlaceholder.visibility = View.VISIBLE
    }

    private fun goneProblemsPlaceholders() {
        problemsIconPlaceholder.visibility = View.GONE
        problemsTextPlaceholder.visibility = View.GONE
        problemsButtonPlaceholder.visibility = View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_TEXT, currentText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentText = savedInstanceState.getString(KEY_SEARCH_TEXT, EMPTY_TEXT)
        editText.setText(currentText)
        if (currentText.isNotEmpty()) {
            editText.setSelection(currentText.length)
        }
    }
}
