package com.practicum.playlistmaker.search.ui.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.data.models.TrackData
import com.practicum.playlistmaker.search.domain.api.intr.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.intr.SearchTracksInteractor
import com.practicum.playlistmaker.search.domain.models.ToastState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.models.TracksState
import com.practicum.playlistmaker.utils.Creator

class SearchTrackViewModel(
    application: Application,
    private val searchTracksInteractor: SearchTracksInteractor,
    private val historyInteractor: SearchHistoryInteractor
) : AndroidViewModel(application) {

    private val trackList: MutableList<Track> = ArrayList()
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private var latestSearchText: String? = null

    private val searchRunnable = Runnable {
        val newSearchText = latestSearchText ?: ""
        getTrack(newSearchText)
    }

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
        mainThreadHandler.removeCallbacks(searchRunnable)
        mainThreadHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    override fun onCleared() {
        mainThreadHandler.removeCallbacks(searchRunnable)
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
            searchTracksInteractor.searchTracksIntr(
                newSearchText,
                object : SearchTracksInteractor.TracksConsumer {
                    override fun consume(tracks: List<Track>?, errorMessage: String?) {

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
                                mainThreadHandler.post {
                                    toastState.value = ToastState.Show(errorMessage)
                                }
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
                })
        } else {
            searchHistoryOrAllHide()
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}