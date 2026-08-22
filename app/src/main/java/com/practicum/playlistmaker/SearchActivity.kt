package com.practicum.playlistmaker

import android.view.inputmethod.InputMethodManager
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    }

    val trackList = ArrayList<Track>()
    private lateinit var backArrow :ImageView
    private lateinit var clearButton:ImageButton
    private lateinit var rvTracks :RecyclerView

    private val iTunesBaseURL = "https://itunes.apple.com/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(iTunesAPI:: class.java)
    private var currentText: String = EMPTY_TEXT

    val tracksAdapter = TrackAdapter(trackList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        backArrow = findViewById(R.id.back_arrow)
        backArrow.setOnClickListener { finish() }

        clearButton = findViewById(R.id.button_clear)
        clearButton.setOnClickListener {
            editText.text.clear()
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)
        }

        rvTracks = findViewById(R.id.rvTracks)
        rvTracks.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvTracks.adapter = tracksAdapter

        editText = findViewById(R.id.search_text_input)

        editText.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                clearButton.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
            },

            afterTextChanged = { editable -> currentText = editable?.toString() ?: "" }
        )
        var trackList = listOf<Track>(
            Track(
                "Smells Like Teen Spirit",
                "Nirvana",
                "5:01",
                "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg1"
            ),
            Track(
                "Billie Jean",
                "Michael Jackson",
                "4:35",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
            ),
            Track(
                "Stayin' Alive",
                "Bee Gees",
                "4:10",
                "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Whole Lotta Love",
                "Led Zeppelin",
                "5:33",
                "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
            ),
            Track(
                "Sweet Child O'Mine",
                "Guns N' Roses",
                "5:03",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
            )
        )
        val tracksAdapter = TrackAdapter(trackList)
        val rvTracks = findViewById<RecyclerView>(R.id.rvTracks)
        rvTracks.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvTracks.adapter = tracksAdapter
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