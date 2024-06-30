package com.practicum.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val arrowbackButton = findViewById<ImageButton>(R.id.arrowback)

        arrowbackButton.setOnClickListener {
            val arrowbackIntent = Intent(this, MainActivity::class.java)
            startActivity(arrowbackIntent)
        }
    }
}