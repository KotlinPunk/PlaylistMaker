package com.practicum.playlistmaker.library.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.api.intr.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.models.NewPlaylistFragmentState
import com.practicum.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.launch

open class NewPlaylistFragmentViewModel(private val playlistInteractor: PlaylistInteractor) :
    ViewModel() {

    internal val _stateLiveData =
        MutableLiveData<NewPlaylistFragmentState>()
    val stateLiveData: LiveData<NewPlaylistFragmentState> = _stateLiveData

    private var isEditing = false
    private var editingPlaylistId: Long? = null

    init {
        _stateLiveData.value = NewPlaylistFragmentState() // инициализируем начальное состояние
    }

    fun loadPlaylistForEditing(
        playlistId: Long?,
        name: String,
        description: String,
        coverPath: String?
    ) {
        Log.d(
            "NewPlaylistFragmentViewModel",
            "loadPlaylistForEditing: id=$playlistId, name=$name, description=$description"
        )
        isEditing = true
        editingPlaylistId = playlistId
        val newState = NewPlaylistFragmentState(
            namePL = name,
            descriptionPL = description,
            coverPathPL = coverPath ?: "",
            isNameValid = name.isNotEmpty(),
            isSaveButtinEnabled = name.isNotEmpty()
        )
        renderState(newState)
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

    open fun savePlaylist() {
        viewModelScope.launch {
            val currentState = _stateLiveData.value
                ?: return@launch // проверка состояния, если null, то сразу выходим из корутины
            try {
                if (isEditing) {
                    Log.d(
                        "NewPlaylistFragmentViewModel",
                        "Updating existing playlist: id=$editingPlaylistId"
                    )
                    Log.d(
                        "NewPlaylistFragmentViewModel",
                        "New data: name='${currentState.namePL}', description='${currentState.descriptionPL}', coverPath='${currentState.coverPathPL}'"
                    )
                    // Обновляем данные
                    playlistInteractor.editPlaylistIntr(
                        editingPlaylistId,
                        currentState.namePL,
                        currentState.descriptionPL,
                        currentState.coverPathPL.ifEmpty { null }
                    )
                    val newState = currentState.copy(
                        saveResult = editingPlaylistId,
                        saveError = null
                    )
                    renderState(newState)
                    android.util.Log.d(
                        "NewPlaylistFragmentViewModel",
                        "Playlist updated successfully"
                    )
                } else {
                    android.util.Log.d("NewPlaylistFragmentViewModel", "Creating new playlist")
                    // Create new playlist
                    val playlist = Playlist(
                        playlistId = null,
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
                }
            } catch (e: Exception) {
                Log.e("NewPlaylistFragmentViewModel", "Error saving playlist", e)
                val newState = currentState.copy(saveResult = null, saveError = e)
                renderState(newState)
            }
        }
    }

    fun isEditingMode(): Boolean = isEditing

    private fun renderState(state: NewPlaylistFragmentState) {
        _stateLiveData.value = state
    }
}
