package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class SearchActivity : AppCompatActivity() {
    private var saveEditText = STRING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val arrowbackButton = findViewById<ImageButton>(R.id.arrowback)
        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearIcon = findViewById<ImageView>(R.id.clearIcon)
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

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
        saveEditText = savedInstanceState.getString(KEY, STRING)
        findViewById<TextView>(R.id.inputEditText).setText(saveEditText)
    }

    companion object {
        const val KEY = "Key"
        const val STRING = ""
    }
}

