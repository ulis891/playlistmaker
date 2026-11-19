package com.practicum.playlistmaker.search.ui

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.models.SearchState

class SearchActivity : AppCompatActivity() {

    companion object {
        const val KEY_SEARCH_TEXT = "KEY_SEARCH_TEXT"
        const val EMPTY_TEXT = ""
    }

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel

    private val trackList = mutableListOf<Track>()
    private val historyTrackList = mutableListOf<Track>()

    private lateinit var tracksAdapter: TrackAdapter
    private lateinit var tracksHistoryAdapter: TrackAdapter

    private var currentText: String = EMPTY_TEXT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this, SearchViewModel.getFactory())[SearchViewModel::class.java]

        tracksAdapter = TrackAdapter(trackList) { track ->
            viewModel.onTrackClick(track)
            startActivity(PlayerActivity.createIntent(this, track.trackId))
        }

        tracksHistoryAdapter = TrackAdapter(historyTrackList) { track ->
            viewModel.onTrackClick(track)
            startActivity(PlayerActivity.createIntent(this, track.trackId))
        }

        binding.rvTracks.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvTracks.adapter = tracksAdapter

        binding.rvHistoryTracks.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvHistoryTracks.adapter = tracksHistoryAdapter

        binding.backArrow.setOnClickListener { finish() }

        binding.buttonClear.setOnClickListener {
            binding.searchTextInput.text.clear()
            viewModel.onClearInput()
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.searchTextInput.windowToken, 0)
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        binding.problemButton.setOnClickListener {
            if (currentText.isNotEmpty()) {
                viewModel.searchRequest(currentText)
            }
        }

        binding.searchTextInput.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onFocusChanged(hasFocus, binding.searchTextInput.text.toString())
        }

        binding.searchTextInput.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                val textString = text?.toString() ?: ""
                binding.buttonClear.visibility = if (textString.isEmpty()) View.GONE else View.VISIBLE
                viewModel.onTextChanged(textString, binding.searchTextInput.hasFocus())
            },
            afterTextChanged = { editable ->
                currentText = editable?.toString() ?: ""
            }
        )

        binding.searchTextInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (currentText.isNotEmpty()) {
                    viewModel.searchRequest(currentText)
                }
            }
            false
        }

        viewModel.observeState().observe(this) { state ->
            render(state)
        }
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Error -> showError()
            is SearchState.Empty -> showEmpty()
            is SearchState.History -> showHistory(state.tracks)
            is SearchState.Clear -> showClear()
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvTracks.visibility = View.GONE
        binding.historyView.visibility = View.GONE
        goneProblemsPlaceholders()
    }

    private fun showContent(tracks: List<Track>) {
        binding.progressBar.visibility = View.GONE
        binding.historyView.visibility = View.GONE
        goneProblemsPlaceholders()

        trackList.clear()
        trackList.addAll(tracks)
        tracksAdapter.notifyDataSetChanged()
        binding.rvTracks.visibility = View.VISIBLE
    }

    private fun showEmpty() {
        binding.progressBar.visibility = View.GONE
        binding.rvTracks.visibility = View.GONE
        binding.historyView.visibility = View.GONE

        trackList.clear()
        tracksAdapter.notifyDataSetChanged()

        binding.problemIcon.setImageResource(R.drawable.ic_nothing_result_placeholder_120)
        binding.problemText.text = getString(R.string.nothing_result_placeholder)
        binding.problemButton.visibility = View.GONE
        binding.problemIcon.visibility = View.VISIBLE
        binding.problemText.visibility = View.VISIBLE
    }

    private fun showError() {
        binding.progressBar.visibility = View.GONE
        binding.rvTracks.visibility = View.GONE
        binding.historyView.visibility = View.GONE

        trackList.clear()
        tracksAdapter.notifyDataSetChanged()

        binding.problemIcon.setImageResource(R.drawable.ic_connection_problem_120)
        binding.problemText.text = getString(R.string.something_went_wrong_placeholder)
        binding.problemButton.visibility = View.VISIBLE
        binding.problemIcon.visibility = View.VISIBLE
        binding.problemText.visibility = View.VISIBLE
    }

    private fun showHistory(tracks: List<Track>) {
        binding.progressBar.visibility = View.GONE
        binding.rvTracks.visibility = View.GONE
        goneProblemsPlaceholders()

        historyTrackList.clear()
        historyTrackList.addAll(tracks)
        tracksHistoryAdapter.notifyDataSetChanged()
        binding.historyView.visibility = View.VISIBLE
    }

    private fun showClear() {
        binding.progressBar.visibility = View.GONE
        binding.rvTracks.visibility = View.GONE
        binding.historyView.visibility = View.GONE
        goneProblemsPlaceholders()

        trackList.clear()
        tracksAdapter.notifyDataSetChanged()
    }

    private fun goneProblemsPlaceholders() {
        binding.problemIcon.visibility = View.GONE
        binding.problemText.visibility = View.GONE
        binding.problemButton.visibility = View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_TEXT, currentText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentText = savedInstanceState.getString(KEY_SEARCH_TEXT, EMPTY_TEXT)
        binding.searchTextInput.setText(currentText)
        if (currentText.isNotEmpty()) {
            binding.searchTextInput.setSelection(currentText.length)
        }
    }
}
