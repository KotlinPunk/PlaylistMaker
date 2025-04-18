package com.practicum.playlistmaker.search.ui.search

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.player.AudioplayerActivity
import com.practicum.playlistmaker.search.data.models.TrackData
import com.practicum.playlistmaker.search.domain.models.ToastState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.models.TracksState
import com.practicum.playlistmaker.search.ui.viewmodel.SearchTrackViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }

    private var saveEditText = ""

    private val trackList = ArrayList<Track>()
    private val trackListSearchHistory = ArrayList<Track>()
    private var trackAdapter = TrackAdapter(trackList)
    private val trackAdapterSearchHistory = TrackAdapter(trackListSearchHistory)
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private var textWatcher: TextWatcher? = null
    private var bottomNavigationView: BottomNavigationView? = null

    private val viewModel by viewModel<SearchTrackViewModel>()

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputMethodManager =
            requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputEditText = binding.inputEditText
        clearIcon = binding.clearIcon
        rvTrackList = binding.rvTrackListXml
        placeholderLinearLayout = binding.placeholderLinearLayout
        placeholderErrorImage = binding.placeholderErrorImage
        placeholderErrorText = binding.placeholderErrorText
        updateQueryButton = binding.updateQueryButton
        progressBar = binding.progressBar

        searchHistoryLayout = binding.searchHistoryLayout
        searchHistoryText = binding.searchHistoryText
        searchHistoryRV = binding.searchHistoryRV
        searchHistoryClearButton = binding.searchHistoryClearButton

        rvTrackList.adapter = trackAdapter
        searchHistoryRV.adapter = trackAdapterSearchHistory

        bottomNavigationView = activity?.findViewById(R.id.bottomNavigationView)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeToastState().observe(viewLifecycleOwner) { toastState ->
            if (toastState is ToastState.Show) {
                showToast(toastState.additionalMessage)
                viewModel.toastWasShown()
            }
        }

        viewModel.observeSearchHistory().observe(viewLifecycleOwner) { history ->
            trackListSearchHistory.clear()
            trackListSearchHistory.addAll(history)
            trackAdapterSearchHistory.notifyDataSetChanged()
            searchHistoryLayout.isVisible = history.isNotEmpty()
        } // теперь тут слушаем измнения в истории списка

        trackAdapter.onClickTrack = { track: Track ->
            if (clickDebounce()) {
                viewModel.addTrackToHistory(track)
                trackAdapterSearchHistory.notifyDataSetChanged()
                val trackData = TrackData(
                    track.trackName,
                    track.artistName,
                    track.trackTimeMillis,
                    track.artworkUrl100,
                    track.trackId,
                    track.collectionName,
                    track.releaseDate,
                    track.primaryGenreName,
                    track.country,
                    track.previewUrl
                )
                val audioPlayerIntent = Intent(requireContext(), AudioplayerActivity::class.java)
                audioPlayerIntent.putExtra(TRACK_DATA, trackData)
                startActivity(audioPlayerIntent)
            }
        }

        trackAdapterSearchHistory.onClickTrack = { track: Track ->
            if (clickDebounce()) {
                viewModel.addTrackToHistory(track)
                trackAdapterSearchHistory.notifyDataSetChanged()
                val trackData = TrackData(
                    track.trackName,
                    track.artistName,
                    track.trackTimeMillis,
                    track.artworkUrl100,
                    track.trackId,
                    track.collectionName,
                    track.releaseDate,
                    track.primaryGenreName,
                    track.country,
                    track.previewUrl
                )
                val audioPlayerIntent = Intent(requireContext(), AudioplayerActivity::class.java)
                audioPlayerIntent.putExtra(TRACK_DATA, trackData)
                startActivity(audioPlayerIntent)
            }
        }

        updateQueryButton.setOnClickListener {
            viewModel.searchDebounce(saveEditText)
        }

        clearIcon.setOnClickListener {
            inputEditText.setText("")
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchDebounce(saveEditText)
            }
            false
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isNullOrEmpty()) {
                viewModel.searchHistoryOrAllHide()
            }
        }

        searchHistoryClearButton.setOnClickListener {
            searchHistoryLayout.isVisible = true
            viewModel.clearSearchHistory()
            trackAdapterSearchHistory.notifyDataSetChanged()
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                clearIcon.visibility = clearIconVisibility(s)
                viewModel.searchDebounce(changedText = s?.toString() ?: "")

                if (inputEditText.hasFocus() && s?.isNullOrEmpty() == true) {
                    viewModel.searchHistoryOrAllHide()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        textWatcher?.let { inputEditText.addTextChangedListener(it) }
    }

    override fun onResume() {
        super.onResume()
        val searchText = inputEditText.text.toString()
        if (searchText.isNotEmpty()) {
            viewModel.getTrack(searchText)
        } else {
            viewModel.getTrack("") // Обновляем состояние, если поле пустое
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textWatcher?.let { inputEditText.removeTextChangedListener(it) }
        _binding = null
    }

    private fun clearIconVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun isVisibleKeyboard(view: View?): Boolean {
        if (view == null) {
            return false
        }
        val inputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        if (inputMethodManager == null) {
            return false
        }
        return inputMethodManager.isAcceptingText
    }

    private fun render(state: TracksState) {
        when (state) {
            is TracksState.Loading -> showLoading()
            is TracksState.EmptyAll -> showEmptyAll()
            is TracksState.Content -> showContent(state.trackL)
            is TracksState.Error -> showError(state.errorMessage)
            is TracksState.EmptyList -> showEmptyList(state.message)
            is TracksState.EmptyInputShowHistory -> showSearchHistory()
        }
    }

    fun showLoading() {
        rvTrackList.isVisible = false
        placeholderLinearLayout.isVisible = false
        progressBar.isVisible = true
        searchHistoryLayout.isVisible = false
    }

    fun showEmptyAll() {
        rvTrackList.isVisible = false
        placeholderLinearLayout.isVisible = false
        progressBar.isVisible = false
        searchHistoryLayout.isVisible = false
    }

    fun showContent(tracks: List<Track>) {
        rvTrackList.isVisible = true
        placeholderLinearLayout.isVisible = false
        progressBar.isVisible = false
        searchHistoryLayout.isVisible = false
        trackList.clear()
        trackList.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    fun showError(errorMessage: String) {
        rvTrackList.isVisible = false
        placeholderLinearLayout.isVisible = true
        progressBar.isVisible = false
        searchHistoryLayout.isVisible = false
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
        placeholderErrorText.text = errorMessage
        placeholderErrorImage.setImageResource(R.drawable.error_internet)
        updateQueryButton.isVisible = true
    }

    fun showEmptyList(emptyMessage: String) {
        rvTrackList.isVisible = false
        placeholderLinearLayout.isVisible = true
        progressBar.isVisible = false
        searchHistoryLayout.isVisible = false
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
        placeholderErrorText.text = emptyMessage
        placeholderErrorImage.setImageResource(R.drawable.error_search)
        updateQueryButton.isVisible = false
    }

    fun showSearchHistory() {
        rvTrackList.isVisible = false
        placeholderLinearLayout.isVisible = false
        progressBar.isVisible = false
        searchHistoryLayout.isVisible = true
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(requireContext(), additionalMessage, Toast.LENGTH_LONG).show()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainThreadHandler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val TRACK_DATA = "track_data"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}