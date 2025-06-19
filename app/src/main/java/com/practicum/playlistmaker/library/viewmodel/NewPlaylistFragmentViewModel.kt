package com.practicum.playlistmaker.library.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.api.intr.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.models.NewPlaylistFragmentState
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.ToastState
import kotlinx.coroutines.launch

class NewPlaylistFragmentViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _stateLiveData =
        MutableLiveData<NewPlaylistFragmentState>()
    val stateLiveData: LiveData<NewPlaylistFragmentState> = _stateLiveData

    private val _toastState = MutableLiveData<ToastState>(ToastState.None)
    val toastState: LiveData<ToastState> = _toastState

    init {
        _stateLiveData.value = NewPlaylistFragmentState() // инициализируем начальное состояние
    }

    fun editNamePL(namePL: String) {
        val isNameValid = namePL.isNotEmpty()
        val isSaveButtonEnabled = isNameValid
        val newState = _stateLiveData.value?.copy(
            namePL = namePL,
            isNameValid = isNameValid,
            isSaveButtinEnabled = isSaveButtonEnabled
        ) ?: return
        renderState(newState)
    }

    fun editDescriptionPL(descriptionPL: String) {
        val newState = _stateLiveData.value?.copy(descriptionPL = descriptionPL) ?: return
        renderState(newState)
    }

    fun editCoverPathPL(coverPathPL: String) {
        val newState = _stateLiveData.value?.copy(coverPathPL = coverPathPL) ?: return
        renderState(newState)
    }

    fun savePlaylist() {
        viewModelScope.launch {
            val currentState = _stateLiveData.value ?: return@launch // проверка состояния, если null, то сразу выходим из корутины
            try{
                val playlist = Playlist(
                    playlistName = currentState.namePL,
                    playlistDescription = currentState.descriptionPL,
                    playlistCoverPath = currentState.coverPathPL,
                    playlistId = null,
                    trackIdsJson = null,
                    trackCount = null
                )
                val newPlaylistId = playlistInteractor.insertPlaylistIntr(playlist) // получили id плейлиста, добавив в БД новый плейлист
                val newState = currentState.copy( saveResult = newPlaylistId, saveError = null) // сохранили id плейлиста
                renderState(newState)
                _toastState.postValue(ToastState.Show("Плейлист ${newState.namePL} создан"))
            } catch(e: Exception) {
                val newState = currentState.copy(saveResult = null, saveError = e)
                renderState(newState)
                _toastState.postValue(ToastState.Show("Ошибка при сохранении"))
            }
        }
    }

    fun toastWasShown() {
        _toastState.value = ToastState.None
    }

    private fun renderState(state: NewPlaylistFragmentState) {
        _stateLiveData.value = state
    }
}