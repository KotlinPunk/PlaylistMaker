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
import com.practicum.playlistmaker.search.domain.api.intr.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.intr.SearchTracksInteractor
import com.practicum.playlistmaker.search.domain.models.ToastState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.models.TracksState
import com.practicum.playlistmaker.utils.Creator

class SearchTrackViewModel(application: Application) : AndroidViewModel(application) {

    private val creator: Creator by lazy { Creator(application) }
    private val searchTracksInteractor: SearchTracksInteractor by lazy {
        creator.searchTracksInteractor
    }
    private val historyInteractor: SearchHistoryInteractor by lazy {
        creator.searchHistoryInteractor
    }

    private val trackList = ArrayList<Track>()
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private var latestSearchText: String? = null
    private var lastSearchText: String? = null

    private val searchRunnable = {
        val newSearchText = lastSearchText ?: ""
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
        this.lastSearchText = changedText
        mainThreadHandler.removeCallbacks(searchRunnable)
        mainThreadHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    override fun onCleared() {
        mainThreadHandler.removeCallbacks(searchRunnable)
    }

    private fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
    }

    private fun searchHistoryOrAllHide() {
        val historyList = historyInteractor.getTrackHistoryIntr()
        if (historyList.isEmpty()) {
            renderState(TracksState.EmptyInput)
        } else {
            renderState(TracksState.EmptyAll)
        }
    }

    // функция добавления в историю поиска
    fun addTrackToHistory(track: Track){
        historyInteractor.addTrackToHistoryIntr(track)
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
        if (newSearchText.isNotEmpty()) {
            renderState(
                TracksState.Loading
            )
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
                                toastState.value = ToastState.Show(errorMessage)
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

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchTrackViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }
}