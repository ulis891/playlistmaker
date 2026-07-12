package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backArrow = findViewById<Button>(R.id.baсk_arrow)

        backArrow.setOnClickListener{
            val settingsIntent = Intent(this, MainActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}