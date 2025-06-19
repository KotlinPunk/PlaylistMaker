package com.practicum.playlistmaker.library.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.domain.api.intr.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.library.domain.models.PlaylistFragmentState
import kotlinx.coroutines.launch

class PlaylistFragmentViewModel(
    private val context: Context,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _stateLiveData =
        MutableLiveData<PlaylistFragmentState>()
    val stateLiveData: LiveData<PlaylistFragmentState> = _stateLiveData

    init {
        fillData()
    }

    fun fillData() {
        viewModelScope.launch {
            playlistInteractor
                .getAllPlaylistsIntr()
                .collect { playlists -> processResult(playlists) }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(PlaylistFragmentState.Error(context.getString(R.string.empty_playlist)))
        } else {
            renderState(PlaylistFragmentState.Content(playlists))
        }
    }

    private fun renderState(state: PlaylistFragmentState) {
        _stateLiveData.value = state
    }
}