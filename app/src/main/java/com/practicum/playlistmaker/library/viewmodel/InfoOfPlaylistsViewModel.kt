package com.practicum.playlistmaker.library.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.domain.api.intr.LibraryDbInteractor
import com.practicum.playlistmaker.library.domain.api.intr.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.models.FavoriteFragmentState
import com.practicum.playlistmaker.library.domain.models.InfoOfPlaylistState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class InfoOfPlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val libraryDbInteractor: LibraryDbInteractor
) : ViewModel() {

    private val _playlistInfo = MutableLiveData<InfoOfPlaylistState>()
    val playlistInfo: LiveData<InfoOfPlaylistState> = _playlistInfo

    private val _stateTrackData = MutableLiveData<FavoriteFragmentState>()
    val stateTrackData: LiveData<FavoriteFragmentState> = _stateTrackData


    fun loadPlaylistData(playlistId: Long?) {
        viewModelScope.launch {
            try {
                if (playlistId != -1L) {
                    var playlist = playlistInteractor.getPlaylistIntr(playlistId)
                    _playlistInfo.postValue(InfoOfPlaylistState.Content(playlist))
                    val trackIdsString = playlist?.playlistName
                    libraryDbInteractor.getPlaylistTotalDurationIntr(trackIdsString.toString())
                        .collect { totalDuration ->
                            _playlistInfo.postValue(
                                InfoOfPlaylistState.Content(
                                    playlist?.copy(
                                        totalDuration = totalDuration
                                    )
                                )
                            )
                        }
                }
            } catch (e: Exception) {
                _playlistInfo.postValue(InfoOfPlaylistState.Error(e))
            }
        }
    }

    fun fillTrackData(playlistId: Long?) {
        viewModelScope.launch {
            try {
                if (playlistId != -1L) {
                    var playlist = playlistInteractor.getPlaylistIntr(playlistId)
                    _playlistInfo.postValue(InfoOfPlaylistState.Content(playlist))
                    val trackIdsString = playlist?.playlistName
                    libraryDbInteractor.getTracksInPlaylistIntr(trackIdsString.toString())
                        .collect { tracks -> processResult(tracks) }
                }
            } catch (e: Exception) {
                _playlistInfo.postValue(InfoOfPlaylistState.Error(e))
            }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoriteFragmentState.Error(""))
        } else {
            renderState(FavoriteFragmentState.Content(tracks))
        }
    }

    private fun renderState(state: FavoriteFragmentState) {
        _stateTrackData.value = state
    }
}