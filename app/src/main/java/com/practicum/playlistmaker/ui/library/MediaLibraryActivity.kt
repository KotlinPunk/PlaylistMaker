package com.practicum.playlistmaker.ui.library

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.practicum.playlistmaker.R

class MediaLibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_library)

        val arrowbackButton = findViewById<ImageButton>(R.id.arrowback)

        arrowbackButton.setOnClickListener {
            finish()
        }
    }
}