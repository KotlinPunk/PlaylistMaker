package com.practicum.playlistmaker.search.ui.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.data.models.TrackData
import com.practicum.playlistmaker.search.domain.api.intr.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.intr.SearchTracksInteractor
import com.practicum.playlistmaker.search.domain.models.ToastState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.models.TracksState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.addAll
import kotlin.text.clear

class SearchTrackViewModel(
    application: Application,
    private val searchTracksInteractor: SearchTracksInteractor,
    private val historyInteractor: SearchHistoryInteractor
) : AndroidViewModel(application) {

    private val trackList: MutableList<Track> = ArrayList()
    private var latestSearchText: String? = null
    private var searchJob: Job? = null

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    private val toastState = MutableLiveData<ToastState>(ToastState.None)
    fun observeToastState(): LiveData<ToastState> = toastState

    private val searchHistoryLiveData = MutableLiveData<List<Track>>()
    fun observeSearchHistory(): LiveData<List<Track>> = searchHistoryLiveData

    init {
        loadSearchHistory() // как только создаём viewvodel сразу загружаем историю поиска
    }

    // функция для загрузки истории поиска
    fun loadSearchHistory() {
        searchHistoryLiveData.value = historyInteractor.getTrackHistoryIntr()
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        this.latestSearchText = changedText
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            val newSearchText = latestSearchText ?: ""
            getTrack(newSearchText)
        }

    }

    private fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
    }

    fun searchHistoryOrAllHide() {
        val historyList = historyInteractor.getTrackHistoryIntr()
        if (historyList.isNotEmpty()) {
            renderState(TracksState.EmptyInputShowHistory)
        } else {
            renderState(TracksState.EmptyAll)
        }
    }

    // функция добавления в историю поиска
    fun addTrackToHistory(track: Track) {
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
        historyInteractor.addTrackToHistoryIntr(trackData)
        loadSearchHistory() // как добавили, сразу обновили LiveData
    }

    //функция очистки истории поиска
    fun clearSearchHistory() {
        historyInteractor.clearTrackHistoryIntr()
        loadSearchHistory() // как удалили снова оповестили LiveData
    }

    fun toastWasShown() {
        toastState.value = ToastState.None
    }

    fun getTrack(newSearchText: String) {
        if (newSearchText.trim().isNotEmpty()) {
            renderState(TracksState.Loading)

            viewModelScope.launch {
                searchTracksInteractor
                    .searchTracksIntr(newSearchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }

        } else {
            searchHistoryOrAllHide()
        }
    }

    private fun processResult(tracks: List<Track>?, errorMessage: String?) {
        if (tracks != null) {
            trackList.clear()
            trackList.addAll(tracks)
        }
        when {
            errorMessage != null -> {
                renderState(
                    TracksState.Error(
                        errorMessage = getApplication<Application>().getString(
                            R.string.problems_with_connection
                        )
                    )
                )
                toastState.postValue(ToastState.Show(errorMessage))
            }

            trackList.isEmpty() -> {
                renderState(
                    TracksState.EmptyList(
                        message = getApplication<Application>().getString(R.string.nothing_found)
                    )
                )
            }

            else -> {
                renderState(
                    TracksState.Content(
                        trackL = trackList
                    )
                )
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}