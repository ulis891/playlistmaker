package com.practicum.playlistmaker

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

        val editText = findViewById<EditText>(R.id.search_text_input)

        val clearButton = findViewById<ImageButton>(R.id.button_clear)
        clearButton.setOnClickListener {
            editText.text.clear()
        }

        editText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {
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

}