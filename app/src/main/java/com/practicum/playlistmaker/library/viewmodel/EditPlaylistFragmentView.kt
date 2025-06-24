/*
package com.practicum.playlistmaker.library.viewmodel

import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.api.intr.LibraryDbInteractor
import com.practicum.playlistmaker.library.domain.api.intr.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.models.NewPlaylistFragmentState
import com.practicum.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.launch

class EditPlaylistFragmentViewModel(
    private val playlistInteractor: PlaylistInteractor) : NewPlaylistFragmentViewModel(playlistInteractor) {

    var currentPlaylistId = 0L
    var currentCoverPath = "" // URI изображения плейлиста. Хранит текущий URI, используется для обновления, если изображение меняется


    fun loadPlaylistData(playlistId: Long?) {
        viewModelScope.launch {
            if (playlistId != null && playlistId != -1L) { // Проверяем на null

                val playlist = playlistInteractor.getPlaylistIntr(playlistId)

                playlist?.let { pl -> // Используем let для обработки playlist, если он не null
                    currentPlaylistId = pl.playlistId ?: 0L // Используем elvis operator
                    currentCoverPath = pl.playlistCoverPath ?: "" // Используем elvis operator

                    val current = _stateLiveData.value ?: NewPlaylistFragmentState()
                    _stateLiveData.value = current.copy(
                        namePL = pl.playlistName ?: "", // Используем elvis operator
                        descriptionPL = pl.playlistDescription ?: "", // Используем elvis operator
                        coverPathPL = pl.playlistCoverPath.orEmpty(), // Или используем orEmpty()
                        isSaveButtinEnabled = true
                    )
                }
            }
        }
    }


    override fun savePlaylist() {
        viewModelScope.launch {
            val currentState = _stateLiveData.value
                ?: return@launch // проверка состояния, если null, то сразу выходим из корутины
            try {
                val playlist = Playlist(
                    playlistId = currentPlaylistId,
                    playlistName = currentState.namePL,
                    playlistDescription = currentState.descriptionPL,
                    playlistCoverPath = currentState.coverPathPL,
                    trackIds = null,
                    trackCount = null,
                    totalDuration = 0L
                )
                val newPlaylistId =
                    playlistInteractor.insertPlaylistIntr(playlist) // получили id плейлиста, добавив в БД новый плейлист
                val newState = currentState.copy(
                    saveResult = newPlaylistId,
                    saveError = null
                ) // сохранили id плейлиста
                renderState(newState)
            } catch (e: Exception) {
                val newState = currentState.copy(saveResult = null, saveError = e)
                renderState(newState)
            }
        }
    }

    private fun renderState(state: NewPlaylistFragmentState) {
        _stateLiveData.value = state
    }
}*/
