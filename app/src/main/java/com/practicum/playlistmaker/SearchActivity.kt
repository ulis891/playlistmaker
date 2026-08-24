package com.practicum.playlistmaker

import android.view.inputmethod.InputMethodManager
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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
    private lateinit var problemsIconPlaceholder: ImageView
    private lateinit var problemsTextPlaceholder: TextView
    private lateinit var problemsButtonPlaceholder: Button
    private lateinit var rvTracks :RecyclerView

    private val iTunesBaseURL = "https://itunes.apple.com/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesAPI:: class.java)
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

        problemsIconPlaceholder = findViewById(R.id.problem_icon)
        problemsTextPlaceholder = findViewById(R.id.problem_text)
        problemsButtonPlaceholder= findViewById(R.id.problem_button)

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

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (editText.text.isNotEmpty()){
                    iTunesService.getSongs(editText.text.toString()).enqueue(object : Callback<TracksResponse> {
                        override fun onResponse(call: Call<TracksResponse>, response: Response<TracksResponse>) {
                            if (response.code() == 200) {
                                trackList.clear()
                                if (response.body()?.results?.isNotEmpty() == true) {
                                    trackList.addAll(response.body()?.results!!)
                                }
                                tracksAdapter.notifyDataSetChanged()
                            }
                        }

                        override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                            t.printStackTrace()
                        }
                    })
                }
            }
            false
        }
    }

    private fun showMessage(message: String) {
        trackList.clear()
        tracksAdapter.notifyDataSetChanged()
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