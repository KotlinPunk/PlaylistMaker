package com.practicum.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //val firstButton = findViewById<Button>(R.id.search)
        //val secondButton = findViewById<Button>(R.id.media)
        //val thirdButton = findViewById<Button>(R.id.settings)

        //val buttonClickListener: View.OnClickListener = object : View.OnClickListener {
            //override fun onClick(v: View?) {
                //Toast.makeText(this@MainActivity, "Нажали на картинку 'Поиск'!", Toast.LENGTH_SHORT).show()
            //}
        //}

        //firstButton.setOnClickListener(buttonClickListener)

        //secondButton.setOnClickListener {
            //Toast.makeText(this@MainActivity, "Нажали на картинку 'Медиатека'!", Toast.LENGTH_SHORT).show()
        //}

        //thirdButton.setOnClickListener {
            //Toast.makeText(this@MainActivity, "Нажали на картинку 'Настройки'!", Toast.LENGTH_SHORT).show()
        //}

        val searchButton = findViewById<Button>(R.id.search)
        val mediaLibraryButton = findViewById<Button>(R.id.media)
        val settingsButton = findViewById<Button>(R.id.settings)

        searchButton.setOnClickListener {
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }

        mediaLibraryButton.setOnClickListener {
            val mediaLibraryIntent = Intent(this, MediaLibraryActivity::class.java)
            startActivity(mediaLibraryIntent)
        }

        settingsButton.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}
