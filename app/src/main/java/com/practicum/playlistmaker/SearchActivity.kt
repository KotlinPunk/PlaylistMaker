package com.practicum.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class SearchActivity : AppCompatActivity() {
    private var saveEditText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val arrowbackButton = findViewById<ImageButton>(R.id.arrowback)
        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearIcon = findViewById<ImageView>(R.id.clearIcon)
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val rvTrackList = findViewById<RecyclerView>(R.id.rvTrackListXml)
        val trackAdapter = TrackAdapter(Track.trackList)

        rvTrackList.adapter = trackAdapter

        arrowbackButton.setOnClickListener {
            finish()
        }

        clearIcon.setOnClickListener {
            inputEditText.setText("")
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

         val textWatcher = object : TextWatcher {
             override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                 //empty
             }

             override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                 clearIcon.visibility = clearIconVisibility(s)
                 saveEditText = s.toString()
             }

             override fun afterTextChanged(s: Editable?) {
                 // empty
             }
         }
        inputEditText.addTextChangedListener(textWatcher)
    }

    private fun clearIconVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(textwatcher: Bundle) {
        super.onSaveInstanceState(textwatcher)
        textwatcher.putString(KEY, saveEditText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        saveEditText = savedInstanceState.getString(KEY, saveEditText)
        findViewById<TextView>(R.id.inputEditText).setText(saveEditText)
    }

    companion object {
        private const val KEY = "Key"
    }
}

