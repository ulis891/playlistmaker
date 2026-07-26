package com.practicum.playlistmaker

import android.view.inputmethod.InputMethodManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SearchActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private var currentText: String = ""
    private val keySearchText = "KEY_SEARCH_TEXT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backArrow = findViewById<ImageView>(R.id.back_arrow)
        backArrow.setOnClickListener{ finish() }

        val clearButton = findViewById<ImageButton>(R.id.button_clear)
        clearButton.setOnClickListener {
            editText.text.clear()
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)
        }

        editText = findViewById(R.id.search_text_input)
        editText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {
                currentText = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               if (!s.isNullOrEmpty()){
                   clearButton.visibility = View.VISIBLE
               }
                   else{
                       clearButton.visibility = View.GONE
               }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(keySearchText, currentText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentText = savedInstanceState.getString(keySearchText, "")
        editText.setText(currentText)
        if (currentText.isNotEmpty()) {
            editText.setSelection(currentText.length)
        }
    }
}