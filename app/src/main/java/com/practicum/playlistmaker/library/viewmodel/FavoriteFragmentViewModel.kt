package com.practicum.playlistmaker.library.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.domain.api.intr.LibraryDbInteractor
import com.practicum.playlistmaker.library.domain.models.FavoriteFragmentState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class FavoriteFragmentViewModel(
    private val context: Context,
    private val libraryFavoriteDbInteractor: LibraryDbInteractor
) : ViewModel() {

    private val _stateLiveData = MutableLiveData<FavoriteFragmentState>()
    val stateLiveData: LiveData<FavoriteFragmentState> = _stateLiveData


    init {
        fillData()
    }

    fun fillData() {
        viewModelScope.launch {
            libraryFavoriteDbInteractor
                .getTracksFromFavoriteIntr()
                .collect { tracks -> processResult(tracks) }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoriteFragmentState.Error(context.getString(R.string.empty_library)))
        } else {
            renderState(FavoriteFragmentState.Content(tracks))
        }
    }

    private fun renderState(state: FavoriteFragmentState) {
        _stateLiveData.value =
            state // можем использовать setValue, т.к. корутина выполняется в основном потоке
    }
}