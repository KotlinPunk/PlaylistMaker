package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val arrowbackButton = findViewById<ImageButton>(R.id.arrowback)

        arrowbackButton.setOnClickListener {
            finish()
        }
    }
}