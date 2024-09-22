package com.practicum.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private var saveEditText = ""

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(TrackApi::class.java)

    private val trackList = ArrayList<Track>()
    private val trackListSearchHistory = ArrayList<Track>()
    private var trackAdapter = TrackAdapter(trackList)
    private val trackAdapterSearchHistory = TrackAdapter(trackListSearchHistory)

    private lateinit var arrowbackButton: ImageButton
    private lateinit var inputEditText: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var rvTrackList: RecyclerView
    private lateinit var placeholderLinearLayout: LinearLayout
    private lateinit var placeholderErrorImage: ImageView
    private lateinit var placeholderErrorText: TextView
    private lateinit var updateQueryButton: Button

    private lateinit var searchHistoryLayout: NestedScrollView
    private lateinit var searchHistoryText: TextView
    private lateinit var searchHistoryRV: RecyclerView
    private lateinit var searchHistoryClearButton: Button
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var searchHistory: SearchHistory
    private lateinit var listenerSharedPrefs: OnSharedPreferenceChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        arrowbackButton = findViewById(R.id.arrowback)
        inputEditText = findViewById(R.id.inputEditText)
        clearIcon = findViewById(R.id.clearIcon)
        rvTrackList = findViewById(R.id.rvTrackListXml)
        placeholderLinearLayout = findViewById(R.id.placeholderLinearLayout)
        placeholderErrorImage = findViewById(R.id.placeholderErrorImage)
        placeholderErrorText = findViewById(R.id.placeholderErrorText)
        updateQueryButton = findViewById(R.id.updateQueryButton)

        searchHistoryLayout = findViewById(R.id.searchHistoryLayout)
        searchHistoryText = findViewById(R.id.searchHistoryText)
        searchHistoryRV = findViewById(R.id.searchHistoryRV)
        searchHistoryClearButton = findViewById(R.id.searchHistoryClearButton)
        sharedPrefs = getSharedPreferences(SEARCH_HISTORY_SHARED_PREFS, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs)

        rvTrackList.adapter = trackAdapter
        searchHistoryRV.adapter = trackAdapterSearchHistory

        trackAdapter.onClickTrack = { track: Track ->
            searchHistory.addTrackInHistoryTrackList(track)
            trackAdapterSearchHistory.notifyDataSetChanged()
        }

        trackAdapterSearchHistory.onClickTrack = { track: Track ->
            searchHistory.addTrackInHistoryTrackList(track)
            trackAdapterSearchHistory.notifyDataSetChanged()
        }

        listenerSharedPrefs = OnSharedPreferenceChangeListener { sharedPrefs, key ->
            if (key == SEARCH_HISTORY_KEY) {
                trackListSearchHistory.clear()
                trackListSearchHistory.addAll(searchHistory.getHistoryTrackList())
                trackAdapterSearchHistory.notifyDataSetChanged()
            }
        }

        sharedPrefs.registerOnSharedPreferenceChangeListener(listenerSharedPrefs)

        arrowbackButton.setOnClickListener {
            finish()
        }

        updateQueryButton.setOnClickListener {
            getTrack()
        }

        clearIcon.setOnClickListener {
            inputEditText.setText("")
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getTrack()
            }
            false
        }

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputEditText.text.isNullOrEmpty()
                && searchHistory.getHistoryTrackList().isNotEmpty()
            ) {
                searchHistoryLayout.visibility = View.VISIBLE
                trackListSearchHistory.addAll(searchHistory.getHistoryTrackList())
                trackAdapterSearchHistory.notifyDataSetChanged()
            } else {
                searchHistoryLayout.visibility = View.GONE
            }
        }

        searchHistoryClearButton.setOnClickListener {
            searchHistoryLayout.visibility = View.GONE
            searchHistory.clearHistoryTrackList()
            trackAdapterSearchHistory.notifyDataSetChanged()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearIcon.visibility = clearIconVisibility(s)
                saveEditText = s.toString()
                if (inputEditText.hasFocus() && s?.isNullOrEmpty() == true
                    && searchHistory.getHistoryTrackList().isNotEmpty()
                ) {
                    searchHistoryLayout.visibility = View.VISIBLE
                }
                else {
                    searchHistoryLayout.visibility = View.GONE
                    trackList.clear()
                    trackAdapter.notifyDataSetChanged()
                }
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

    private fun getTrack() {
        if (inputEditText.text.isNotEmpty()) {
            itunesService.search(inputEditText.text.toString())
                .enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        if (response.code() == 200) {
                            trackList.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                trackList.addAll(response.body()?.results!!)
                                trackAdapter.notifyDataSetChanged()
                            }
                            if (trackList.isEmpty()) {
                                showMessage(getString(R.string.nothing_found), "")
                            } else {
                                showMessage("", "")
                            }
                        } else {
                            showMessage(
                                getString(R.string.problems_with_connection),
                                response.code().toString()
                            )
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        showMessage(
                            getString(R.string.problems_with_connection),
                            t.message.toString()
                        )
                    }

                })
        }
    }

    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            placeholderLinearLayout.visibility = View.VISIBLE
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            placeholderErrorText.text = text
            showVariantMessage(text)
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG).show()
            }
        } else {
            placeholderLinearLayout.visibility = View.GONE
        }
    }

    private fun showVariantMessage(text: String) {
        if (text == getString(R.string.nothing_found)) {
            placeholderErrorImage.setImageResource(R.drawable.error_search)
            updateQueryButton.visibility = View.GONE
        } else {
            placeholderErrorImage.setImageResource(R.drawable.error_internet)
            updateQueryButton.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val KEY = "Key"
        private const val itunesBaseUrl = "https://itunes.apple.com"
        private const val SEARCH_HISTORY_SHARED_PREFS = "search_history_shared_prefs"
        private const val SEARCH_HISTORY_KEY = "search_history_key"
    }
}

