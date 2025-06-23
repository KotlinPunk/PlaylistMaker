package com.practicum.playlistmaker.library.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.api.intr.LibraryDbInteractor
import com.practicum.playlistmaker.library.domain.api.intr.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.models.InfoOfPlaylistState
import kotlinx.coroutines.launch

class InfoOfPlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val libraryDbInteractor: LibraryDbInteractor
) : ViewModel() {

    private val _playlistInfo = MutableLiveData<InfoOfPlaylistState>()
    val playlistInfo: LiveData<InfoOfPlaylistState> = _playlistInfo


    fun loadPlaylistData(playlistId: Long?) {
        viewModelScope.launch {
            try {
                if (playlistId != -1L) {
                    var playlist = playlistInteractor.getPlaylistIntr(playlistId)
                    _playlistInfo.postValue(InfoOfPlaylistState.Content(playlist))
                    val trackIdsString = playlist?.playlistName
                    Log.d("ViewModel", "trackIdsString: $trackIdsString")
                    libraryDbInteractor.getPlaylistTotalDurationIntr(trackIdsString.toString()).collect { totalDuration ->
                        Log.d("ViewModel", "totalDuration from interactor: $totalDuration")
                        _playlistInfo.postValue(InfoOfPlaylistState.Content(playlist?.copy(totalDuration = totalDuration)))
                    }
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Error loading playlist data", e)
                _playlistInfo.postValue(InfoOfPlaylistState.Error(e))
            }
        }
    }
}