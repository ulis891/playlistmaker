package com.practicum.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val battonSerch = findViewById<Button>(R.id.button_search)
        val battonMedia = findViewById<Button>(R.id.button_media)
        val battonSettings = findViewById<Button>(R.id.button_settings)

        battonSerch.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на поиск!", Toast.LENGTH_SHORT).show()
        }
        battonMedia . setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на медиатеку!", Toast.LENGTH_SHORT).show()
        }

        val imageClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажали на Настройки но через анонимный класс!", Toast.LENGTH_SHORT).show()
            }
        }
        battonSettings.setOnClickListener(imageClickListener)
    }
}