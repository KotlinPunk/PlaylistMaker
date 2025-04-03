package com.practicum.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.impl.storage.SearchHistoryImpl
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.data.dto.TrackResponseDto
import com.practicum.playlistmaker.data.network.TrackApi
import com.practicum.playlistmaker.domain.api.intr.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.intr.SearchTracksInteractor
import com.practicum.playlistmaker.presentation.TrackAdapter
import com.practicum.playlistmaker.ui.audioplayer.AudioplayerActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private var saveEditText = ""

    private val creator: Creator by lazy { Creator(this) }
    private val searchTracksInteractor: SearchTracksInteractor by lazy {
        creator.searchTracksInteractor
    }
    private val historyInteractor: SearchHistoryInteractor by lazy {
        creator.searchHistoryInteractor
    }

    private val trackList = ArrayList<Track>()
    private val trackListSearchHistory = ArrayList<Track>()
    private var trackAdapter = TrackAdapter(trackList)
    private val trackAdapterSearchHistory = TrackAdapter(trackListSearchHistory)
    private var isClickAllowed = true
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val searchRunnable = { getTrack() }


    private lateinit var arrowbackButton: ImageButton
    private lateinit var inputEditText: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var rvTrackList: RecyclerView
    private lateinit var placeholderLinearLayout: LinearLayout
    private lateinit var placeholderErrorImage: ImageView
    private lateinit var placeholderErrorText: TextView
    private lateinit var updateQueryButton: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var searchHistoryLayout: NestedScrollView
    private lateinit var searchHistoryText: TextView
    private lateinit var searchHistoryRV: RecyclerView
    private lateinit var searchHistoryClearButton: Button
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var searchHistory: SearchHistoryImpl
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
        progressBar = findViewById(R.id.progressBar)

        searchHistoryLayout = findViewById(R.id.searchHistoryLayout)
        searchHistoryText = findViewById(R.id.searchHistoryText)
        searchHistoryRV = findViewById(R.id.searchHistoryRV)
        searchHistoryClearButton = findViewById(R.id.searchHistoryClearButton)
        sharedPrefs = getSharedPreferences(SEARCH_HISTORY_SHARED_PREFS, MODE_PRIVATE)
        searchHistory = SearchHistoryImpl(sharedPrefs)

        rvTrackList.adapter = trackAdapter
        searchHistoryRV.adapter = trackAdapterSearchHistory

        trackAdapter.onClickTrack = { track: Track ->
            if (clickDebounce()) {
                historyInteractor.addTrackToHistoryIntr(track)
                trackAdapterSearchHistory.notifyDataSetChanged()
                val audioPlayerIntent = Intent(this, AudioplayerActivity::class.java)
                audioPlayerIntent.putExtra(TRACK_DATA, track)
                startActivity(audioPlayerIntent)
            }
        }

        trackAdapterSearchHistory.onClickTrack = { track: Track ->
            if (clickDebounce()) {
                historyInteractor.addTrackToHistoryIntr(track)
                trackAdapterSearchHistory.notifyDataSetChanged()
                val audioPlayerIntent = Intent(this, AudioplayerActivity::class.java)
                audioPlayerIntent.putExtra(TRACK_DATA, track)
                startActivity(audioPlayerIntent)
            }
        }

        listenerSharedPrefs = OnSharedPreferenceChangeListener { sharedPrefs, key ->
            if (key == SEARCH_HISTORY_KEY) {
                trackListSearchHistory.clear()
                trackListSearchHistory.addAll(historyInteractor.getTrackHistoryIntr())
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
                && historyInteractor.getTrackHistoryIntr().isNotEmpty()
            ) {
                searchHistoryLayout.isVisible = true
                trackListSearchHistory.addAll(historyInteractor.getTrackHistoryIntr())
                trackAdapterSearchHistory.notifyDataSetChanged()
            } else {
                searchHistoryLayout.isVisible = false
            }
        }

        searchHistoryClearButton.setOnClickListener {
            searchHistoryLayout.isVisible = true
            historyInteractor.clearTrackHistoryIntr()
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
                    && historyInteractor.getTrackHistoryIntr().isNotEmpty()
                ) {
                    searchHistoryLayout.isVisible = true
                    hideAll()
                } else {
                    searchHistoryLayout.isVisible = false
                    trackList.clear()
                    trackAdapter.notifyDataSetChanged()
                }
                searchDebounce()
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
            showLoadingState()
            searchTracksInteractor.searchTracksIntr(
                inputEditText.text.toString(),
                object : SearchTracksInteractor.TracksConsumer {
                    override fun consume(tracks: List<Track>?) {
                        mainThreadHandler.post {
                            when {
                                tracks == null -> {
                                    showMessage(
                                        getString(R.string.problems_with_connection),
                                        ""
                                    )
                                }

                                tracks.isEmpty() -> {
                                    showMessage(getString(R.string.nothing_found), "")
                                }

                                else -> {
                                    trackList.clear()
                                    trackList.addAll(tracks)
                                    trackAdapter.notifyDataSetChanged()
                                    showTrackList()
                                }
                            }
                        }
                    }
                })
        } else {
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            hideAll()
        }
    }

    private fun showLoadingState() { //отображение состояния загрузки
        progressBar.isVisible = true
        placeholderLinearLayout.isVisible = false
        rvTrackList.isVisible = false
    }

    private fun showTrackList() { //отображение трек листа
        progressBar.isVisible = false
        placeholderLinearLayout.isVisible = false
        rvTrackList.isVisible = true
    }

    private fun hideAll() { //всё скрыто
        progressBar.isVisible = false
        placeholderLinearLayout.isVisible = false
        rvTrackList.isVisible = false
    }

    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            progressBar.isVisible = false
            placeholderLinearLayout.isVisible = true
            rvTrackList.isVisible = false
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            placeholderErrorText.text = text
            showVariantMessage(text)
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG).show()
            }
        } else {
            placeholderLinearLayout.isVisible = false
        }
    }

    private fun showVariantMessage(text: String) {
        if (text == getString(R.string.nothing_found)) {
            placeholderErrorImage.setImageResource(R.drawable.error_search)
            updateQueryButton.isVisible = false
        } else {
            placeholderErrorImage.setImageResource(R.drawable.error_internet)
            updateQueryButton.isVisible = true
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainThreadHandler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        mainThreadHandler.removeCallbacks(searchRunnable)
        mainThreadHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    companion object {
        private const val KEY = "Key"
        private const val itunesBaseUrl = "https://itunes.apple.com"
        private const val SEARCH_HISTORY_SHARED_PREFS = "search_history_shared_prefs"
        private const val SEARCH_HISTORY_KEY = "search_history_key"
        private const val TRACK_DATA = "track_data"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}